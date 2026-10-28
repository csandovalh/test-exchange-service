package pe.test.exchange.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotBlank(message = "La moneda de origen es obligatoria")
        String monedaOrigen,

        @NotBlank(message = "La moneda de destino es obligatoria")
        String monedaDestino
) {
}
