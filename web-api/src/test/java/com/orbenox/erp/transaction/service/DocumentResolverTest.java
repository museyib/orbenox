package com.orbenox.erp.transaction.service;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DocumentResolverTest {

    @Test
    void resolve_shouldReturnDocumentWhenIdAndTypeMatch() {
        DocumentRepository repository = mock(DocumentRepository.class);
        LocalizationService localization = mock(LocalizationService.class);
        Document document = new Document();
        when(repository.findByIdAndTypeCode(21L, "SALES_ORDER")).thenReturn(Optional.of(document));
        DocumentResolver resolver = new DocumentResolver(repository, localization);

        assertThat(resolver.resolve(21L, "SALES_ORDER")).isSameAs(document);
        verify(repository).findByIdAndTypeCode(21L, "SALES_ORDER");
        verifyNoInteractions(localization);
    }

    @Test
    void resolve_shouldThrowLocalizedBusinessRuleWhenNoMatchingDocumentExists() {
        DocumentRepository repository = mock(DocumentRepository.class);
        LocalizationService localization = mock(LocalizationService.class);
        when(repository.findByIdAndTypeCode(21L, "SALES_ORDER")).thenReturn(Optional.empty());
        when(localization.msg("error.document.invalidIdForSpecifiedType", 21L))
                .thenReturn("Document 21 is not a sales order");
        DocumentResolver resolver = new DocumentResolver(repository, localization);

        assertThatThrownBy(() -> resolver.resolve(21L, "SALES_ORDER"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Document 21 is not a sales order");
    }

    @Test
    void resolveOptional_shouldPreserveEmptyRepositoryResult() {
        DocumentRepository repository = mock(DocumentRepository.class);
        LocalizationService localization = mock(LocalizationService.class);
        when(repository.findByIdAndTypeCode(5L, "PRODUCT_APPROVE")).thenReturn(Optional.empty());

        assertThat(new DocumentResolver(repository, localization)
                .resolveOptional(5L, "PRODUCT_APPROVE")).isEmpty();
        verifyNoInteractions(localization);
    }
}
