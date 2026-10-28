package pe.test.exchange.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionDomainService {

    public BigDecimal calcularMontoConvertido(
            BigDecimal monto,
            BigDecimal tipoCambio,
            String monedaDestino
    ) {
        int decimales = Currency.getInstance(monedaDestino)
                .getDefaultFractionDigits();

        if (decimales < 0) {
            decimales = 2;
        }

        return monto.multiply(tipoCambio)
                .setScale(decimales, RoundingMode.HALF_UP);
    }
}
