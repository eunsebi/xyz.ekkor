package xyz.ekkor

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BannerServiceSpec extends Specification {

    BannerService bannerService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Banner(...).save(flush: true, failOnError: true)
        //new Banner(...).save(flush: true, failOnError: true)
        //Banner banner = new Banner(...).save(flush: true, failOnError: true)
        //new Banner(...).save(flush: true, failOnError: true)
        //new Banner(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //banner.id
    }

    void "test get"() {
        setupData()

        expect:
        bannerService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Banner> bannerList = bannerService.list(max: 2, offset: 2)

        then:
        bannerList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        bannerService.count() == 5
    }

    void "test delete"() {
        Long bannerId = setupData()

        expect:
        bannerService.count() == 5

        when:
        bannerService.delete(bannerId)
        sessionFactory.currentSession.flush()

        then:
        bannerService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Banner banner = new Banner()
        bannerService.save(banner)

        then:
        banner.id != null
    }
}
