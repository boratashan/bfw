package b.currencycrawler.amqpclient;

public class AmqpClientConfigurationException extends Exception {
    public AmqpClientConfigurationException(String message) {
        super(message);
    }

    public AmqpClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
