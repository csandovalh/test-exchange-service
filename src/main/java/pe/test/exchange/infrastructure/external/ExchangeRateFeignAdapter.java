
package pe.test.exchange.infrastructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.test.exchange.domain.exception.ExchangeRateNotAvailableException;
import pe.test.exchange.domain.exception.InvalidExchangeRateResponseException;
import pe.test.exchange.domain.service.ExchangeRateProvider;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;

@Component
public class ExchangeRateFeignAdapter implements ExchangeRateProvider {

    private final ExchangeRateApiClient client;
    private final String apiKey;

    public ExchangeRateFeignAdapter(
            ExchangeRateApiClient client,
            @Value("${exchange-rate.api.key}") String apiKey
    ) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public BigDecimal obtenerTipoCambio(
            String monedaOrigen,
            String monedaDestino
    ) {

        ExchangeRateApiResponse response =
                client.getLatestRates(apiKey, monedaOrigen);

        if (response == null
                || !"success".equalsIgnoreCase(response.result())
                || response.conversionRates() == null) {

            throw new InvalidExchangeRateResponseException(
                    "El proveedor devolvió una respuesta inválida"
            );
        }

        BigDecimal tipoCambio =
                response.conversionRates().get(monedaDestino);

        if (tipoCambio == null || tipoCambio.signum() <= 0) {

            throw new ExchangeRateNotAvailableException(
                    "No existe un tipo de cambio disponible para la moneda destino: "
                            + monedaDestino
            );
        }

        return tipoCambio;
    }
}
