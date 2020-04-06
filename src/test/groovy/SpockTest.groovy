import spock.lang.Specification
 
class SpockTest extends Specification {

    def "First Spock Test Temp"() {
        given:

        when:
        String a = "Test"
        String b = "Test2"

        then:
        assert a != b
    }
}