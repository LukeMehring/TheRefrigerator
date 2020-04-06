package luke.mehring.fridge;

import luke.mehring.fridge.database.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.server.RatpackServer;

import static ratpack.jackson.Jackson.json;

public class FridgeMain {

    private static final Logger logger = LoggerFactory.getLogger(FridgeMain.class);

    public static void main(String args[]) throws Exception {
        //Get MongoDB connection going
        MongoDatabase mongo = new MongoDatabase();

        //Start the RATPACK
        RatpackServer.start(server -> server.handlers(chain -> chain
                .prefix("refrigerator", empChain -> { //Fridge Calls
                    empChain.get("all", ctx -> {  //getAll
                        logger.debug("Retrieving all refrigerators via RESTAPI");
                        ctx.render(json(mongo.getAll()));
                    })
                    .get(":name", ctx -> { //get single fridge ID
                        String name = ctx.getPathTokens().get("name");
                        logger.debug("Retrieving one refrigerator "+name+" via RESTAPI");
                        ctx.render(json(mongo.getRefrigerator(name)));
                    })
                    .post(ctx -> { //create
                        logger.debug("Creating one refrigerator via RESTAPI");
                        ctx.render("Create One");
                    })
                    .put(ctx -> { //update
                        logger.debug("Updating one refrigerator via RESTAPI");
                        ctx.render("Update One");
                    })
                    .delete(":name", ctx -> { // Delete fridge by ID
                        String name = ctx.getPathTokens().get("name");
                        logger.debug("Deleting one refrigerator "+name+" via RESTAPI");
                        ctx.render(json(mongo.deleteRefrigerator(name)));
                    });
                })));
    } 
} 