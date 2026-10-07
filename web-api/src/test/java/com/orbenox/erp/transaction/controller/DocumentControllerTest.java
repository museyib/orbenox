package com.orbenox.erp.transaction.controller;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.projection.DocumentData;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.projection.ProductLineItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DocumentControllerTest {

    @Test
    void getAll_shouldWrapRepositoryItemsInSuccessfulResponse() {
        DocumentRepository documents = mock(DocumentRepository.class);
        ProductLineRepository productLines = mock(ProductLineRepository.class);
        List<DocumentItem> items = List.of(mock(DocumentItem.class));
        when(documents.getAllItems()).thenReturn(items);
        DocumentController controller = new DocumentController(documents, productLines, mock(LocalizationService.class));

        var response = controller.getAll();

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isSameAs(items);
        verifyNoInteractions(productLines);
    }

    @Test
    void getById_shouldCombineDocumentItemAndLines() {
        DocumentRepository documents = mock(DocumentRepository.class);
        ProductLineRepository productLines = mock(ProductLineRepository.class);
        DocumentItem item = mock(DocumentItem.class);
        when(item.getId()).thenReturn(18L);
        ProductLineItem line = mock(ProductLineItem.class);
        List<ProductLineItem> lines = List.of(line);
        when(documents.getItemById(18L)).thenReturn(item);
        when(productLines.getItemsByDocumentId(18L)).thenReturn(lines);
        DocumentController controller = new DocumentController(documents, productLines, mock(LocalizationService.class));

        var response = controller.getById(18L);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        DocumentData data = response.getBody().getData();
        assertThat(data.documentItem()).isSameAs(item);
        assertThat(data.productLines()).isSameAs(lines);
    }

    @Test
    void getById_shouldReportLocalizedErrorWhenDocumentIsMissing() {
        DocumentRepository documents = mock(DocumentRepository.class);
        LocalizationService localization = mock(LocalizationService.class);
        when(localization.msg("error.document.notFound", 404L)).thenReturn("Document 404 not found");
        DocumentController controller = new DocumentController(documents, mock(ProductLineRepository.class), localization);

        assertThatThrownBy(() -> controller.getById(404L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document 404 not found");
    }
}
