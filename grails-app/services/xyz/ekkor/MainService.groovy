package xyz.ekkor

import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import grails.plugin.springsecurity.SpringSecurityService
import org.hibernate.FetchMode

@Transactional
class MainService {

    SpringSecurityService springSecurityService

    //@Cacheable(value="choiceArticlesCache")
    def getChoiceArticles() {

        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            eq('choice', true)
            eq('enabled', true)
            ne('category', Category.get('recruit'))
            ne('category', Category.get('resumes'))
            order('id', 'desc')
            maxResults(5)
        }.findAll()
    }

    //@Cacheable(value="weeklyArticlesCache")
    def getWeeklyArticles() {

        def diff = new Date() - 7

        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            ne('category', Category.get('promote'))
            ne('category', Category.get('recruit'))
            eq('enabled', true)
            eq('choice', false)
            eq('ignoreBest', false)
            gt('dateCreated', diff)
            order('best', 'desc')
            maxResults(5)
        }.findAll()
    }

    @Cacheable("techArticlesCache")
    def getTechArticles() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('tech').children)
            //Category.get('tech').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(3)
        }.findAll()
    }

    //@Cacheable("qnaArticlesCache")
    def getQnaArticles() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('questions'))
            //Category.get('questions').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(10)
        }.findAll()
    }

    //@Cacheable("communityArticlesCache")
    def getCommunityArticles() {

        def categories = Category.get('community').children.findAll { it.code != 'promote' }

        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', categories)
            //categories
            eq('enabled', true)
            order('id', 'desc')
            maxResults(20)
        }.findAll()
    }

    //@Cacheable("informArticlesCache")
    def getInformArticles() {

        def categories = Category.get('inform').children.findAll { it.code != 'promote' }

        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', categories)
            //'in'('category', Category.get('inform').children.findAll())
            //Category.get('inform').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(3)
        }.findAll()
    }

    //@Cacheable("classArticlesCache")
    def getClassArticles() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('class'))
            //Category.get('questions').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(4)
        }.findAll()
    }

    //@Cacheable("leaderArticlesCache")
    def getLeaderArticles() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('leader'))
            //Category.get('questions').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(4)
        }.findAll()
    }

    //@Cacheable("maintArticlesCache")
    def getMaintArticles() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('maint'))
            //Category.get('questions').children
            eq('enabled', true)
            order('id', 'desc')
            maxResults(4)
        }.findAll()
    }

    @Cacheable("columnsArticlesCache")
    def getColumnArticle() {
        Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            eq('category', Category.get('columns'))
            eq('enabled', true)
            order('id', 'desc')
            maxResults(1)
        }.find()
    }

    @Cacheable("promoteArticlesCache")
    def getPromoteArticles() {

        def diff = new Date() - 7

        def promoteArticles = Article.withCriteria() {
            fetchMode 'content', FetchMode.JOIN
            fetchMode 'author', FetchMode.JOIN
            'in'('category', Category.get('promote'))
            //Category.get('promote')
            eq('enabled', true)
            gt('dateCreated', diff)
            order('id', 'desc')
            maxResults(50)
        }.findAll()

        promoteArticles = promoteArticles.unique{ a, b -> a.authorId <=> b.authorId }

        Collections.shuffle(promoteArticles)

//        promoteArticles = promoteArticles.unique { a, b -> a.createIp <=> b.createIp }
        if(promoteArticles?.size() > 4) promoteArticles = promoteArticles.subList(0, 3)

        promoteArticles
    }
}
