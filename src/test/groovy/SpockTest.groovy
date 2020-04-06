import luke.mehring.fridge.FridgeMain
import ratpack.http.client.ReceivedResponse
import ratpack.http.client.RequestSpec
import ratpack.test.MainClassApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification

class SpockTest extends Specification {

    MainClassApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(FridgeMain.class)

    def "First Spock Test Temp"() {
        given:
        TestHttpClient client = appUnderTest.getHttpClient()

        when:
        ReceivedResponse getAllResp = client.request("refrigerator/all", { RequestSpec req ->
            req.method("GET")
        })

        then:
        assert getAllResp.getStatusCode() == 200
    }
}