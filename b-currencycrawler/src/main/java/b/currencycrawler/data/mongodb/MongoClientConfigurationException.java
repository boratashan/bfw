package b.currencycrawler.data.mongodb;

public class MongoClientConfigurationException extends Exception {
    public MongoClientConfigurationException(String message) {
        super(message);
    }

    public MongoClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
