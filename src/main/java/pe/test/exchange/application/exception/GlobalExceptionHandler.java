package pe.test.exchange.application.exception;

import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.test.exchange.domain.exception.ExchangeRateNotAvailableException;
import pe.test.exchange.domain.exception.InvalidExchangeRateResponseException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Solicitud inválida");

        return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(
            RetryableException ex
    ) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de tipo de cambio no está disponible"
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignError(
            FeignException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "Error al consultar el proveedor de tipo de cambio"
        );
    }

    @ExceptionHandler(InvalidExchangeRateResponseException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidProviderResponse(
            InvalidExchangeRateResponseException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(
            Exception ex
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno"
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String mensaje
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ExchangeRateNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleExchangeRateNotAvailable(
            ExchangeRateNotAvailableException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );
    }
}
