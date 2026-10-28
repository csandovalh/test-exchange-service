package pe.test.exchange.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversionApplicationServiceTest {

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private ConversionRepository conversionRepository;

    @InjectMocks
    private ConversionApplicationService service;

    @Test
    void debeConvertirYGuardarCorrectamente() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"),
                "USD",
                "PEN"
        );

        when(exchangeRateProvider.obtenerTipoCambio("USD", "PEN"))
                .thenReturn(new BigDecimal("3.4486"));

        ConversionResponse response = service.convertir(request);

        assertEquals(
                0,
                new BigDecimal("344.86")
                        .compareTo(response.montoConvertido())
        );

        assertEquals(
                0,
                new BigDecimal("3.4486")
                        .compareTo(response.tipoCambio())
        );

        assertEquals("USD", response.monedaOrigen());
        assertEquals("PEN", response.monedaDestino());

        verify(conversionRepository, times(1))
                .guardar(any(Conversion.class));
    }

    @Test
    void debeRechazarMontoNegativoSinGuardar() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("-100"),
                "USD",
                "PEN"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.convertir(request)
        );

        verifyNoInteractions(
                exchangeRateProvider,
                conversionRepository
        );
    }

    @Test
    void debeRechazarMonedaInvalidaSinGuardar() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"),
                "INVALID",
                "PEN"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.convertir(request)
        );

        verifyNoInteractions(
                exchangeRateProvider,
                conversionRepository
        );
    }

    @Test
    void noDebeGuardarSiFallaElProveedor() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"),
                "USD",
                "PEN"
        );

        when(exchangeRateProvider.obtenerTipoCambio("USD", "PEN"))
                .thenThrow(new IllegalStateException(
                        "Proveedor no disponible"
                ));

        assertThrows(
                IllegalStateException.class,
                () -> service.convertir(request)
        );

        verify(conversionRepository, never())
                .guardar(any(Conversion.class));
    }

    @Test
    void debeConvertirMismaMonedaConTasaUno() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"),
                "USD",
                "USD"
        );

        ConversionResponse response = service.convertir(request);

        assertEquals(
                0,
                new BigDecimal("100.00")
                        .compareTo(response.montoConvertido())
        );

        assertEquals(
                0,
                BigDecimal.ONE.compareTo(response.tipoCambio())
        );

        verifyNoInteractions(exchangeRateProvider);

        verify(conversionRepository)
                .guardar(any(Conversion.class));
    }


    @Test
    void debeRedondearSegunLaMonedaDestino() {

        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100"),
                "USD",
                "JPY"
        );

        when(exchangeRateProvider.obtenerTipoCambio("USD", "JPY"))
                .thenReturn(new BigDecimal("150.567"));

        ConversionResponse response = service.convertir(request);

        assertEquals(
                0,
                new BigDecimal("15057")
                        .compareTo(response.montoConvertido())
        );

        verify(conversionRepository)
                .guardar(any(Conversion.class));
    }

}
