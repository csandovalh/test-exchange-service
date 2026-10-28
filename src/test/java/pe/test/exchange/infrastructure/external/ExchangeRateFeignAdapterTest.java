package pe.test.exchange.infrastructure.external;

import org.junit.jupiter.api.Test;
import pe.test.exchange.domain.exception.ExchangeRateNotAvailableException;
import pe.test.exchange.domain.exception.InvalidExchangeRateResponseException;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateFeignAdapterTest {

    private final ExchangeRateApiClient client =
            mock(ExchangeRateApiClient.class);

    private final ExchangeRateFeignAdapter adapter =
            new ExchangeRateFeignAdapter(client, "test-api-key");

    @Test
    void debeObtenerTipoCambioCorrectamente() {

        ExchangeRateApiResponse response =
                new ExchangeRateApiResponse(
                        "success",
                        "USD",
                        Map.of("PEN", new BigDecimal("3.45"))
                );

        when(client.getLatestRates("test-api-key", "USD"))
                .thenReturn(response);

        BigDecimal resultado =
                adapter.obtenerTipoCambio("USD", "PEN");

        assertEquals(
                0,
                new BigDecimal("3.45").compareTo(resultado)
        );
    }

    @Test
    void debeRechazarRespuestaNula() {

        when(client.getLatestRates("test-api-key", "USD"))
                .thenReturn(null);

        assertThrows(
                InvalidExchangeRateResponseException.class,
                () -> adapter.obtenerTipoCambio("USD", "PEN")
        );
    }

    @Test
    void debeRechazarRespuestaFallida() {

        ExchangeRateApiResponse response =
                new ExchangeRateApiResponse(
                        "error",
                        "USD",
                        Map.of()
                );

        when(client.getLatestRates("test-api-key", "USD"))
                .thenReturn(response);

        assertThrows(
                InvalidExchangeRateResponseException.class,
                () -> adapter.obtenerTipoCambio("USD", "PEN")
        );
    }

    @Test
    void debeRechazarTipoCambioInexistente() {

        ExchangeRateApiResponse response =
                new ExchangeRateApiResponse(
                        "success",
                        "USD",
                        Map.of("EUR", new BigDecimal("0.90"))
                );

        when(client.getLatestRates("test-api-key", "USD"))
                .thenReturn(response);

        assertThrows(
                ExchangeRateNotAvailableException.class,
                () -> adapter.obtenerTipoCambio("USD", "PEN")
        );
    }

    @Test
    void debeRechazarTipoCambioNoPositivo() {

        ExchangeRateApiResponse response =
                new ExchangeRateApiResponse(
                        "success",
                        "USD",
                        Map.of("PEN", BigDecimal.ZERO)
                );

        when(client.getLatestRates("test-api-key", "USD"))
                .thenReturn(response);

        assertThrows(
                ExchangeRateNotAvailableException.class,
                () -> adapter.obtenerTipoCambio("USD", "PEN")
        );
    }
}
