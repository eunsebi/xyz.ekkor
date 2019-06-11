package xyz.ekkor

import grails.boot.GrailsApp
import groovy.transform.InheritConstructors
import org.springframework.core.env.Environment

@InheritConstructors
class BannerGrailsApp extends GrailsApp{

    protected void printBanner(final Environment environment) {
        // Create GrailsBanner instance.
        final GrailsBanner banner = new GrailsBanner()
        banner.printBanner(environment, Application, System.out)
    }

}
