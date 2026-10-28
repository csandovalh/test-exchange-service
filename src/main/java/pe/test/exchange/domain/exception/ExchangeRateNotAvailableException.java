package pe.test.exchange.domain.exception;

public class ExchangeRateNotAvailableException
        extends RuntimeException {

    public ExchangeRateNotAvailableException(String message) {
        super(message);
    }
}
