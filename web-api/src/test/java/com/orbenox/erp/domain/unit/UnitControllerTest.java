package com.orbenox.erp.domain.unit;

import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {
    @Mock
    private UnitService unitService;
    @Mock
    private UnitConverterService unitConverterService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private UnitController controller;

    @Test
    void listsUnitsAndReturnsNavigationHeaders() {
        UnitItem item = mock(UnitItem.class);
        Slice<UnitItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(1, 4), false);
        when(unitService.getAllItems(1, 4, "mass")).thenReturn(slice);

        var response = controller.getAll(1, 4, "mass");

        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", false).containsEntry("hasPrev", true);
        verify(unitService).getAllItems(1, 4, "mass");
    }

    @Test
    void itemDimensionCreateAndUpdateEndpointsReturnDelegatedResults() {
        UnitItem item = mock(UnitItem.class);
        UnitCreateDto createDto = mock(UnitCreateDto.class);
        UnitUpdateDto updateDto = mock(UnitUpdateDto.class);
        when(unitService.getItemById(7L)).thenReturn(item);
        when(unitService.findAllByDimensionId(8L)).thenReturn(List.of(item));
        when(unitService.create(createDto)).thenReturn(item);
        when(unitService.update(7L, updateDto)).thenReturn(item);

        assertThat(controller.getItemById(7L).getBody().getData()).isSameAs(item);
        assertThat(controller.getAllByDimensionId(8L).getBody().getData()).containsExactly(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(7L, updateDto).getBody().getData()).isSameAs(item);
        verify(unitService).findAllByDimensionId(8L);
        verify(unitService).update(7L, updateDto);
    }

    @Test
    void convertReturnsConverterValue() {
        UnitConversionRequest request = new UnitConversionRequest();
        request.setValue(new BigDecimal("10"));
        UnitUpdateDto from = mock(UnitUpdateDto.class);
        UnitUpdateDto to = mock(UnitUpdateDto.class);
        request.setFrom(from);
        request.setTo(to);
        BigDecimal result = new BigDecimal("100");
        when(unitConverterService.convert(request.getValue(), from, to)).thenReturn(result);

        assertThat(controller.convert(request).getBody().getData()).isEqualByComparingTo(result);
        verify(unitConverterService).convert(request.getValue(), request.getFrom(), request.getTo());
    }

    @Test
    void deleteSoftDeletesUnitAndReturnsLocalizedMessage() {
        when(i18n.msg("unit.deleted", 7L)).thenReturn("Unit deleted");

        var response = controller.delete(7L);

        verify(unitService).softDelete(7L);
        assertThat(response.getBody().getMessage()).isEqualTo("Unit deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("uom.deleted");
    }
}
