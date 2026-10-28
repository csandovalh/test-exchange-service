
package pe.test.exchange.application.service;

import org.springframework.stereotype.Service;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;
import pe.test.exchange.domain.service.CurrencyConversionDomainService;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

@Service
public class ConversionApplicationService {

    private final ExchangeRateProvider exchangeRateProvider;
    private final ConversionRepository conversionRepository;
    private final CurrencyConversionDomainService conversionDomainService;

    public ConversionApplicationService(
            ExchangeRateProvider exchangeRateProvider,
            ConversionRepository conversionRepository
    ) {
        this.exchangeRateProvider = exchangeRateProvider;
        this.conversionRepository = conversionRepository;
        this.conversionDomainService = new CurrencyConversionDomainService();
    }

    public ConversionResponse convertir(ConversionRequest request) {

        if (request == null || request.monto() == null
                || request.monto().signum() <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero"
            );
        }

        String origen = normalizarMoneda(request.monedaOrigen());
        String destino = normalizarMoneda(request.monedaDestino());

        BigDecimal tipoCambio = origen.equals(destino)
                ? BigDecimal.ONE
                : exchangeRateProvider.obtenerTipoCambio(origen, destino);

        BigDecimal montoConvertido =
                conversionDomainService.calcularMontoConvertido(
                        request.monto(),
                        tipoCambio,
                        destino
                );

        Conversion conversion = new Conversion(
                request.monto(),
                origen,
                destino,
                tipoCambio,
                montoConvertido,
                LocalDateTime.now()
        );

        conversionRepository.guardar(conversion);

        return new ConversionResponse(
                conversion.monto(),
                conversion.montoConvertido(),
                conversion.monedaOrigen(),
                conversion.monedaDestino(),
                conversion.tipoCambio()
        );
    }

    private String normalizarMoneda(String moneda) {

        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de moneda es obligatorio"
            );
        }

        String codigo = moneda.trim().toUpperCase(Locale.ROOT);

        try {
            Currency.getInstance(codigo);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Código de moneda inválido: " + codigo
            );
        }

        return codigo;
    }
}
