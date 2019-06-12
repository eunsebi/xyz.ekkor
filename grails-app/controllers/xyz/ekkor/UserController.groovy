package xyz.ekkor

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService
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

    @Secured("USER_ADMIN")
    def show(Long id) {
        respond userService.get(id)
    }

    def create() {
        respond new User(params)
    }

    def save(User user) {
        if (user == null) {
            notFound()
            return
        }

        try {
            userService.save(user)
        } catch (ValidationException e) {
            respond user.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect user
            }
            '*' { respond user, [status: CREATED] }
        }
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
