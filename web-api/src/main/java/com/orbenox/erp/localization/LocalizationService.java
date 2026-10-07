package com.orbenox.erp.localization;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalizationService {
    private final MessageSource messageSource;

    public String msg(String code, Object... args) {
        var locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
