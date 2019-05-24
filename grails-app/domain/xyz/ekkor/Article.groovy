package xyz.ekkor

class Article {

    String title
    String tagString

    Integer viewCount = 0
    Integer voteCount = 0
    Integer noteCount = 0
    Integer scrapCount = 0
    boolean enabled = true
    boolean choice = false

    Category category

    Avatar author
    Avatar lastEditor

    boolean anonymity = false
    String aNickName

    Content selectedNote

    String createIp = ""

    Date dateCreated
    Date lastUpdated

    Integer best = 0

    boolean disabled
    boolean ignore

    boolean isRecruit = false
    boolean ignoreBest = false

    static belongsTo = [content: Content]

    static hasMany = [tags : Tag, notes: Content, articleNotices: ArticleNotice]

    static constraints = {
        title blank: false
        author nullable: true, bindable: false
        lastEditor nullable: true, bindable: false
        aNickName nullable: true
        viewCount bindable: false
        voteCount bindable: false
        noteCount bindable: false
        scrapCount bindable: false
        tags maxSize: 10, nullable: true
        tagString nullable: true
        notes bindable: false
        enabled bindable: false
        selectedNote nullable: true, bindable: false
        content nullable: true
        choice bindable: false, nullable: true
        createIp bindable: false, nullable: true
        ignoreBest bindable: false, nullable: true
        title validator: { val ->
            def spam = SpamWord.findAll().find { word ->
                val.contains(word.text)
            }

            if(spam) return ["default.invalid.word.message"]
        }
    }

    def updateViewCount(def i) {
        if(id != null) {
            executeUpdate("update Article set viewCount = viewCount+:i where id = :id",[i:i, id: id])
        }
    }

    def updateVoteCount(def i) {
        if(id != null) {
            executeUpdate("update Article set voteCount = voteCount+:i where id = :id",[i:i, id: id])
        }
    }

    String toString() {
        return "#${id} - ${title}"
    }
}
