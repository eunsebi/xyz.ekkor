package xyz.ekkor

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        //GrailsApp.run(Application, args)
        final BannerGrailsApp app = new BannerGrailsApp(Application)
        app.run(args)
    }
}