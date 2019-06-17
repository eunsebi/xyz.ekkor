package xyz.ekkor

import grails.gorm.transactions.Transactional

class Article {

    transient articleDataService

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

    static transients = ['disabled', 'ignore']

    static mapping = {
        cache true
        notes sort: 'id', order: 'asc'
        sort id: 'desc'
        best formula: "view_count + vote_count * 500 + note_count * 50"
    }

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

    def beforeInsert() {
        if(anonymity) {
            author = null
            content.anonymity = true
            content.author = null
            content.aNickName = aNickName
        }
        updateTag()
    }

    def beforeUpdate() {
        if(anonymity) {
            author = null
            lastEditor = null
            content.anonymity = true
            content.aNickName = aNickName
        }
        if(isDirty('tagString')) {
            updateTag()
            articleDataService.changeLog(ChangeLogType.TAGS, this, content, this.getPersistentValue('tagString'), tagString)
        }
        if(isDirty('title')) {
            articleDataService.changeLog(ChangeLogType.TITLE, this, content, this.getPersistentValue('title'), title)
        }
    }

    def beforeDelete() {
        /*if(tags) {
            tags.each { tag ->
                tag.taggedCount--
                tag.save()
            }
        }*/
    }

    def getDisplayAuthor() {
        if(anonymity) {
            return new Avatar(
                    nickname: aNickName ?: "익명",
                    picture: '',
                    pictureType: AvatarPictureType.ANONYMOUSE,
                    activityPoint: null
            )
        } else {
            return author
        }
    }

    void updateTag() {

        def removedTags = tags ?: []

        if(tagString) {
            def tagNames = tagString.split(/[,\s]+/).toList().unique().findAll { !it.isEmpty() }

            tagNames.each { tagName ->
                tagName = tagName.toLowerCase()
                def tag = TagSimilarText.findByText(tagName)?.tag ?: Tag.findByName(tagName)

                if(tag == null) {
                    tag =  new Tag(name: tagName).save()
                } else {
                    if(!tags?.contains(tag))
                        tag.taggedCount++
                }

                addToTags(tag)
                removedTags -= tag
                tag.save()
            }

            tagString = tagNames.join(',')
        }

        removedTags.each { tag ->
            removeFromTags(tag)
            tag.taggedCount--
            tag.save()
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

    def updateScrapCount(def i) {
        if(id != null) {
            executeUpdate("update Article set scrapCount = scrapCount+:i where id = :id",[i:i, id: id])
        }
    }

    String toString() {
        return "#${id} - ${title}"
    }
}
