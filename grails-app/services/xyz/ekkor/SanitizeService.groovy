package xyz.ekkor

import grails.gorm.transactions.Transactional
import org.owasp.html.AttributePolicy
import org.owasp.html.PolicyFactory
import org.owasp.html.Sanitizers

@Transactional
class SanitizeService {

    private static final AttributePolicy INTEGER = new AttributePolicy() {
        @Override
        String apply(String elementName, String attributeName, String value) {
            int n = value.length()
            if (n == 0) {
                return null
            }
            for (int i = 0; i < n; ++i) {
                char ch = value.charAt(i)
                if (ch == '.') {
                    if (i == 0) {
                        return null
                    }
                    return value.substring(0, i)
                } else if (!('0') <= ch && ch <= '9') {
                    return null
                }
            }
            return value
        }
    }

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING


    def sanitize(def html) {
        POLICY.sanitize(html)
    }

    def serviceMethod() {

    }
}
