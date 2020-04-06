package luke.mehring.fridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import luke.mehring.fridge.database.MongoDatabase;
import luke.mehring.fridge.database.Refrigerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.server.RatpackServer;

import static ratpack.jackson.Jackson.json;

public class FridgeMain {

    private static final Logger logger = LoggerFactory.getLogger(FridgeMain.class);

    public static void main(String args[]) throws Exception {
        //Get MongoDB connection going
        MongoDatabase mongo = new MongoDatabase();
        ObjectMapper mapper = new ObjectMapper();

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
                        ctx.getRequest().getBody().then(data -> {
                            Refrigerator request = mapper.readValue(data.getText(), Refrigerator.class);
                            logger.debug("Creating one refrigerator ("+request.getName()+") via RESTAPI");
                            ctx.render(json(mongo.createOrUpdate(request)));
                        });
                    })
                    .put(ctx -> { //update
                        ctx.getRequest().getBody().then(data -> {
                            Refrigerator request = mapper.readValue(data.getText(), Refrigerator.class);
                            logger.debug("Updating one refrigerator ("+request.getName()+") via RESTAPI");
                            ctx.render(json(mongo.createOrUpdate(request)));
                        });
                    })
                    .delete(":name/delete", ctx -> { // Delete fridge by ID
                        String name = ctx.getPathTokens().get("name");
                        logger.debug("Deleting one refrigerator "+name+" via RESTAPI");
                        ctx.render(json(mongo.deleteRefrigerator(name)));
                    });
                })));
    } 
} 