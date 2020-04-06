package luke.mehring.fridge.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import luke.mehring.fridge.exception.NoFridgesExcpetion;
import luke.mehring.fridge.exception.TooManyFridgesExcpetion;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.catalog.Catalog;
import java.io.IOException;
import java.sql.Ref;
import java.util.MissingResourceException;

public class MongoDatabase {

    private static final Logger logger = LoggerFactory.getLogger(MongoDatabase.class);

    private static final String DB_NAME = "refrigerator";
    private static final String COLLECTION_NAME = "refrigerator";

    private MongoClient mongo;
    private ObjectMapper mapper;

    public MongoDatabase() {

        mapper = new ObjectMapper();

        MongoClient mongo = new MongoClient( "localhost" , 12345 );
        logger.debug("Connected to MongoDB");
        initDB();
    }

    public void close() {
        try {
            logger.debug("Closing MongoDB object");
            mongo.close();
        } catch (Exception e) {
            logger.debug("Failed to close MongoDB object", e);
        }
    }

    public void initDB() {
        try {
            com.mongodb.client.MongoDatabase database = mongo.getDatabase(DB_NAME);
            logger.debug("Database Created");
            database.createCollection(COLLECTION_NAME);
            logger.debug("Collection Created");
        } catch (Exception e) {
            logger.trace("Error on startup, most likly nothing.", e);
        }
    }

    private MongoCollection<Document> getCollection() {
        return mongo.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
    }

    public Refrigerator getRefrigerator(String name) throws NoFridgesExcpetion, TooManyFridgesExcpetion, IOException {
       return mapper.readValue(getRefrigeratorInternal(name), Refrigerator.class);
    }

    private String getRefrigeratorInternal(String name) throws NoFridgesExcpetion, TooManyFridgesExcpetion {
        MongoCollection collection = getCollection();

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", "name");

        long size = collection.countDocuments(searchQuery);
        if (size > 1) {
            String msg = "Found multiple fridges with same name ("+name+")!";
            TooManyFridgesExcpetion exception = new TooManyFridgesExcpetion(msg);
            logger.warn(msg, exception);
            throw exception;
        } else if (size == 0) {
            String msg = "No fridges found with name ("+name+")!";
            NoFridgesExcpetion exception = new NoFridgesExcpetion(msg);
            logger.warn(msg, exception);
            throw exception;
        }

        FindIterable findIterable = collection.find(searchQuery);

        Object documentToReturn = findIterable.first();
        return documentToReturn.toString();
    }

    public Refrigerator createOrUpdate(Refrigerator fridge) throws TooManyFridgesExcpetion, IOException {
        Refrigerator fridgeDB = null;
        try {
            fridgeDB = getRefrigerator(fridge.getName());

//            fridgeDB.update(fridge);
        } catch (NoFridgesExcpetion e) {
            //If we get here, we know that we are adding
        } catch (TooManyFridgesExcpetion e) {
            //if we got here, something is wrong, throw error out
            throw e;
        }
        return fridgeDB;
    }
}
