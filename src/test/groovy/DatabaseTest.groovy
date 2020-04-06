import luke.mehring.fridge.FridgeMain
import luke.mehring.fridge.database.MongoDatabase
import ratpack.http.client.ReceivedResponse
import ratpack.http.client.RequestSpec
import ratpack.test.MainClassApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class DatabaseTest extends Specification {

    @Shared
    @AutoCleanup
    MongoDatabase mongo = new MongoDatabase()

    def "First Spock Test Temp"() {
        given:
        String a = "test";

        when:
        String b = a;


        then:
        assert 200 == 200
    }
}