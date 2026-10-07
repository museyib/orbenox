package com.orbenox.erp.transaction.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateSalesOrderCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.SalesOrderActionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SalesOrderControllerTest {
    private final SalesOrderActionService actions = mock(SalesOrderActionService.class);
    private final DocumentRepository documents = mock(DocumentRepository.class);
    private final LocalizationService localization = mock(LocalizationService.class);
    private final SalesOrderController controller = new SalesOrderController(actions, documents, localization);

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
    void getAll_shouldReturnSalesOrderListWithSuccessStatusAndEnvelope() {
        List<DocumentItem> items = List.of(mock(DocumentItem.class), mock(DocumentItem.class));
        when(documents.getItemsByType(2L)).thenReturn(items);

        var response = controller.getAll();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), items);
        verify(documents).getItemsByType(2L);
    }

    @Test
    void getById_shouldReturnTypeFilteredItemWithSuccessStatusAndEnvelope() {
        DocumentItem item = mock(DocumentItem.class);
        when(documents.getItemByIdAndType(31L, 2L)).thenReturn(item);

        var response = controller.getById(31L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), item);
        verify(documents).getItemByIdAndType(31L, 2L);
    }

    @Test
    void create_shouldDelegateCommandAndReturnCreatedDocumentItem() {
        CreateSalesOrderCommand command = new CreateSalesOrderCommand(
                null, "sale", 8L, null, 4L, 3L, List.of());
        Document document = mock(Document.class);
        DocumentItem item = mock(DocumentItem.class);
        when(document.getId()).thenReturn(31L);
        when(actions.createDraft(command)).thenReturn(document);
        when(documents.getItemByIdAndType(31L, 2L)).thenReturn(item);

        var response = controller.create(command);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertSuccessWithData(response.getBody(), item);
        verify(actions).createDraft(command);
        verify(documents).getItemByIdAndType(31L, 2L);
    }

    @Test
    void workflowEndpoints_shouldReturnUpdatedItemAndDelegateEachAction() {
        DocumentItem item = mock(DocumentItem.class);
        when(documents.getItemByIdAndType(31L, 2L)).thenReturn(item);

        assertEndpointResponse(controller.submit(31L), item);
        assertEndpointResponse(controller.approve(31L), item);
        assertEndpointResponse(controller.post(31L), item);
        assertEndpointResponse(controller.reject(31L), item);
        assertEndpointResponse(controller.close(31L), item);
        assertEndpointResponse(controller.cancel(31L), item);

        verify(actions).submit(31L);
        verify(actions).approve(31L);
        verify(actions).post(31L);
        verify(actions).reject(31L);
        verify(actions).close(31L);
        verify(actions).cancel(31L);
        verify(documents, times(6)).getItemByIdAndType(31L, 2L);
    }
}
