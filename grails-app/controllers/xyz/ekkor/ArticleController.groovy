package xyz.ekkor

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ArticleController {

    ArticleService articleService
    ArticleDataService articleDataService
    UserDataService userDataService
    SpringSecurityService springSecurityService

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    static allowedMethods = [save   : "POST", update: ["PUT", "POST"], delete: ["DELETE", "POST"], scrap: "POST",
                             addNote: "POST", assent: ["PUT", "POST"], dissent: ["PUT", "POST"]]

    //def index(Integer max) {
    def index(Integer max) {
        println "Loding in Article index List~~~~~~~~~~~~~~~~~~~~~~~"
        params.max = Math.min(max ?: 10, 100)
        respond articleService.list(params), model:[articleCount: articleService.count()]
    }

    /*def index(String code, Integer max) {
        println "ggggggggggggggggggggggg"
        params.max = Math.min(max ?: 10, 100)
        respond articleService.list(params), model:[articleCount: articleService.count()]
    }*/

    def show(Long id) {

        println "11111111111111111111111111111111111111111111111"
        //respond articleService.get(id)

        def contentVotes = [], scrapped

        Article article = Article.get(id)

        article.updateViewCount(1)

        def notes = Content.findAllByArticleAndTypeAndEnabled(article, ContentType.NOTE, true)

        //respond article, model: [contentVotes: contentVotes, notes: notes, scrapped: scrapped, contentBanner: contentBanner, changeLogs: changeLogs]
        respond article, model: [contentVotes: contentVotes, notes: notes, scrapped: scrapped]
    }

    def create(String code) {
        //respond new Article(params)

        code = "4"

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

        params.category = category

        def writableCategories
        def categories = Category.findAllByEnabled(true)
        def goExternalLink = false

        // 권한 확인 process
        /*println "user Role: " + user.getAuthorities()
        println "Category Role : " + category.cate_role*/

        /*String[] role = user.getAuthorities()
        int user_size = user.getAuthorities().size()
        String category_role = Integer.toString(category.cate_role)

        for (int num ; num < user_size ; num++) {
            role[num] = role[num].substring(role[num].length() -1)
        }

        boolean result = Arrays.asList(role).contains(category_role)

        //println " 권한 : " + result
        */

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

    def save(Article article) {

        println "save Category Code : " + article.category

        //Article article = new Article(params)
        Category category = Category.get(params.categoryCode)

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

            articleDataService.save(article, author, category)

            //

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

    def edit(Long id) {
        respond articleService.get(id)
    }

    def update(Article article) {
        if (article == null) {
            notFound()
            return
        }

        try {
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
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        articleService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'article.label', default: 'Article'), id])
                redirect action:"index", method:"GET"
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

            println "댓글 서비스 가자"
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
}
