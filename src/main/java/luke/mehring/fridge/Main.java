package luke.mehring.fridge;


import ratpack.server.RatpackServer;

class Main {

    public static void main(String args[]) throws Exception {
        RatpackServer.start(server -> server.handlers(chain -> chain
                .prefix("refrigerator", empChain -> { //Fridge Calls
                    empChain.get("all", ctx -> {  //getAll

                    })
                    .get(":id", ctx -> { //get single fridge ID

                    })
                    .post(ctx -> { //create

                    })
                    .put(ctx -> { //update

                    })
                    .delete(":id", ctx -> { // Delete fridge by ID

                    });
                })));
    } 
} 