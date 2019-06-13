package xyz.ekkor.encoding

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.engine.event.EventType
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import xyz.ekkor.User

@CompileStatic
class UserPasswordEncoderListener extends AbstractPersistenceEventListener {

    @Autowired
    SpringSecurityService springSecurityService

    UserPasswordEncoderListener(final Datastore datastore) {
        super(datastore)
    }

    @Override
    protected void onPersistenceEvent(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof User) {
            User u = (event.entityObject as User)
            if (u.password && (event.eventType == EventType.PreInsert || (event.eventType == EventType.PreUpdate && u.isDirty('password')))) {
                event.getEntityAccess().setProperty("password", encodePassword(u.password))
            }
        }
    }

    @Override
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        eventType == PreUpdateEvent || eventType == PreInsertEvent
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    /*String encodePassword(String password, Object salt = null) {

        def input = password.getBytes()

        if (input == null || input.length <= 0) {
            return null
        }
        long nr = 1345345333L
        long add = 7
        long nr2 = 0x12345671L

        for (int i = 0; i < input.length; i++) {
            if (input[i] == ' ' || 	input[i] == '\t') {
                continue
            }
            nr ^= (((nr & 63) + add) * input[i]) + (nr << 8)
            nr2 += (nr2 << 8) ^ nr
            add += input[i]
        }

        nr = nr & 0x7FFFFFFFL;
        nr2 = nr2 & 0x7FFFFFFFL;

        StringBuilder sb = new StringBuilder(16);

        sb.append(Long.toString((nr & 0xF0000000) >> 28, 16))
                .append(Long.toString((nr & 0xF000000) >> 24, 16))
                .append(Long.toString((nr & 0xF00000) >> 20, 16))
                .append(Long.toString((nr & 0xF0000) >> 16, 16))
                .append(Long.toString((nr & 0xF000) >> 12, 16))
                .append(Long.toString((nr & 0xF00) >> 8, 16))
                .append(Long.toString((nr & 0xF0) >> 4, 16))
                .append(Long.toString((nr & 0x0F), 16))

        sb.append(Long.toString((nr2 & 0xF0000000) >> 28, 16))
                .append(Long.toString((nr2 & 0xF000000) >> 24, 16))
                .append(Long.toString((nr2 & 0xF00000) >> 20, 16))
                .append(Long.toString((nr2 & 0xF0000) >> 16, 16))
                .append(Long.toString((nr2 & 0xF000) >> 12, 16))
                .append(Long.toString((nr2 & 0xF00) >> 8, 16))
                .append(Long.toString((nr2 & 0xF0) >> 4, 16))
                .append(Long.toString((nr2 & 0x0F), 16))

        sb.toString()
    }*/

}
