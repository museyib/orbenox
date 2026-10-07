package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.Currency;
import com.orbenox.erp.domain.unit.Unit;
import com.orbenox.erp.domain.unit.UnitCreateDto;
import com.orbenox.erp.domain.unit.UnitMapperImpl;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimension;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceAndUnitMapperTest {
    @Mock
    EntityManager entityManager;
    @InjectMocks
    PriceListMapperImpl priceListMapper;
    @InjectMocks
    UnitMapperImpl unitMapper;

    @Test
    void mapsPriceListScalarValuesCurrencyAndOptionalParent() {
        Currency currency = new Currency();
        PriceList parent = new PriceList();
        BigDecimal factor = new BigDecimal("1.75");
        when(entityManager.getReference(Currency.class, 1L)).thenReturn(currency);
        when(entityManager.getReference(PriceList.class, 2L)).thenReturn(parent);

        PriceList mapped = priceListMapper.toEntity(
                new PriceListCreateDto(true, "RETAIL", "Retail", 1L, factor, 2L, (short) 3));

        assertThat(mapped.isEnabled()).isTrue();
        assertThat(mapped.getCode()).isEqualTo("RETAIL");
        assertThat(mapped.getName()).isEqualTo("Retail");
        assertThat(mapped.getCurrency()).isSameAs(currency);
        assertThat(mapped.getParent()).isSameAs(parent);
        assertThat(mapped.getFactorToParent()).isEqualByComparingTo(factor);
        assertThat(mapped.getRoundLength()).isEqualTo((short) 3);

        PriceList root = priceListMapper.toEntity(
                new PriceListCreateDto(false, "BASE", "Base", 1L, BigDecimal.ONE, 0L, (short) 2));
        assertThat(root.getParent()).isNull();
    }

    @Test
    void updatesPriceListUsingReferencesAndRetainsNullFields() {
        Currency currency = new Currency();
        PriceList parent = new PriceList();
        PriceList existing = new PriceList();
        existing.setName("previous");
        existing.setParent(new PriceList());
        when(entityManager.getReference(Currency.class, 3L)).thenReturn(currency);
        when(entityManager.getReference(PriceList.class, 4L)).thenReturn(parent);

        priceListMapper.updateEntityFromDto(
                new PriceListUpdateDto(7L, true, "NEW", null, 3L, null, 4L, null), existing);

        assertThat(existing.getId()).isEqualTo(7L);
        assertThat(existing.isEnabled()).isTrue();
        assertThat(existing.getCode()).isEqualTo("NEW");
        assertThat(existing.getName()).isEqualTo("previous");
        assertThat(existing.getCurrency()).isSameAs(currency);
        assertThat(existing.getParent()).isSameAs(parent);
    }

    @Test
    void unitMapperMapsDimensionReferenceAndConversionValues() {
        UnitDimension dimension = new UnitDimension();
        BigDecimal factor = new BigDecimal("1000");
        BigDecimal offset = new BigDecimal("0.5");
        when(entityManager.getReference(UnitDimension.class, 5L)).thenReturn(dimension);

        Unit unit = unitMapper.toEntity(
                new UnitCreateDto(true, "G", "Gram", 5L, false, factor, offset));

        assertThat(unit.isEnabled()).isTrue();
        assertThat(unit.getCode()).isEqualTo("G");
        assertThat(unit.getName()).isEqualTo("Gram");
        assertThat(unit.getUnitDimension()).isSameAs(dimension);
        assertThat(unit.isBase()).isFalse();
        assertThat(unit.getFactorToBase()).isEqualByComparingTo(factor);
        assertThat(unit.getOffsetToBase()).isEqualByComparingTo(offset);

        Unit root = unitMapper.toEntity(new UnitCreateDto(true, "BASE", "Base", 0L, true,
                BigDecimal.ONE, BigDecimal.ZERO));
        assertThat(root.getUnitDimension()).isNull();
    }

    @Test
    void unitUpdateMapsForeignKeyAndKeepsUnspecifiedNullableFields() {
        UnitDimension dimension = new UnitDimension();
        Unit existing = new Unit();
        existing.setName("previous name");
        existing.setOffsetToBase(new BigDecimal("4"));
        when(entityManager.getReference(UnitDimension.class, 6L)).thenReturn(dimension);

        unitMapper.updateEntityFromDto(
                new UnitUpdateDto(8L, true, "NEW", null, 6L, true, null, null), existing);

        assertThat(existing.getId()).isEqualTo(8L);
        assertThat(existing.isEnabled()).isTrue();
        assertThat(existing.getCode()).isEqualTo("NEW");
        assertThat(existing.getName()).isEqualTo("previous name");
        assertThat(existing.getUnitDimension()).isSameAs(dimension);
        assertThat(existing.getOffsetToBase()).isEqualByComparingTo("4");
        verify(entityManager, never()).getReference(UnitDimension.class, 0L);
    }
}
