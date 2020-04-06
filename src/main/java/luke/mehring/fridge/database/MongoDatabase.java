package luke.mehring.fridge.database;

import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDatabase {

    private static final Logger logger = LoggerFactory.getLogger(MongoDatabase.class);

    private MongoClient mongo;

    public MongoDatabase() {

        mongo = new MongoClient( "localhost" , 12345 );
        logger.debug("Connected to MongoDB");

        com.mongodb.client.MongoDatabase database = getDatabase();
        logger.debug("Database Created");
        database.createCollection("refrigerator");
        logger.debug("Collection Created");
    }

    private com.mongodb.client.MongoDatabase getDatabase() {
        return mongo.getDatabase("refrigerator");
    }
}
