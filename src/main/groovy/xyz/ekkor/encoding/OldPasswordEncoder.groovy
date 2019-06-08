package xyz.ekkor.encoding

import org.springframework.security.crypto.password.PasswordEncoder

class OldPasswordEncoder implements PasswordEncoder{

    String encodePassword(String password, Object salt = null) {

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
    }

    @Override
    String encode(CharSequence rawPassword) {
        return encodePassword(rawPassword, null)
    }

    @Override
    boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodePassword(rawPassword,null).equals(encodedPassword)) {
            return true
        } else {
            return false
        }
    }
}
