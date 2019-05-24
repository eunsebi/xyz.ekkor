package xyz.ekkor

class Banner {

    BannerType type
    BannerContentType contentType = BannerContentType.IMAGE_FILE
    String name
    String image
    String url
    String target
    String tagDesktop
    String tagMobile
    boolean visible = true

    Date dateCreated
    Date lastUpdated

    int clickCount = 0
    int ipCount = 0

    static constraints = {
        target nullable: true
        image nullable: true
        url nullable: true
        tagDesktop nullable: true
        tagMobile nullable: true
    }
}
