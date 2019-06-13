package xyz.ekkor

class MainController {

    def mainService
    def randomService
    def grailsCacheAdminService

    def index() {
        log.info "Main Controller Index Loardding....."
        def mainBanners = Banner.where {
            type == BannerType.MAIN && visible == true
        }.list()

        def mainBanner = mainBanners ? randomService.draw(mainBanners) : null

        return [
                isIndex: true,
                weeklyArticles: mainService.getWeeklyArticles(),
                questionsArticles: mainService.getQnaArticles(),
                communityArticles: mainService.getCommunityArticles(),
                //columnArticle: mainService.getColumnArticle(),
                //techArticle: mainService.getTechArticle(),
                techArticles: mainService.getTechArticles(),
                //informArticles: mainService.getInformArticles(),
                //classArticles: mainService.getClassArticles(),
                //leaderArticles: mainService.getLeaderArticles(),
                //maintArticles: mainService.getMaintArticles(),
                //promoteArticles: mainService.getPromoteArticles(),
                mainBanner : mainBanner
        ]
    }

    def flush() {
        grailsCacheAdminService.clearAllCaches()
    }
}
