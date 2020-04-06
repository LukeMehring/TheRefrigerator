import luke.mehring.fridge.database.ItemType
import luke.mehring.fridge.database.Items
import luke.mehring.fridge.database.MongoDatabase
import luke.mehring.fridge.database.Refrigerator
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class DatabaseTest extends Specification {

    @Shared
    @AutoCleanup
    MongoDatabase mongo = new MongoDatabase()

    def "Refrigerator Saves and Gets"() {
        given:
        String fridgeName = "Fridge-1"
        String item1Name = "Item-1"
        String item2Name = "Item-2"

        Refrigerator startFridge = new Refrigerator()
        startFridge.setName(fridgeName)
        startFridge.addItem(new Items(item1Name, ItemType.PIZZA, 5))
        startFridge.addItem(new Items(item2Name, ItemType.PIZZA, 5))
        startFridge.addItem(new Items(item1Name, ItemType.PIZZA, -3))

        when:
        Refrigerator createdFridge = mongo.createOrUpdate(startFridge)
        Refrigerator getFridge = mongo.getRefrigerator(startFridge.getName())
        int deleteCount = mongo.deleteRefrigerator(startFridge.getName())

        then:
        assert deleteCount == 1
        assert startFridge.getItems().size() == 2
        assert startFridge.getItems().get(new Items(item1Name, ItemType.PIZZA, 0).getKey()).getCount() == 2
        assert startFridge.equals(createdFridge)
        assert startFridge.equals(getFridge)
    }

    def "Refrigerator Item Math over multiple saves"() {
        given:
        String fridgeName = "Fridge-2"
        String item1Name = "Item-1"
        String item2Name = "Item-2"

        Refrigerator startFridge1 = new Refrigerator()
        startFridge1.setName(fridgeName)
        startFridge1.addItem(new Items(item1Name, ItemType.PIZZA, 5))
        startFridge1.addItem(new Items(item2Name, ItemType.PIZZA, 5))
        startFridge1.addItem(new Items(item1Name, ItemType.PIZZA, -3))

        Refrigerator startFridge2 = new Refrigerator()
        startFridge2.setName(fridgeName)
        startFridge2.addItem(new Items(item2Name, ItemType.CHEESE, 10))
        startFridge2.addItem(new Items(item2Name, ItemType.PIZZA, 10))

        Refrigerator startFridge3 = new Refrigerator()
        startFridge3.setName(fridgeName)
        startFridge3.addItem(new Items(item2Name, ItemType.PIZZA, -15))

        when:
        mongo.createOrUpdate(startFridge1)
        mongo.createOrUpdate(startFridge2)
        mongo.createOrUpdate(startFridge3)

        Refrigerator finalFridge = mongo.getRefrigerator(fridgeName)

        int deleteCount = mongo.deleteRefrigerator(fridgeName)

        then:
        assert deleteCount == 1
        assert finalFridge.getItems().size() == 2
        assert finalFridge.getItems().get(new Items(item1Name, ItemType.PIZZA, 0).getKey()).getCount() == 2
        assert finalFridge.getItems().get(new Items(item2Name, ItemType.PIZZA, 0).getKey()) == null
        assert finalFridge.getItems().get(new Items(item2Name, ItemType.CHEESE, 0).getKey()).getCount() == 10
    }
}