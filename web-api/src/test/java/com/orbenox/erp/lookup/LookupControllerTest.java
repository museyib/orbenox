package com.orbenox.erp.lookup;

import com.orbenox.erp.common.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LookupControllerTest {

    @Test
    void getLookups_shouldWrapServiceResultInSuccessfulResponse() {
        LookupService service = mock(LookupService.class);
        LookupController controller = new LookupController(service);
        List<String> requested = List.of("brands");
        Map<String, Object> lookups = Map.of("brands", List.of("Acme"));
        when(service.getLookups(requested)).thenReturn(lookups);

        var response = controller.getLookups(requested);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        Response<Map<String, Object>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isSameAs(lookups);
        verify(service).getLookups(requested);
    }
}
