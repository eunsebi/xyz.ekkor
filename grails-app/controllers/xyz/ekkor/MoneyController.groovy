package xyz.ekkor

import grails.plugin.springsecurity.SpringSecurityService

class MoneyController {

    SpringSecurityService springSecurityService

    def index() {
        User user = springSecurityService.loadCurrentUser()

        //println "id : " + user.getUsername()

        return [ pay: user.getUsername() ]
    }
}
