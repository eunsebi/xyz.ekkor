package xyz.ekkor

class ChangeLog {

    Article article
    Content content

    ChangeLogType type
    String md5
    String patch
    String text

    Avatar avatar

    int revision

    Date dateCreated

    static transients = ['text']

    static constraints = {
        content nullable: true
        avatar nullable: true
    }

    static mapping = {
        patch type: 'text'
    }
}
