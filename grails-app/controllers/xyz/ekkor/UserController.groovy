package xyz.ekkor

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService
    UserDataService userDataService
    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def beforeInterceptor = [action:this.&notLoggedIn,
                             except: ['edit', 'update', 'index', 'rejectDM', 'withdrawConfirm', 'withdraw', 'passwordChange', 'updatePasswordChange']]

    private notLoggedIn() {
        if(springSecurityService.loggedIn) {
            redirect uri: '/'
            return false
        }
    }

    //TODO 2019. 06. 09 user  활동 정보(user/info/id)
    def index(Integer id, Integer max) {
        //params.max = Math.min(max ?: 10, 100)
        //respond userService.list(params), model:[userCount: userService.count()]

        params.max = Math.min(max ?: 20, 100)
        params.sort = params.sort ?: 'id'
        params.order = params.order ?: 'desc'

        Avatar currentAvatar = Avatar.get(id)
        User user = User.findByAvatar(currentAvatar)

        if (user.withdraw) {
            redirect uri: '/'
            return
        }

        def activitiesQuery

        if(params.category == 'activity' || (!params.category && !currentAvatar.official)) {
            activitiesQuery= Activity.where {
                avatar == currentAvatar
            }
        } else {
            def category

            if(params.category == 'solved') category = ActivityType.SOLVED
            else if(params.category == 'scrapped') category = ActivityType.SCRAPED
            else {
                params.category = 'articles'
                category = ActivityType.POSTED
            }

            activitiesQuery= Activity.where {
                avatar == currentAvatar
                type == category
            }
        }

        def counts = [
                postedCount: Activity.countByAvatarAndType(currentAvatar, ActivityType.POSTED),
                solvedCount: Activity.countByAvatarAndType(currentAvatar, ActivityType.SOLVED),
                followerCount : Follow.countByFollowing(currentAvatar),
                followingCount : Follow.countByFollower(currentAvatar),
                scrappedCount : Scrap.countByAvatar(currentAvatar)
        ]

        respond user, model: [avatar: currentAvatar, activities: activitiesQuery.list(params), activitiesCount: activitiesQuery.count(), counts: counts]
    }

    def show(Long id) {
        respond userService.get(id)
    }

    def create() {
        respond new User(params)
    }

    def register() {
        respond new User(params)
    }

    //TODO 2019. 06. 13 user 회원가입 저장
    def save(User user) {
        if (user == null) {
            notFound()
            return
        }

        try {
            def realIp = userDataService.getRealIp(request)

            user.createIp = realIp

            //userService.save(user)
            userDataService.saveUser(user)

            // 가입승인 확인 메일 발송
            /*def key = userService.createConfirmEmail(user)

            mailService.sendMail {
                async true
                to user.person.email
                subject message(code:'email.join.subject')
                body(view:'/email/join_confirm', model: [user: user, key: key, grailsApplication: grailsApplication] )
            }

            session['confirmSecuredKey'] = key*/


        } catch (ValidationException e) {
            respond user.errors, view:'register'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                //redirect user
                //redirect action: 'complete'
                render view: 'complete'
            }
            '*' { respond user, [status: CREATED] }
        }
    }

    //TODO 2019. 06. 13 회원가입 완료 처리
    def complete() {
        if (springSecurityService.isLoggedIn()) {
            redirect uri: "/"
            return
        }

        // 메일 발송
        /*def confirmEmail = ConfirmEmail.where {
            securedKey == session['confirmSecuredKey'] &&
                    dateExpired > new Date()
        }.get()

        if(!confirmEmail) {
            flash.message = message(code: 'default.expired.link.message')
            redirect uri: '/login/auth'
            return
        }*/

        //render view: 'complete', model: [email: confirmEmail.email]
        render view: 'complete'
    }

    //TODO 2019. 06. 13 메일 발송 실행 및 완료 화면
    def confirm(String key) {

        if(springSecurityService.isLoggedIn()) {
            redirect uri: "/"
            return
        }

        session.invalidate()

        /*def confirmEmail = ConfirmEmail.where {
            securedKey == key &&
                    dateExpired > new Date()
        }.get()

        if(!confirmEmail) {
            flash.message = message(code: 'default.expired.link.message')
            redirect uri: '/login/auth'
            return
        }

        User user = confirmEmail.user

        user.person.email = confirmEmail.email
        user.person.save()

        user.enabled = true
        user.save()

        confirmEmail.delete(flush: true)*/

        render view: 'confirm'
    }

    //TODO 2019. 06. 09 user 정보수정 페이지
    def edit(Long id) {
        //respond userService.get(id)
        println "User info Edit Lodding..."
        User user = springSecurityService.currentUser

        respond(user)
    }

    //TODO 2019. 06. 09 user  정보 업데이트
    def update(User user) {
        println "User Info Update Lodding..."
        if (user == null) {
            notFound()
            return
        }

        try {
            userService.save(user)
        } catch (ValidationException e) {
            respond user.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect user
            }
            '*'{ respond user, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        userService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
