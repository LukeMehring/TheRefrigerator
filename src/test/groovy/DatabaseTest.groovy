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
        startFridge.addItem(createItem(item1Name, ItemType.PIZZA, 5))
        startFridge.addItem(createItem(item2Name, ItemType.PIZZA, 5))
        startFridge.addItem(createItem(item1Name, ItemType.PIZZA, -3))

        when:
        Refrigerator createdFridge = mongo.createOrUpdate(startFridge)
        Refrigerator getFridge = mongo.getRefrigerator(startFridge.getName())
        int deleteCount = mongo.deleteRefrigerator(startFridge.getName())

        then:
        deleteCount == 1
        startFridge.getItemsMap().size() == 2
        startFridge.getItemsMap().get(createItem(item1Name, ItemType.PIZZA, 0).getKey()).getCount() == 2
        startFridge.equals(createdFridge)
        startFridge.equals(getFridge)
    }

    def "Refrigerator Item Math over multiple saves"() {
        given:
        String fridgeName = "Fridge-2"
        String item1Name = "Item-1"
        String item2Name = "Item-2"

        Refrigerator startFridge1 = new Refrigerator()
        startFridge1.setName(fridgeName)
        startFridge1.addItem(createItem(item1Name, ItemType.PIZZA, 5))
        startFridge1.addItem(createItem(item2Name, ItemType.PIZZA, 5))
        startFridge1.addItem(createItem(item1Name, ItemType.PIZZA, -3))

        Refrigerator startFridge2 = new Refrigerator()
        startFridge2.setName(fridgeName)
        startFridge2.addItem(createItem(item2Name, ItemType.CHEESE, 10))
        startFridge2.addItem(createItem(item2Name, ItemType.PIZZA, 10))

        Refrigerator startFridge3 = new Refrigerator()
        startFridge3.setName(fridgeName)
        startFridge3.addItem(createItem(item2Name, ItemType.PIZZA, -16))

        when:
        mongo.createOrUpdate(startFridge1)
        mongo.createOrUpdate(startFridge2)
        mongo.createOrUpdate(startFridge3)

        Refrigerator finalFridge = mongo.getRefrigerator(fridgeName)

        int deleteCount = mongo.deleteRefrigerator(fridgeName)

        then:
        deleteCount == 1
        finalFridge.getItemsMap().size() == 2
        finalFridge.getItemsMap().get(createItem(item1Name, ItemType.PIZZA, 0).getKey()).getCount() == 2
        finalFridge.getItemsMap().get(createItem(item2Name, ItemType.PIZZA, 0).getKey()) == null
        finalFridge.getItemsMap().get(createItem(item2Name, ItemType.CHEESE, 0).getKey()).getCount() == 10
    }


    def "Test Max Soda of 12"() {
        given:
        String fridgeName1 = "Fridge-32"
        String fridgeName2 = "Fridge-15"
        String item1Name = "Item-1"
        String item2Name = "Item-2"

        Refrigerator startFridge1 = new Refrigerator()
        startFridge1.setName(fridgeName1)
        startFridge1.addItem(createItem(item1Name, ItemType.SODA, 5))
        startFridge1.addItem(createItem(item2Name, ItemType.SODA, 7))

        Refrigerator startFridge2 = new Refrigerator()
        startFridge2.setName(fridgeName2)
        startFridge2.addItem(createItem(item2Name, ItemType.SODA, 5))
        startFridge2.addItem(createItem(item2Name, ItemType.SODA, 8))

        Refrigerator startFridge3 = new Refrigerator()
        startFridge3.setName(fridgeName1)
        startFridge3.addItem(createItem(item2Name, ItemType.SODA, 1))

        when:
        try { //work
            mongo.createOrUpdate(startFridge1)
        } catch (Exception e) {}
        try { //fail
            mongo.createOrUpdate(startFridge2)
        } catch (Exception e) {}
        try { //fail
            mongo.createOrUpdate(startFridge3)
        } catch (Exception e) {}

        Refrigerator finalFridge1 = mongo.getRefrigerator(fridgeName1)

        int deleteCount1 = mongo.deleteRefrigerator(fridgeName1)
        int deleteCount2 = mongo.deleteRefrigerator(fridgeName2)

        then:
        deleteCount1 == 1
        deleteCount2 == 0
        finalFridge1.getItemsMap().size() == 2
        finalFridge1.getItemsMap().get(createItem(item1Name, ItemType.SODA, 0).getKey()).getCount() == 5
        finalFridge1.getItemsMap().get(createItem(item2Name, ItemType.SODA, 0).getKey()).getCount() == 7
    }

    Items createItem(String name, ItemType type, int count) {
        Items theItem = new Items()
        theItem.setName(name)
        theItem.setType(type)
        theItem.setCount(count)
        return theItem
    }
}