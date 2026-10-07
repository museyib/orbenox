package com.orbenox.erp.domain.country;

import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CountryControllerTest {

    private final CountryService countryService = mock(CountryService.class);
    private final LocalizationService localizationService = mock(LocalizationService.class);
    private final CountryController controller = new CountryController(countryService, localizationService);

    @Test
    void getActions_shouldReturnSliceContentAndNavigationHeaders() {
        CountryItem country = mock(CountryItem.class);
        when(countryService.getAllItems(2, 5, "can")).thenReturn(new SliceImpl<>(
                List.of(country),
                PageRequest.of(2, 5),
                true));

        var response = controller.getActions(2, 5, "can");

        assertThat(response.getBody().getData()).containsExactly(country);
        assertThat(response.getBody().getHeaders())
                .containsEntry("hasNext", true)
                .containsEntry("hasPrev", true);
    }

    @Test
    void getById_shouldReturnServiceItem() {
        CountryItem country = mock(CountryItem.class);
        when(countryService.getItemById(7L)).thenReturn(country);

        assertThat(controller.getById(7L).getBody().getData()).isSameAs(country);
    }

    @Test
    void create_shouldReturnCreatedItem() {
        CountryItem country = mock(CountryItem.class);
        when(countryService.create(null)).thenReturn(country);

        assertThat(controller.create(null).getBody().getData()).isSameAs(country);
    }

    @Test
    void update_shouldReturnUpdatedItem() {
        CountryItem country = mock(CountryItem.class);
        when(countryService.update(7L, null)).thenReturn(country);

        assertThat(controller.update(7L, null).getBody().getData()).isSameAs(country);
    }

    @Test
    void delete_shouldSoftDeleteAndReturnLocalizedMessage() {
        when(localizationService.msg("country.deleted", 7L)).thenReturn("Country deleted");

        var response = controller.delete(7L);

        verify(countryService).softDelete(7L);
        assertThat(response.getBody().getMessage()).isEqualTo("Country deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("country.deleted");
    }
}
