package xyz.ekkor

class Activity {

    Avatar avatar
    ActivityType type
    ActivityPointType pointType = ActivityPointType.NONE
    Article article
    Content content
    Integer point = 0

    Date dateCreated
    Date lastUpdated

    static constraints = {
        point bindable:false
    }
}
