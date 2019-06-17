package xyz.ekkor

import grails.compiler.GrailsCompileStatic
import grails.util.Environment
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@GrailsCompileStatic
@Slf4j
class BootStrap {

    UserDataService userDataService

    def init = { servletContext ->
        log.info "Loading database..."
        if ( Environment.current == Environment.DEVELOPMENT ) {
            configureForDevelopment()
            userAddForDevelopment()
            configureForCategoryDevelopment()
        } else if ( Environment.current == Environment.TEST ) {
            configureForTest()
        } else if ( Environment.current == Environment.PRODUCTION ) {
            configureForProduction()
        }
    }

    @CompileStatic
    void userAddForDevelopment() {
        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)

        // 테스트 User 생성
        def eunsebiUser = new User(
                username: 'eunsebi',
                password: '1234',
                person: new Person(fullName: '은서비', email: 'eunsebi@ekkor.xyz'),
                avatar: new Avatar(nickname: '은서비')
        )
        eunsebiUser.enabled = true
        eunsebiUser.createIp = '0.0.0.0'
        userDataService.saveUser eunsebiUser
        UserRole.create(eunsebiUser, adminRole)
    }

    @CompileStatic
    void configureForTest() {
        //quartzScheduler.start()
    }

    @CompileStatic
    void configureForDevelopment() {
        //quartzScheduler.start()

        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def classRole = new Role(authority: 'ROLE_CLASS').save(flush: true)
        def leaderRole = new Role(authority: 'ROLE_LEADER').save(flush: true)
        def maintRole = new Role(authority: 'ROLE_MAINT').save(flush: true)
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

        //if(!User.findByUsername('admin')) {

        // 테스트 User 생성
        def adminUser = new User(
                username: 'admin',
                password: 'admin123',
                person: new Person(fullName: '관리자', email: 'admin@ekkor.xyz'),
                avatar: new Avatar(nickname: '관리자')
        )
        adminUser.enabled = true
        adminUser.createIp = '0.0.0.0'
        userDataService.saveUser adminUser
        UserRole.create(adminUser, adminRole)
        //}

        /*def authorities = ['ROLE_CLIENT']
        authorities.each {
            if ( !Role.findByAuthority(it) ) {
                new Role(authority: it).save()
            }
        }
        if ( !User.findByUsername('sherlock') ) {
            def u = new User(username: 'sherlock', password: 'elementary')
            BANKCARD.each { k, v ->
                u.addToCoordinates(new SecurityCoordinate(position: k, value: v, user: u))
            }
            u.save()
            def ur = new UserRole(user: u, role:  Role.findByAuthority('ROLE_CLIENT'))
            ur.save()
        }*/
    }


    @CompileStatic
    void configureForCategoryDevelopment() {
        // 1 Level Category
        def questionsCategory = Category.get('questions') ?: new Category(code: 'questions', labelCode: 'questions.label', defaultLabel: 'Q&A', iconCssNames: 'fa fa-database', sortOrder: 0, writable: true, useNote: true, useOpinion: true, useEvaluate: true, useTag: true, requireTag: true).save(flush: true)
        def techCategory = Category.get('tech') ?: new Category(code: 'tech', labelCode: 'tech.label', defaultLabel: 'Tech', iconCssNames: 'fa fa-code', sortOrder: 1, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def communityCategory = Category.get('community') ?: new Category(code: 'community', labelCode: 'community.label', defaultLabel: '커뮤니티', iconCssNames: 'fa fa-comments', sortOrder: 2, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def informCategory = Category.get('inform') ?: new Category(code: 'inform', labelCode: 'inform.label', defaultLabel: 'Inform', iconCssNames: 'fa fa-comments', sortOrder: 2, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        //def columnsCategory = Category.get('columns') ?: new Category(code: 'columns', labelCode: 'columns.label', defaultLabel: '칼럼', iconCssNames: 'fa fa-quote-left', sortOrder: 3, writable: true, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        //def jobsCategory = Category.get('jobs') ?: new Category(code: 'jobs', labelCode: 'jobs.label', defaultLabel: 'Jobs', iconCssNames: 'fa fa-group', sortOrder: 4, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)

        // 2 Level Category

        // Tech
        def newsCategory = Category.get('news') ?: new Category(code: 'news', parent: techCategory, labelCode: 'news.label', defaultLabel: 'IT News & 정보', iconCssNames: 'fa fa-code', sortOrder: 0, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def tipsCategory = Category.get('tips') ?: new Category(code: 'tips', parent: techCategory, labelCode: 'tips.label', defaultLabel: 'Tips & Tricks', iconCssNames: 'fa fa-code', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)

        // Inform
        def classCategory = Category.get('class') ?: new Category(code: 'class', parent: informCategory, labelCode: 'life.label', defaultLabel: 'Class Inform', iconCssNames: 'fa fa-comments', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def readerCategory = Category.get('reader') ?: new Category(code: 'reader', parent: informCategory, labelCode: 'life.label', defaultLabel: 'Reader Inform', iconCssNames: 'fa fa-comments', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def maintCategory = Category.get('maint') ?: new Category(code: 'maint', parent: informCategory, labelCode: 'life.label', defaultLabel: 'Maint Inform', iconCssNames: 'fa fa-comments', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)

        // Community
        def noticeCategory = Category.get('notice') ?: new Category(code: 'notice', parent: communityCategory, labelCode: 'notice.label', defaultLabel: '공지사항', iconCssNames: 'fa fa-comments', sortOrder: 0, useNote: true, useOpinion: false, useEvaluate: false, useTag: true, adminOnly: true).save(flush: true)
        def lifeCategory = Category.get('life') ?: new Category(code: 'life', parent: communityCategory, labelCode: 'life.label', defaultLabel: '사는얘기', iconCssNames: 'fa fa-comments', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def forumCategory = Category.get('forum') ?: new Category(code: '포럼', parent: communityCategory, labelCode: 'forum.label', defaultLabel: 'Forum', iconCssNames: 'fa fa-code', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def gatheringCategory = Category.get('gathering') ?: new Category(code: 'gathering', parent: communityCategory, labelCode: 'gathering.label', defaultLabel: '정기모임/스터디', iconCssNames: 'fa fa-comments', sortOrder: 2, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def chatCategory = Category.get('chat') ?: new Category(code: 'chat', parent: communityCategory, labelCode: 'chat.label', defaultLabel: '잡담', iconCssNames: 'fa fa-comments', sortOrder: 3, useNote: true, useOpinion: false, useEvaluate: false).save(flush: true)
        def promoteCategory = Category.get('promote') ?: new Category(code: 'promote', parent: communityCategory, labelCode: 'gathering.label', defaultLabel: '학원홍보', iconCssNames: 'fa fa-comments', sortOrder: 3, useNote: true, useOpinion: false, useEvaluate: false).save(flush: true)

        // Job
        //def evalcomCategory = Category.get('evalcom') ?: new Category(code: 'evalcom', parent: jobsCategory, labelCode: 'gathering.label', defaultLabel: '좋은회사/나쁜회사', iconCssNames: 'fa fa-group', sortOrder: 0, useNote: true, useOpinion: false, useEvaluate: false, useTag: false, anonymity: true).save(flush: true)
        //def recruitCategory = Category.get('recruit') ?: new Category(code: 'recruit', parent: jobsCategory, labelCode: 'recruit.label', defaultLabel: '구인', iconCssNames: 'fa fa-group', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        //def resumesCategory = Category.get('resumes') ?: new Category(code: 'resumes', parent: jobsCategory, labelCode: 'resumes.label', defaultLabel: '구직', iconCssNames: 'fa fa-group', sortOrder: 3, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
    }

    @CompileStatic
    void configureForProduction() {
        //quartzScheduler.start()
    }

    def destroy = {
    }
}
