package com.orbenox.erp.localization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocalizationServiceTest {

    @AfterEach
    void clearLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void msg_shouldResolveMessageUsingCurrentLocaleAndArguments() {
        MessageSource messageSource = mock(MessageSource.class);
        Locale locale = Locale.CANADA_FRENCH;
        LocaleContextHolder.setLocale(locale);
        when(messageSource.getMessage("error.document.notFound", new Object[]{42L}, locale))
                .thenReturn("Document 42 introuvable");

        String message = new LocalizationService(messageSource).msg("error.document.notFound", 42L);

        assertThat(message).isEqualTo("Document 42 introuvable");
        verify(messageSource).getMessage("error.document.notFound", new Object[]{42L}, locale);
    }
}
