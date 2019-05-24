package xyz.ekkor

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AnonymousSpec extends Specification implements DomainUnitTest<Anonymous> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
