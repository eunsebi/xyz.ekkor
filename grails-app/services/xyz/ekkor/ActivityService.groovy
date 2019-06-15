package xyz.ekkor

import grails.gorm.transactions.Transactional

@Transactional
class ActivityService {

    /**
     * 좋아요(추천)
     * @param activityType
     * @param content
     * @param avatar
     * @return
     */
    def createByContent(ActivityType activityType, Content content, Avatar avatar) {
        Activity activity = create(activityType, content.article, content, avatar)

        activity
    }

    /**
     * 좋아요(추천) create
     * @param activityType
     * @param article
     * @param content
     * @param avatar
     * @return
     */
    def create(ActivityType activityType, Article article, Content content, Avatar avatar) {
        Activity activity = new Activity(article: article,
                avatar: avatar,
                content: content,
                type: activityType)
                //.save(flush: true, failOnError: true)
        .save()
        activity
    }

    /**
     * 신규 게시물 등록 정보 연동
     * @param activityType
     * @param article
     * @param avatar
     * @return
     */
    def createByArticle(ActivityType activityType, Article article, Avatar avatar) {
        Activity activity = create(activityType, article,
                article.content, avatar)
        activity
    }

    /**
     *  게시물 삭제 연동
     * @param article
     */
    def removeAllByArticle(Article article) {
        def activities = Activity.findAllByArticle(article)

        Activity.deleteAll(activities)
    }

    def serviceMethod() {

    }
}
