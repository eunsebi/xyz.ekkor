package xyz.ekkor

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ContentServiceSpec extends Specification {

    ContentService contentService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Content(...).save(flush: true, failOnError: true)
        //new Content(...).save(flush: true, failOnError: true)
        //Content content = new Content(...).save(flush: true, failOnError: true)
        //new Content(...).save(flush: true, failOnError: true)
        //new Content(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //content.id
    }

    void "test get"() {
        setupData()

        expect:
        contentService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Content> contentList = contentService.list(max: 2, offset: 2)

        then:
        contentList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        contentService.count() == 5
    }

    void "test delete"() {
        Long contentId = setupData()

        expect:
        contentService.count() == 5

        when:
        contentService.delete(contentId)
        sessionFactory.currentSession.flush()

        then:
        contentService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Content content = new Content()
        contentService.save(content)

        then:
        content.id != null
    }
}
