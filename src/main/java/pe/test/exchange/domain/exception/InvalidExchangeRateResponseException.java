
package pe.test.exchange.domain.exception;

public class InvalidExchangeRateResponseException
        extends RuntimeException {

    public InvalidExchangeRateResponseException(String message) {
        super(message);
    }
}
