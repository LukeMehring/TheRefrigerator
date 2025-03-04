package luke.mehring.fridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import luke.mehring.fridge.database.MongoDatabase;
import luke.mehring.fridge.database.Refrigerator;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.guice.Guice;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;

import static ratpack.jackson.Jackson.json;

public class FridgeMain {

    private static final Logger logger = LoggerFactory.getLogger(FridgeMain.class);

    public static void main(String args[]) throws Exception {
        //Get MongoDB connection going
        MongoDatabase mongo = new MongoDatabase();
        ObjectMapper mapper = new ObjectMapper();

        //Start the RATPACK
        RatpackServer.start(server -> server
                .registry(Guice.registry(b -> b
                        .module(SessionModule.class)
                ))
                .handlers(chain -> chain
                        .all(ratpack.pac4j.RatpackPac4j.authenticator(new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator())))
                        .all(RatpackPac4j.requireAuth(DirectBasicAuthClient.class))
                        .path("fridges", ctx -> {
                                ctx.byMethod( method-> method
                                    .get(() -> {
                                        logger.debug("Retrieving all refrigerators via RESTAPI");
                                        ctx.render(json(mongo.getAll()));
                                    })
                                    .post(() -> {
                                        ctx.getRequest().getBody().then(data -> {
                                            Refrigerator request = mapper.readValue(data.getText(), Refrigerator.class);
                                            logger.debug("Creating one refrigerator ("+request.getName()+") via RESTAPI");
                                            ctx.render(json(mongo.createOrUpdate(request)));
                                        });
                                    })
                                    .put(() -> {
                                        ctx.getRequest().getBody().then(data -> {
                                            Refrigerator request = mapper.readValue(data.getText(), Refrigerator.class);
                                            logger.debug("Updating one refrigerator ("+request.getName()+") via RESTAPI");
                                            ctx.render(json(mongo.createOrUpdate(request)));
                                        });
                                    })
                                );
                        })
                        .path("fridges/:name", ctx -> {
                            ctx.byMethod( method-> method
                                    .get(() -> {
                                        String name = ctx.getPathTokens().get("name");
                                        logger.debug("Retrieving one refrigerator "+name+" via RESTAPI");
                                        ctx.render(json(mongo.getRefrigerator(name)));
                                    })
                                    .delete(() -> {
                                        String name = ctx.getPathTokens().get("name");
                                        logger.debug("Deleting one refrigerator "+name+" via RESTAPI");
                                        ctx.render(json(mongo.deleteRefrigerator(name)));
                                    })
                            );
                        })
                )
            );
    }
}