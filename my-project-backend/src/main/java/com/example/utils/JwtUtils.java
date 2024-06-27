package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 需要导入依赖java-jwt
@Component
public class JwtUtils {

    @Resource
    StringRedisTemplate template;

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    public String createJwt(UserDetails details, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                // 每一个令牌生成一个随机的UUID
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id).withClaim("name", username).withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                // 令牌过期时间
                .withExpiresAt(expire)
                // 令牌获取时间
                .withIssuedAt(new Date()).sign(algorithm);
    }

    // 过期时间 = 颁发时间 + 7天
    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    // 解析token信息
    public DecodedJWT resolveJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        // 验证jwt是否合法, 是否被用户篡改
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            // 判断token是否被拉黑了
            if (this.isInvalidToken(verify.getId()))
                return null;
            // 判断令牌是否过期了
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    // 判断token是否合法
    public String convertToken(String headerToken) {
        // 不存在token或者开头不是以Bearer开头的
        if (headerToken == null || !headerToken.startsWith("Bearer")) return null;
        // 去除前缀Bearer
        return headerToken.substring(7);
    }

    // 解析用户方法
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("name").asString()).password("*****").authorities(claims.get("authorities").asArray(String.class)).build();
    }

    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }


    // 令牌失效的方法
    public boolean invalidateJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        } catch (JWTVerificationException e){
            return false;
        }
    }

    // 存入redis里面也记录还有几天失效
    private boolean deleteToken(String uuid, Date time)  {
        // 判断token是否失效
        if (this.isInvalidToken(uuid))
            return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    // 判断令牌是否已经过期/失效
    private boolean isInvalidToken(String uuid){
        // 查看redis黑名单是否有key
        return template.hasKey(Const.JWT_BLACK_LIST + uuid);
    }
}
