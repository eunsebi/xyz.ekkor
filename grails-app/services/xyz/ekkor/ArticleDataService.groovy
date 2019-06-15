package xyz.ekkor

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import name.fraser.neil.plaintext.diff_match_patch

@Transactional
@Slf4j
class ArticleDataService {

    ActivityService activityService
    ArticleService articleService
    NotificationService notificationService

    def grailsApplication

    //TODO 2019. 05. 22 댓글 추가
    def addNote(Article article, Content content, Avatar author) {
        User user = User.findByAvatar(author)

        content.type = ContentType.NOTE
        content.author = author
        content.anonymity = content.anonymity
        content.aNickName = content.anonymity ? generateNickname(user, content.createIp) : author.nickname
        content.save(failOnError: true)

        article.noteCount++

        article.addToNotes(content)

        article.save(failOnError: true, flush: true)

        if(article.anonymity) {
            new Anonymous(
                    user: user,
                    article: article,
                    content: content,
                    type: ContentType.NOTE
            ).save(failOnError: true)
        } else {
            activityService.createByContent(ActivityType.NOTED, content, author)
        }
    }

    //TODO 2019. 05. 22 추천 포인트 추가
    def addVote(Article article, Content content, Avatar avatar, Integer point) {
        println "서비스 왔다."

        if(ContentVote.countByContentAndVoter(content, avatar) < 1) {
            ContentVote contentVote = new ContentVote()
            contentVote.point = point
            contentVote.article = article
            contentVote.content = content
            contentVote.voter = avatar
            contentVote.save(flush: true)

            ActivityType activityType = null

            if(content.type == ContentType.ARTICLE)
                activityType = contentVote.point > 0 ? ActivityType.ASSENTED_ARTICLE : ActivityType.DISSENTED_ARTICLE
            else if(content.type == ContentType.NOTE)
                activityType = contentVote.point > 0 ? ActivityType.ASSENTED_NOTE : ActivityType.DISSENTED_NOTE

            activityService.createByContent(activityType, content, avatar)

            // Notification 은 스케쥴로 발송
        }

        content
    }

    //TODO 2019. 05. 24 게시물 저장
    def save(Article article, Avatar author, Category category) {
        log.info("article save dataService 시작")
        println "article save dataService 시작"
        User user = User.findByAvatar(author)

        article.category = category
        article.author = author

        article.anonymity = category?.anonymity ?: false
        article.aNickName = article.anonymity ? generateNickname(user, article.createIp) : author.nickname

        article.content.type = ContentType.ARTICLE
        article.content.author = author

        article.content.save()

        log.info("article save start")
        //articleService.save(article)
        //article.save()

        //article.attach()

        if(article.anonymity) {
            new Anonymous(
                    user: user,
                    article: article,
                    content: article.content,
                    type: ContentType.ARTICLE
            ).save(failOnError: true)
        } else {
            activityService.createByArticle(ActivityType.POSTED, article, author)
        }
        log.info("end")

    }

    def generateNickname(User user, String ip) {

        String md5 = "${ip}${grailsApplication.config.grails.encrypt.key}${user.id}".encodeAsMD5()

        println md5

        int startIndex = user.id % 10

        return "A${md5.substring(startIndex, startIndex+7)}"
    }

    //TODO 2019. 06. 02 게시판 공지사항 List
    def getNotices(Category category) {

        def notices

        Category parentCategory = category.parent ?: null

        def articleNotices = ArticleNotice.findAllByCategory(category)

        if(parentCategory) {
            articleNotices += ArticleNotice.findAllByCategory(parentCategory)
        }

        if(articleNotices) {
            notices = Article.withCriteria() {
                eq('enabled', true)
                'in'('id', articleNotices*.articleId)
                order('id', 'desc')
            }.findAll()
        }

        notices

    }

    //TODO
    def changeLog(ChangeLogType type, Article articleInstance, Content contentInstance, String oldText, String text) {

        Avatar avatar = Avatar.load(springSecurityService.principal.avatarId)

        if(oldText) {

            def latestChangeLog = ChangeLog.createCriteria().get {
                eq('article', articleInstance)
                eq('content', contentInstance)
                order('revision', 'desc')
                maxResults(1)
            }

            def dmp = new diff_match_patch()

            def patches = dmp.patch_make(text, oldText)

            if(patches) {
                int revision = 1

                if(latestChangeLog) {
                    revision = latestChangeLog.revision+1
                }

                new ChangeLog(
                        type: type,
                        md5: oldText.encodeAsMD5(),
                        patch: dmp.patch_toText(patches),
                        article: articleInstance,
                        content: contentInstance,
                        avatar: avatar,
                        revision: revision).save()
            }
        }
    }

    //TODO 2019. 06. 19  게시물 수정
    def update(Article article, Avatar editor, Category category) {
        println "gggggggggggggggg"
        article.category = category

        article.lastEditor = editor
        article.content.lastEditor = editor

        article.content.save()
        article.save()
    }

    //TODO 2019. 06. 15  게시물 삭제
    def delete(Article article) {

        article.enabled = false

        article.save()

        /*activityService.removeAllByArticle(article)

        notificationService.removeFromArticle(article)

        removeNotices(article)

        Scrap.where { eq('article', article)}.deleteAll()

        Content content = article.content

        ChangeLog.where { eq('article', article)}.deleteAll()

        article.content = null
        article.save()

        ContentVote.where {
            eq('article', article)
        }.deleteAll()

        content.delete()

        if (article.anonymity) {
            Anonymous.where {
                eq('article', article)
                eq('content', article.content)
            }.deleteAll()
        }

        article.delete()*/
    }

    def removeNotices(Article article) {
        def query = ArticleNotice.where {
            eq('article', article)
        }
        query.deleteAll()
    }

    def saveNotices(Article article, User user, def categories) {

        categories.each {
            String it ->
                def category = Category.get(it)

                def existNotice = ArticleNotice.findAllByArticleAndCategory(article, category)

                if (!existNotice) {
                    def articleNotice = new ArticleNotice(article: article, user: user, category: category)
                    articleNotice.save()
                }
        }
    }

}
