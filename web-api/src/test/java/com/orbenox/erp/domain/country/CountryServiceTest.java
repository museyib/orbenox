package com.orbenox.erp.domain.country;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Country Service Tests")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    private CountryItem countryItem;
    private Country country;

    @BeforeEach
    void setUp() {
        countryItem = mock(CountryItem.class);
        country = new Country();
        country.setId(1L);
        country.setCode("US");
        country.setName("United States");
    }

    @Test
    @DisplayName("Should return all country items")
    void getAllItems_ShouldReturnAllCountries() {
        // Given
        List<CountryItem> expected = List.of(countryItem);
        when(countryRepository.getAllItems(PageRequest.of(0, 10)).getContent()).thenReturn(expected);

        // When
        List<CountryItem> actual = countryService.getAllItems(0, 10, "").getContent();

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(1);
        verify(countryRepository, times(1)).getAllItems(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should return country by ID")
    void getItemById_ShouldReturnCountry() {
        // Given
        Long countryId = 1L;
        when(countryRepository.getItemById(countryId)).thenReturn(countryItem);

        // When
        CountryItem actual = countryService.getItemById(countryId);

        // Then
        assertThat(actual).isNotNull();
        verify(countryRepository, times(1)).getItemById(countryId);
    }

    @Test
    @DisplayName("Should create country successfully")
    void create_ShouldCreateCountry() {
        // Given
        CountryCreateDto dto = new CountryCreateDto(true, "US", "United States");
        when(countryMapper.toEntity(dto)).thenReturn(country);
        when(countryRepository.save(any(Country.class))).thenReturn(country);
        when(countryRepository.getItemById(country.getId())).thenReturn(countryItem);

        // When
        CountryItem actual = countryService.create(dto);

        // Then
        assertThat(actual).isNotNull();
        verify(countryRepository, times(1)).save(country);
    }

    @Test
    @DisplayName("Should soft delete country")
    void softDelete_ShouldMarkCountryAsDeleted() {
        // Given
        Long countryId = 1L;
        when(countryRepository.findByIdAndDeletedFalse(countryId)).thenReturn(country);

        // When
        countryService.softDelete(countryId);

        // Then
        assertThat(country.isDeleted()).isTrue();
        verify(countryRepository, times(1)).findByIdAndDeletedFalse(countryId);
    }
}
