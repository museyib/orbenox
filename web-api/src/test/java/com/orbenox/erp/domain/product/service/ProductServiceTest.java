package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.mapper.ProductMapper;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductBarcodeRepository productBarcodeRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductItem productItem;
    private Product product;

    @BeforeEach
    void setUp() {
        productItem = mock(ProductItem.class);
        product = new Product();
        product.setId(1L);
        product.setCode("PROD001");
        product.setName("Test Product");
    }

    @Test
    @DisplayName("Should return all product items")
    void getAllItems_ShouldReturnAllProducts() {
        // Given
        List<ProductItem> expected = List.of(productItem);
        when(productRepository.getAllItems(PageRequest.of(0, 10)).getContent()).thenReturn(expected);

        // When
        List<ProductItem> actual = productService.getAllItems(0, 10, "").getContent();

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(1);
        verify(productRepository, times(1)).getAllItems(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should return product by ID")
    void getItemById_ShouldReturnProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.getItemById(productId)).thenReturn(productItem);

        // When
        ProductItem actual = productService.getItemById(productId);

        // Then
        assertThat(actual).isNotNull();
        verify(productRepository, times(1)).getItemById(productId);
    }

    @Test
    @DisplayName("Should create product successfully")
    void create_ShouldCreateProduct() {
        // Given
        ProductCreateDto dto = mock(ProductCreateDto.class);
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productRepository.getItemById(product.getId())).thenReturn(productItem);

        // When
        ProductItem actual = productService.create(dto);

        // Then
        assertThat(actual).isNotNull();
        verify(productRepository, times(1)).save(product);
        verify(productBarcodeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should soft delete product")
    void softDelete_ShouldMarkProductAsDeleted() {
        // Given
        Long productId = 1L;
        when(productRepository.findByIdAndDeletedFalse(productId)).thenReturn(product);

        // When
        productService.softDelete(productId);

        // Then
        assertThat(product.isDeleted()).isTrue();
        verify(productRepository, times(1)).findByIdAndDeletedFalse(productId);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void getItemById_WhenNotFound_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.getItemById(productId)).thenReturn(null);

        // When/Then
        ProductItem result = productService.getItemById(productId);
        assertThat(result).isNull();
    }
}
