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

    def "Refrigerator Saves"() {
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


        then:
        assert startFridge.equals(createdFridge)
        assert startFridge.equals(getFridge)
    }
}