package xyz.ekkor

import grails.gorm.services.Service

@Service(Banner)
interface BannerService {

    Banner get(Serializable id)

    List<Banner> list(Map args)

    Long count()

    void delete(Serializable id)

    Banner save(Banner banner)

}