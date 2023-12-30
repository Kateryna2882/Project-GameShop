package org.example.repository.dao;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordEncoderImpl extends PasswordEncoder {

    @Override
    public String encode(String password) {
        return DigestUtils.md5Hex(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {

        return encode(rawPassword).equals(encodedPassword);
    }
}
