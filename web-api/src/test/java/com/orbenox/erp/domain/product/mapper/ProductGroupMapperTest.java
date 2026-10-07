package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductGroupMapperTest {
    @Mock
    EntityManager entityManager;
    @InjectMocks
    ProductGroupMapperImpl mapper;

    @Test
    void createMapsGroupFieldsAndOptionalParentReference() {
        ProductGroup parent = new ProductGroup();
        when(entityManager.find(ProductGroup.class, 7L)).thenReturn(parent);

        ProductGroup child = mapper.toEntity(
                new ProductGroupCreateDto(true, "TOOLS", "Tools", "Hardware", "tools", 7L));

        assertThat(child.isEnabled()).isTrue();
        assertThat(child.getCode()).isEqualTo("TOOLS");
        assertThat(child.getName()).isEqualTo("Tools");
        assertThat(child.getDescription()).isEqualTo("Hardware");
        assertThat(child.getSlug()).isEqualTo("tools");
        assertThat(child.getParent()).isSameAs(parent);

        ProductGroup root = mapper.toEntity(
                new ProductGroupCreateDto(true, "ROOT", "Root", null, "root", 0L));
        assertThat(root.getParent()).isNull();
    }

    @Test
    void updateMapsParentAndRetainsNullOptionalFields() {
        ProductGroup parent = new ProductGroup();
        ProductGroup existing = new ProductGroup();
        existing.setName("existing name");
        existing.setDescription("existing description");
        existing.setSlug("existing-slug");
        when(entityManager.find(ProductGroup.class, 8L)).thenReturn(parent);

        mapper.updateEntityFromDto(
                new ProductGroupUpdateDto(2L, false, "NEW", null, null, null, 8L), existing);

        assertThat(existing.getId()).isEqualTo(2L);
        assertThat(existing.isEnabled()).isFalse();
        assertThat(existing.getCode()).isEqualTo("NEW");
        assertThat(existing.getName()).isEqualTo("existing name");
        assertThat(existing.getDescription()).isEqualTo("existing description");
        assertThat(existing.getSlug()).isEqualTo("existing-slug");
        assertThat(existing.getParent()).isSameAs(parent);
    }
}
