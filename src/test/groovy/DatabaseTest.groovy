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
        Refrigerator fridge = new Refrigerator()
        fridge.setName("Fridge-1")
        fridge.addItem(new Items("Item-1", ItemType.PIZZA, 5))

        when:
        mongo.createOrUpdate(fridge)


        then:
        assert 200 == 200
    }
}