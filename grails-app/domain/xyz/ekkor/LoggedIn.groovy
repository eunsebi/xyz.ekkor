package xyz.ekkor

import groovy.transform.ToString

@ToString
class LoggedIn {

    User user
    Date dateCreated
    String remoteAddr

    static constraints = {
        remoteAddr nullable: true
    }
}
