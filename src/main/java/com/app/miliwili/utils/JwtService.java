package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
public class JwtService {
    /**
     * JWT 생성
     * @param userId
     * @return String
     */
    public String createJwt(long userId) {
        Date now = new Date();
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /**
     * Header에서 X-ACCESS-TOKEN 으로 JWT 추출
     * @return String
     */
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /**
     * JWT에서 userId 추출
     * @return long
     * @throws BaseException
     */
    public long getUserId() throws BaseException {
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        return (long) claims.getBody().get("userId", Integer.class);
    }
}