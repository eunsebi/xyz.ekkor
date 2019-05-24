package xyz.ekkor

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class BannerController {

    BannerService bannerService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond bannerService.list(params), model:[bannerCount: bannerService.count()]
    }

    def show(Long id) {
        respond bannerService.get(id)
    }

    def create() {
        respond new Banner(params)
    }

    def save(Banner banner) {
        if (banner == null) {
            notFound()
            return
        }

        try {
            bannerService.save(banner)
        } catch (ValidationException e) {
            respond banner.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'banner.label', default: 'Banner'), banner.id])
                redirect banner
            }
            '*' { respond banner, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond bannerService.get(id)
    }

    def update(Banner banner) {
        if (banner == null) {
            notFound()
            return
        }

        try {
            bannerService.save(banner)
        } catch (ValidationException e) {
            respond banner.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'banner.label', default: 'Banner'), banner.id])
                redirect banner
            }
            '*'{ respond banner, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        bannerService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'banner.label', default: 'Banner'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'banner.label', default: 'Banner'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
