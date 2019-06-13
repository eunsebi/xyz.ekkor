package xyz.ekkor

import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j

@Slf4j
class FindUserController {

    UserDataService userDataService
    SpringSecurityService springSecurityService

    /*def beforeInterceptor = [action:this.&notLoggedIn]

    private notLoggedIn() {
        if(springSecurityService.loggedIn) {
            redirect uri: '/'
            return false
        }
    }*/

    def index() {
        log.info("User ID Find")
        render view: 'index'
    }

    def send(String email) {
        if (!email || email.isEmpty()) {
            flash.message = message(code: 'default.blank.message', args: [message(code: 'person.email.label', default: 'email')])
            redirect action: 'index'
            return
        }

        def persons = Person.findAllByEmail(email)

        if (!persons) {
            flash.message = message(code: 'email.not.found.message')
            redirect action: 'index'
            return
        }

        if (persons.size() > 1) {
            flash.message = message(code: 'email.duplicate.found.message')
            redirect action: 'index'
            return
        }

        def person = persons[0]

        def user = User.findByPerson(person)

        if (user.withdraw || user.accountLocked) {
            flash.message = message(code: 'email.not.found.message')
            redirect action: 'index'
            return
        }

        def now = new Date()

        def key = "${now.time}_${user.username}_${user.person.email}_${grailsApplication.config.grails.mail.key}".encodeAsSHA256().encodeAsBase64()

        //def key = userDataService.createConfirmEmail(user)

        render view:'/email/find', model: [user: user, key: key, grailsApplication: grailsApplication]

    }
}
