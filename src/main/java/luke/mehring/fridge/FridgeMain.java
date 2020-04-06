package luke.mehring.fridge;

import luke.mehring.fridge.database.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.server.RatpackServer;

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
                        ctx.render("Get All");
                    })
                    .get(":id", ctx -> { //get single fridge ID
                        Long id = Long.valueOf(ctx.getPathTokens().get("id"));
                        logger.debug("Retrieving one refrigerator "+id+" via RESTAPI");
                        ctx.render("Get " + id);
                    })
                    .post(ctx -> { //create

                        logger.debug("Creating one refrigerator via RESTAPI");
                        ctx.render("Create One");
                    })
                    .put(ctx -> { //update

                        logger.debug("Updating one refrigerator via RESTAPI");
                        ctx.render("Update One");
                    })
                    .delete(":id", ctx -> { // Delete fridge by ID
                        Long id = Long.valueOf(ctx.getPathTokens().get("id"));
                        logger.debug("Deleting one refrigerator "+id+" via RESTAPI");
                        ctx.render("Delete " + id);

                    });
                })));
    } 
} 