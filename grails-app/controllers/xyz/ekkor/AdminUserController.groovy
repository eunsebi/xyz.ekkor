package xyz.ekkor

import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_ADMIN")
class AdminUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.order = params.order ?: 'desc'
        params.sort = params.sort ?: 'id'

        def userList, userCount

        if (params.query) {
            def users = User.where {
                username =~ "${params.query}" ||
                        person.fullName =~ "${params.query}" ||
                        person.email =~ "${params.query}" ||
                        avatar.nickname =~ "${params.query}"
            }
            userList = users.list(params)
            userCount = users.count()

        } else {
            userList = User.list(params)
            userCount = User.count()
        }

        respond userList, model: [userCount: userCount]

    }


}
