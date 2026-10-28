package pe.test.exchange.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Conversion(
        BigDecimal monto,
        String monedaOrigen,
        String monedaDestino,
        BigDecimal tipoCambio,
        BigDecimal montoConvertido,
        LocalDateTime fechaConversion
) {
}
