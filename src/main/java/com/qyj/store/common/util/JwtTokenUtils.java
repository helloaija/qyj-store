package com.qyj.store.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * JwtToken工具类
 * @author shitl
 */
public class JwtTokenUtils {
    private static Logger log = LoggerFactory.getLogger(JwtTokenUtils.class);

    private static PrivateKey PRIVATE_KEY = null;
    private static PublicKey PUBLIC_KEY = null;

    /** 证书名 */
    private static String JKS_NAME = "stl-jwt.jks";
    /** 别名 */
    private static String JKS_ALIAS = "stl-jwt";
    private static String KEY_PASS = "123456";
    private static String STORE_PASS = "123456";

    static {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream inputStream = new ClassPathResource(JKS_NAME).getInputStream();
            keyStore.load(inputStream, STORE_PASS.toCharArray());
            PRIVATE_KEY = (PrivateKey) keyStore.getKey(JKS_ALIAS, KEY_PASS.toCharArray());
            PUBLIC_KEY = (PublicKey) keyStore.getCertificate(JKS_ALIAS).getPublicKey();
        } catch (Exception e) {
            log.error("JwtTokenUtils exception: ", e);
        }
    }

    /**
     * 根据subject字符串生成jwt
     * @param subject
     * @return
     */
    public static String generateToken(String subject) {
        return Jwts.builder().claim("random", Math.random()).setSubject(subject)
                .signWith(SignatureAlgorithm.RS256, PRIVATE_KEY).compact();
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public static String parseToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(PUBLIC_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
