package xyz.ekkor

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.validation.ValidationException
import groovy.util.logging.Slf4j
import org.hibernate.type.StandardBasicTypes

import static org.springframework.http.HttpStatus.*

@Slf4j
class ArticleController {

    ArticleService articleService
    ArticleDataService articleDataService
    ActivityService activityService
    NotificationService notificationService
    UserDataService userDataService
    SpringSecurityService springSecurityService

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    //static responseFormats = ['html', 'json']

    static allowedMethods = [save   : "POST", update: ["PUT", "POST"], delete: ["DELETE", "POST"], scrap: "POST",
                             addNote: "POST", assent: ["PUT", "POST"], dissent: ["PUT", "POST"]]

    //TODO 2019. 06. 03 게시물 리스트
    /**
     *
     * @param code
     * @param max
     * @return
     */
    def index(String code, Integer max) {
    //def index(Integer max) {
        log.info("Loading Arcicle List(index) database...")

        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: 'id'
        params.order = params.order ?: 'desc'
        params.query = params.query?.trim()

        log.info "code : " + code

        def category = Category.get(code)

        log.info "category : " + category

        if(category == null) {
            notFound()
            return
        }

        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        def notices = articleDataService.getNotices(category)

        def categories = category.children ?: [category]

        if(category.code == 'community')
            categories = categories.findAll { it.code != 'promote' }

        def articlesQuery = Article.where {
            category in categories
            if (SpringSecurityUtils.ifNotGranted("ROLE_ADMIN"))
                enabled == true
            if (params.query && params.query != '')
                title =~ "%${params.query}%" || content.text =~ "%${params.query}%"

            /*if(recruitFilter) {
                if(recruits)
                    id in recruits*.article*.id
                else
                    id in [Long.MAX_VALUE]
            }*/
        }

        log.info "params : " + params

        def articles = articlesQuery.list(params)
        //respond articles, model:[articleCount: articleService.count(), category: category]
        respond articles, model:[articlesCount: articlesQuery.count(), category: category, notices: notices]
    }

    /*def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond articleService.list(params), model:[articleCount: articleService.count()]
    }*/

    // TODO 2019. 06. 03 게시물 보기
    /**
     *
     * @param id
     * @return
     */
    def show(Long id) {
        //respond articleService.get(id)
        log.info "article show Loding......"

        def contentVotes = [], scrapped

        Article article = Article.get(id)

        if (article == null || (!article.enabled && SpringSecurityUtils.ifNotGranted("ROLE_ADMIN"))) {
            notFound()
            return
        }

        article.updateViewCount(1)

        if (springSecurityService.loggedIn) {
            Avatar avatar = Avatar.load(springSecurityService.principal.avatarId)
            contentVotes = ContentVote.findAllByArticleAndVoter(article, avatar)
            scrapped = Scrap.findByArticleAndAvatar(article, avatar)
        }

        def category = Category.get(article.categoryId)

        // 권한 확인
        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        def notes = Content.findAllByArticleAndTypeAndEnabled(article, ContentType.NOTE, true)

        def contentBanners = Banner.where {
            type == BannerType.CONTENT && visible == true
        }.list()

        def contentBanner = contentBanners ? randomService.draw(contentBanners) : null

        def changeLogs = ChangeLog.createCriteria().list {
            eq('article', article)
            projections {
                sqlGroupProjection 'article_id as articleId, max(date_created) as dateCreated, content_id as contentId', 'content_id',
                        ['articleId', 'dateCreated', 'contentId'],
                        [StandardBasicTypes.LONG, StandardBasicTypes.TIMESTAMP, StandardBasicTypes.LONG]
            }
        }

        respond article, model: [contentVotes: contentVotes, notes: notes, scrapped: scrapped, contentBanner: contentBanner, changeLogs: changeLogs]
        //respond article, model: [contentVotes: contentVotes, notes: notes, scrapped: scrapped, contentBanner: contentBanner]
        //respond article, model: [contentVotes: contentVotes, notes: notes, contentBanner: contentBanner]
    }

    def seq(Long id) {
        redirect uri: "/article/${id}"
    }

    //TODO 2019. 06. 03 게시물 작성 페이지
    /**
     *
     * @param code
     * @return
     */
    def create(String code) {
        //respond new Article(params)

        //Article article = new Article(params)

        log.info "params : " + params

        def category =Category.get(code)

        User user = springSecurityService.loadCurrentUser()

        if (category == null) {
            notFound()
            return
        }

        if (user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        // Category 접근 권한 확인
        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        params.category = category

        def writableCategories
        def categories = Category.findAllByEnabled(true)
        def goExternalLink = false

        // 권한 확인
        if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
            writableCategories = Category.findAllByWritableAndEnabled(true, true)
        } else {
            goExternalLink = category.writeByExternalLink
            writableCategories = Category.findAllByParentAndWritableAndEnabledAndAdminOnly(category?.parent ?: category, true, true, false) ?: [category]
            params.anonymity = category?.anonymity ?: false
        }

        def notices = params.list('notices') ?: []

        if(goExternalLink) {
            redirect(url: category.externalLink)
        } else {
            respond new Article(params), model: [writableCategories: writableCategories, category: category, categories: categories, notices: notices]
        }
    }

    //TODO 2019. 06. 02 게시물 저장
    /**
     *
     * @param article
     * @return
     */
    //def save(Article article) {
    def save(String code) {
        log.info("article save start")

        log.info "Article Save Loding......."

        Article article = new Article(params)

        log.info "Artice Save Params : " + params
        log.info "Article save article : " + article
        log.info "category code : " + params.code

        //Category category = Category.get(article.category)
        Category category = Category.get(params.code)

        log.info "category : " + category

        User user = springSecurityService.loadCurrentUser()

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        if (article == null) {
            notFound()
            return
        }

        try {
            //추가
            def realIp = userDataService.getRealIp(request)

            Avatar author = Avatar.load(springSecurityService.principal.avatarId)

            if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
                article.choice = params.choice?:false
                article.enabled = !params.disabled
                article.ignoreBest = params.ignore ?: false
            }

            article.createIp = userDataService.getRealIp(request)

            // 서비스 항목
            article.category = category
            article.author = author
            //

            articleDataService.save(article, author, category)

            //articleService.save(article, author, category)
            //articleService.save(article)

            //articleService.save(article)
        } catch (ValidationException e) {
            respond article.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'article.label', default: 'Article'), article.id])
                redirect article
            }
            '*' { respond article, [status: CREATED] }
        }
    }

    //TODO 2019. 06.09 tag 클릭
    def tagged(String tag, Integer max) {
        params.max = Math.min(max ?: 20, 100)
        params.sort = params.sort ?: 'id'
        params.order = params.order ?: 'desc'
        params.query = params.query?.trim()

        if (tag == null) {
            notFound()
            return
        }

        def articlesQuery = Article.where {
            tagString =~ "%${tag}%"
            if (params.query && params.query != '')
                title =~ "%${params.query}%" || content.text =~ "%${params.query}%"
        }

        respond articlesQuery.list(params), model:[articleCount: articlesQuery.count()]
    }

    //TODO  게시물 수정 페이지
    def edit(Long id) {
        //respond articleService.get(id)
        Article article = Article.get(id)

        if (article == null) {
            notFound()
            return
        }

        if(SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        def writableCategories
        def categories = Category.findAllByEnabled(true)

        if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
            writableCategories = Category.findAllByWritableAndEnabled(true, true)
        } else {
            writableCategories = article.category.children ?: article.category.parent?.children ?: [article.category]
        }

        if(params.categoryCode) {
            article.category = Category.get(params.categoryCode)
        }

        def notices = ArticleNotice.findAllByArticle(article)

        respond article, model: [writableCategories: writableCategories, categories: categories, notices: notices]

    }

    // update
    /*def update(Article article, Avatar editor, Category category) {
        if (article == null) {
            notFound()
            return
        }

        try {
            article.category = category
            article.lastEditor = editor
            article.content.lastEditor = editor

            articleService.save(article)
        } catch (ValidationException e) {
            respond article.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'article.label', default: 'Article'), article.id])
                redirect article
            }
            '*'{ respond article, [status: OK] }
        }
    }*/

    //TODO  게시물 수정 함수
    def update(Article article) {
        User user = springSecurityService.loadCurrentUser()

        if (SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        if (user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        try {
            withForm {

                Avatar editor = Avatar.get(springSecurityService.principal.avatarId)

                Category category = Category.get(params.categoryCode)

                if (SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
                    article.choice = params.choice ?: false
                    article.enabled = !params.disabled
                    article.ignoreBest = params.ignore ?: false
                }

                articleDataService.update(article, editor, category)

                articleDataService.removeNotices(article)

                articleDataService.saveNotices(article, user, params.list('notices'))

                withFormat {
                    html {
                        flash.message = message(code: 'default.updated.message', args: [message(code: 'Article.label', default: 'Article'), article.id])
                        redirect article
                    }
                    json { respond article, [status: OK] }
                }

            }.invalidToken {
                redirect article
            }
        } catch (ValidationException e) {
            respond(article.errors, view: 'edit')
        }
    }

    //TODO  게시물 삭제
    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        println "delete Id : " + id

        Article article = Article.get(id)

        User user = springSecurityService.loadCurrentUser()

        def categoryCode = article.category.code

        if (article == null) {
            notFound()
            return
        }

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        if(SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        articleDataService.delete(article)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'article.label', default: 'Article'), id])
                flash.status = "success"
                //redirect action:"index", method:"GET"
                redirect uri: "/articles/${categoryCode}", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    //TODO 댓글 등록 2019. 05. 19
    /**
     * 댓들 등록
     * @param id
     * @return
     */
    def addNote(Long id) {
        Article article = Article.get(id)

        /*User user = springSecurityService.loadCurrentUser()

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }*/

        try {

            Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)

            Content content = new Content()
            bindData(content, params, 'note')

            content.createIp = userDataService.getRealIp(request)

            log.info "댓글 서비스 가자"
            articleDataService.addNote(article, content, avatar)

            withFormat {
                html {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'Note.label', default: 'Note'), article.id])
                    redirect article
                }
                json {
                    respond article, [status: OK]
                }
            }

        } catch (ValidationException e) {
            flash.error = e.message
            redirect article
        }
    }

    //TODO 추천 2019. 05. 20
    def assent(Long id, Long contentId) {
        Article article = Article.get(id)

        Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)
        Content content = Content.get(contentId)

        articleDataService.addVote(article, content, avatar, 1)

        withFormat {
            html { redirect article }
            json {
                content.refresh()
                def result = [voteCount: content.voteCount]
                respond result
            }
        }
    }

    //TODO 2019. 06. 17 스크랩
    def scrap(Long id) {
        Article article = Article.get(id)

        if (article == null) {
            notFound()
            return
        }

        try {
            Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)

            if (Scrap.countByArticleAndAvatar(article, avatar) < 1) {
                articleDataService.saveScrap(article, avatar)
            } else {
                articleDataService.deleteScrap(article, avatar)
            }

            withFormat {
                html { redirect article }
                json {
                    //article.refresh()
                    def result = [scrapCount: article.scrapCount]
                    respond result
                }
            }


        } catch (ValidationException e) {
            flash.error = e.message
            redirect article
        }

    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'article.label', default: 'Article'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void forbidden() {

        withFormat {
            html {
                flash.message = message(code: 'default.forbidden.message', args: [message(code: 'article.label', default: 'Article'), params.id])
                redirect uri: '/'
            }
            json { render status: FORBIDDEN }
        }
    }

    protected notAcceptable() {
        withFormat {
            html {
                flash.message = message(code: 'default.notAcceptable.message', args: [message(code: 'article.label', default: 'Article'), params.id])
                redirect uri: '/'
            }
            json { render status: NOT_ACCEPTABLE }
        }
    }
}
