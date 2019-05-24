package xyz.ekkor

import grails.gorm.transactions.Transactional

@Transactional
class UserDataService {

    def getRealIp(def request) {
        String ipAddress = request.getHeader("X-Fowarded-For")

        if (!ipAddress)
            ipAddress = request.getHeader("X-Real-Ip")

        if (!ipAddress)
            ipAddress = request.getRemoteAddr()

        ipAddress
    }

    def saveUser(User userInstance) {

        userInstance.avatar.setPictureBySns(userInstance)

        // 유저 연관 정보 선 저장
        userInstance.person.save(failOnError: true)
        userInstance.avatar.save(failOnError: true)

        def result = userInstance.save(failOnError: true)

        // 유저 권한 생성
        UserRole.create(userInstance, Role.findByAuthority('ROLE_USER'), true)

        result
    }
}
