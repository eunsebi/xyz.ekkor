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

    //TODO 2019. 06. 17 스크랩 삭제
    def removeByArticle(ActivityType activityType, Article article, Avatar avatar) {

        Activity activity = Activity.createCriteria().get {
            and {
                eq('type', activityType)
                eq('article', article)
                eq('content', article.content)
                eq('avatar', avatar)
            }
        }

        unsetPoint(activity, article.content)

        remove(activity)
    }

    def unsetPoint(Activity activityInstance, Content contentInstance) {
        if(activityInstance.pointType == ActivityPointType.TAKE)
            activityInstance.avatar.updateActivityPoint(-activityInstance.point)
        else if(activityInstance.pointType == ActivityPointType.GIVE)
            contentInstance.author.updateActivityPoint(-activityInstance.point)
    }

    def remove(Activity activity) {
        activity.delete(flush: true, failOnError: true)
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
