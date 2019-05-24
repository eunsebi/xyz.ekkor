package xyz.ekkor

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ArticleServiceSpec extends Specification {

    ArticleService articleService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Article(...).save(flush: true, failOnError: true)
        //new Article(...).save(flush: true, failOnError: true)
        //Article article = new Article(...).save(flush: true, failOnError: true)
        //new Article(...).save(flush: true, failOnError: true)
        //new Article(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //article.id
    }

    void "test get"() {
        setupData()

        expect:
        articleService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Article> articleList = articleService.list(max: 2, offset: 2)

        then:
        articleList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        articleService.count() == 5
    }

    void "test delete"() {
        Long articleId = setupData()

        expect:
        articleService.count() == 5

        when:
        articleService.delete(articleId)
        sessionFactory.currentSession.flush()

        then:
        articleService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Article article = new Article()
        articleService.save(article)

        then:
        article.id != null
    }
}
