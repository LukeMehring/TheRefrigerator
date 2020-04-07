package luke.mehring.fridge.database;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import luke.mehring.fridge.exception.NoFridgesExcpetion;
import luke.mehring.fridge.exception.RefrigeratorValidationException;
import luke.mehring.fridge.exception.TooManyFridgesExcpetion;
import luke.mehring.fridge.validation.RefrigeratorItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MongoDatabase {

    private static final Logger logger = LoggerFactory.getLogger(MongoDatabase.class);

    private static final String DB_NAME = "refrigerator";
    private static final String COLLECTION_NAME = "refrigerator";

    private MongoClient mongo;

    public MongoDatabase() {
        String hostname = System.getenv("RATPACK_MONGO_HOSTNAME");

        if (hostname == null) {
            hostname = "localhost";
        }

        mongo = new MongoClient( hostname , 27017 );
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

    private MongoCollection<BasicDBObject> getCollection() {
        return mongo.getDatabase(DB_NAME).getCollection(COLLECTION_NAME, BasicDBObject.class);
    }

    public List<Refrigerator> getAll() {

        MongoCollection collection = getCollection();
        FindIterable<BasicDBObject> findIterable = collection.find();
        List<Refrigerator> result = new ArrayList<>();
        for (BasicDBObject obj : findIterable) {
            result.add(new Refrigerator(obj));
        }
        return result;
    }

    public Refrigerator getRefrigerator(String name) throws NoFridgesExcpetion, TooManyFridgesExcpetion {
        MongoCollection collection = getCollection();

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", name);

        long size = collection.countDocuments(searchQuery);
        if (size > 1) {
            String msg = "Found multiple fridges with same name ("+name+")!";
            TooManyFridgesExcpetion exception = new TooManyFridgesExcpetion(msg);
            throw exception;
        } else if (size == 0) {
            String msg = "No fridges found with name ("+name+")!";
            NoFridgesExcpetion exception = new NoFridgesExcpetion(msg);
            throw exception;
        }

        FindIterable<BasicDBObject> findIterable = collection.find(searchQuery);

        BasicDBObject documentToReturn = findIterable.first();
        return new Refrigerator(documentToReturn);
    }

    public int deleteRefrigerator(String name) throws NoFridgesExcpetion, TooManyFridgesExcpetion {
        MongoCollection collection = getCollection();

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", name);

        long size = collection.countDocuments(searchQuery);
        if (size > 1) {
            String msg = "Found multiple fridges with same name ("+name+")!";
            TooManyFridgesExcpetion exception = new TooManyFridgesExcpetion(msg);
            throw exception;
        } else if (size == 1) {
            collection.deleteOne(searchQuery);
            return 1;
        } else {
            return 0;
        }
    }

    public Refrigerator createOrUpdate(Refrigerator fridge) throws TooManyFridgesExcpetion, IOException, NoFridgesExcpetion, RefrigeratorValidationException {
        Refrigerator fridgeDB = null;
        try {
            fridgeDB = getRefrigerator(fridge.getName());
        } catch (NoFridgesExcpetion e) {
            //If we get here, we know that we are adding
            return create(fridge);
        } catch (TooManyFridgesExcpetion e) {
            //if we got here, something is wrong, throw error out
            logger.warn("Too many fridges are present with name " + fridge.getName() + " , something is wrong", e);
            throw e;
        }

        //If we get to here, we know no NoFridgesExcpetion or TooManyFridgesExcpetion so its update
        fridgeDB.update(fridge);

        return update(fridgeDB);
    }

    private Refrigerator create(Refrigerator fridge) throws IOException, TooManyFridgesExcpetion, NoFridgesExcpetion, RefrigeratorValidationException {
        MongoCollection collection = getCollection();

        //Validate before saving
        RefrigeratorItemValidator.validate(fridge);

        collection.insertOne(fridge.getDBDocument());

        return getRefrigerator(fridge.getName());
    }

    private Refrigerator update(Refrigerator fridge) throws IOException, TooManyFridgesExcpetion, NoFridgesExcpetion, RefrigeratorValidationException {
        MongoCollection collection = getCollection();

        //Validate before saving
        RefrigeratorItemValidator.validate(fridge);

        BasicDBObject query = new BasicDBObject();
        query.put("name", fridge.getName());

        collection.replaceOne(query, fridge.getDBDocument());

        return getRefrigerator(fridge.getName());
    }



}
