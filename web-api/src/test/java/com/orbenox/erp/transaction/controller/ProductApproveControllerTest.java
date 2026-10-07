package com.orbenox.erp.transaction.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateProductApproveCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.ProductApproveActionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductApproveControllerTest {
    private final ProductApproveActionService actions = mock(ProductApproveActionService.class);
    private final DocumentRepository documents = mock(DocumentRepository.class);
    private final LocalizationService localization = mock(LocalizationService.class);
    private final ProductApproveController controller =
            new ProductApproveController(actions, documents, localization);

    private static void assertEndpointResponse(
            org.springframework.http.ResponseEntity<Response<DocumentItem>> response,
            DocumentItem expectedItem) {
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), expectedItem);
    }

    private static <T> void assertSuccessWithData(Response<T> body, T expectedItem) {
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getCode()).isEqualTo(200);
        assertThat(body.getData()).isSameAs(expectedItem);
    }

    @Test
    void getAll_shouldReturnProductApprovalsWithSuccessStatusAndEnvelope() {
        List<DocumentItem> items = List.of(mock(DocumentItem.class));
        when(documents.getItemsByType(1L)).thenReturn(items);

        var response = controller.getAll();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), items);
        verify(documents).getItemsByType(1L);
    }

    @Test
    void getById_shouldReturnTypeFilteredItemWithSuccessStatusAndEnvelope() {
        DocumentItem item = mock(DocumentItem.class);
        when(documents.getItemByIdAndType(17L, 1L)).thenReturn(item);

        var response = controller.getById(17L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), item);
        verify(documents).getItemByIdAndType(17L, 1L);
    }

    @Test
    void create_shouldDelegateCommandAndReturnCreatedDocumentItem() {
        CreateProductApproveCommand command = new CreateProductApproveCommand(
                null, "initial stock", null, null, 3L, List.of());
        Document document = mock(Document.class);
        DocumentItem item = mock(DocumentItem.class);
        when(document.getId()).thenReturn(17L);
        when(actions.createDraft(command)).thenReturn(document);
        when(documents.getItemByIdAndType(17L, 1L)).thenReturn(item);

        var response = controller.create(command);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), item);
        verify(actions).createDraft(command);
        verify(documents).getItemByIdAndType(17L, 1L);
    }

    @Test
    void workflowEndpoints_shouldReturnUpdatedItemAndDelegateEachAction() {
        DocumentItem item = mock(DocumentItem.class);
        when(documents.getItemByIdAndType(17L, 1L)).thenReturn(item);

        assertEndpointResponse(controller.submit(17L), item);
        assertEndpointResponse(controller.approve(17L), item);
        assertEndpointResponse(controller.post(17L), item);
        assertEndpointResponse(controller.reject(17L), item);
        assertEndpointResponse(controller.close(17L), item);
        assertEndpointResponse(controller.cancel(17L), item);

        verify(actions).submit(17L);
        verify(actions).approve(17L);
        verify(actions).post(17L);
        verify(actions).reject(17L);
        verify(actions).close(17L);
        verify(actions).cancel(17L);
        verify(documents, times(6)).getItemByIdAndType(17L, 1L);
    }
}
