package com.orbenox.erp.idempotency;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IdempotencyAspectTest {
    private final IdempotencyExecutor executor = mock(IdempotencyExecutor.class);
    private final IdempotencyAspect aspect = new IdempotencyAspect(executor, JsonMapper.builder().build());
    private final Idempotent annotation = mock(Idempotent.class);

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void handleIdempotency_shouldProceedWithoutRequestContextOrWhenKeyIsBlank() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("direct");
        when(annotation.headerName()).thenReturn("Idempotency-Key");

        assertThat(aspect.handleIdempotency(joinPoint, annotation)).isEqualTo("direct");
        verifyNoInteractions(executor);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "   ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(aspect.handleIdempotency(joinPoint, annotation)).isEqualTo("direct");
        verify(joinPoint, times(2)).proceed();
        verifyNoInteractions(executor);
    }

    @Test
    void handleIdempotency_shouldFingerprintBodyAndPrefixTheKeyWithItsTag() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "request-1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(annotation.headerName()).thenReturn("Idempotency-Key");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new SampleBody("abc")});
        when(joinPoint.proceed()).thenReturn("result");
        when(executor.execute(eq("invoice:request-1"), anyString(), any())).thenAnswer(invocation ->
                ((org.springframework.util.function.ThrowingSupplier<?>) invocation.getArgument(2)).get());

        assertThat(aspect.handleIdempotency(joinPoint, annotation)).isEqualTo("result");

        verify(executor).execute(eq("invoice:request-1"), matches("[0-9a-f]{64}"), any());
        verify(joinPoint).proceed();
    }

    @Test
    void handleIdempotency_shouldWrapCheckedProceedFailures() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "request-2");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(annotation.headerName()).thenReturn("Idempotency-Key");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenThrow(new Exception("downstream failed"));
        when(executor.execute(eq("request-2"), eq(""), any())).thenAnswer(invocation ->
                ((org.springframework.util.function.ThrowingSupplier<?>) invocation.getArgument(2)).get());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> aspect.handleIdempotency(joinPoint, annotation))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(Exception.class);
    }

    @Test
    void handleIdempotency_shouldRethrowRuntimeProceedFailuresUnchanged() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "request-3");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(annotation.headerName()).thenReturn("Idempotency-Key");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        IllegalArgumentException failure = new IllegalArgumentException("bad request");
        when(joinPoint.proceed()).thenThrow(failure);
        when(executor.execute(eq("request-3"), eq(""), any())).thenAnswer(invocation ->
                ((org.springframework.util.function.ThrowingSupplier<?>) invocation.getArgument(2)).get());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> aspect.handleIdempotency(joinPoint, annotation))
                .isSameAs(failure);
    }

    private record SampleBody(String value) implements Fingerprintable {
        @Override
        public String tag() {
            return "invoice";
        }
    }
}
