import com.fasterxml.jackson.databind.ObjectMapper
import luke.mehring.fridge.FridgeMain
import luke.mehring.fridge.database.ItemType
import luke.mehring.fridge.database.Items
import luke.mehring.fridge.database.Refrigerator
import ratpack.http.client.ReceivedResponse
import ratpack.http.client.RequestSpec
import ratpack.test.MainClassApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification

class EndPointTest extends Specification {

    MainClassApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(FridgeMain)
    ObjectMapper mapper = new ObjectMapper();

    def "End point test"(){
        given:
        TestHttpClient client = appUnderTest.getHttpClient()
        String fridgeName1 = "Fridge-3"
        String fridgeName2 = "Fridge-4"
        String item1Name = "Item-1"
        String item2Name = "Item-2"

        Refrigerator altFridge1 = new Refrigerator()
        altFridge1.setName(fridgeName1)
        altFridge1.addItem(new Items(item1Name, ItemType.SODA, 12))

        Refrigerator startFridge1 = new Refrigerator()
        startFridge1.setName(fridgeName2)
        startFridge1.addItem(new Items(item1Name, ItemType.PIZZA, 5))
        startFridge1.addItem(new Items(item2Name, ItemType.PIZZA, 5))
        startFridge1.addItem(new Items(item1Name, ItemType.PIZZA, -3))

        Refrigerator startFridge2 = new Refrigerator()
        startFridge2.setName(fridgeName2)
        startFridge2.addItem(new Items(item2Name, ItemType.CHEESE, 10))
        startFridge2.addItem(new Items(item2Name, ItemType.PIZZA, 10))

        Refrigerator startFridge3 = new Refrigerator()
        startFridge3.setName(fridgeName2)
        startFridge3.addItem(new Items(item2Name, ItemType.PIZZA, -15))

        when:
        //Create a bunch (only 2 really)
        Refrigerator altFridge1Created   = callFridgeWithBody(client, altFridge1)
        Refrigerator startFridge1Created = callFridgeWithBody(client, startFridge1)
        Refrigerator startFridge2Created = callFridgeWithBody(client, startFridge2)
        Refrigerator startFridge3Created = callFridgeWithBody(client, startFridge3)

        ReceivedResponse getAllResp = client.request("all", { RequestSpec req ->
            req.method("GET")
            req.basicAuth("username", "username")
        })
        assert getAllResp.getStatusCode() == 200 //Make sure it worked
        List<Refrigerator> allList = mapper.readValue(getAllResp.body.getText(), mapper.getTypeFactory().constructCollectionType(List.class, Refrigerator.class)) as List<Refrigerator>

        //Get Single
        ReceivedResponse responseGetSingle = client.request("get/" + fridgeName2,{ RequestSpec req ->
            req.method("GET")
            req.basicAuth("username", "username")
        })
        assert responseGetSingle.getStatusCode() == 200 //Make sure it worked
        Refrigerator singleGetResponse = mapper.readValue(responseGetSingle.getBody().getText(),  Refrigerator.class)

        //Delete1
        ReceivedResponse responseDelete1 = client.request(fridgeName2 + "/delete", { RequestSpec req ->
            req.method("DELETE")
            req.basicAuth("username", "username")
        })
        assert responseDelete1.getStatusCode() == 200 //Make sure it worked
        //Delete2
        ReceivedResponse responseDelete2 = client.request(fridgeName1 + "/delete", { RequestSpec req ->
            req.method("DELETE")
            req.basicAuth("username", "username")
        })
        assert responseDelete2.getStatusCode() == 200 //Make sure it worked

        then:
        assert altFridge1Created.equals(altFridge1)
        assert allList.size() == 2
        assert singleGetResponse.getItems().size() == 2
        assert singleGetResponse.getItems().get(new Items(item1Name, ItemType.PIZZA, 0).getKey()).getCount() == 2
        assert singleGetResponse.getItems().get(new Items(item2Name, ItemType.PIZZA, 0).getKey()) == null
        assert singleGetResponse.getItems().get(new Items(item2Name, ItemType.CHEESE, 0).getKey()).getCount() == 10
    }

    Refrigerator callFridgeWithBody(TestHttpClient client, Refrigerator refrigerator) {
        ReceivedResponse responsePost = client.request(refrigerator.getName() + "/save", { RequestSpec req ->
            req.method("POST")
            req.getBody().text(mapper.writeValueAsString(refrigerator))
            req.basicAuth("username", "username")
        })
        assert responsePost.getStatusCode() == 200 //make sure it worked
        return mapper.readValue(responsePost.getBody().getText(),  Refrigerator.class)
    }
}