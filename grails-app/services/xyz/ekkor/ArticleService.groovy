package xyz.ekkor

import grails.gorm.services.Service

@Service(Article)
interface ArticleService {

    Article get(Serializable id)

    List<Article> list(Map args)

    Long count()

    void delete(Serializable id)

    Article save(Article article)

    //Article save(Article article, Avatar author, Category category)

    //addVote(Article article, Content content, Avatar avatar, Integer point)

}