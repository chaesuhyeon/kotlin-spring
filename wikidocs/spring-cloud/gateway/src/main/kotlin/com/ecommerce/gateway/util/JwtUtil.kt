package com.ecommerce.gateway.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil(@Value("\${jwt.secret}") secret: String) {

    // application.yml의 secret 값을 HS256 알고리즘에 맞는 SecretKey 객체로 변환
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    /**
     * 토큰에서 모든 Claim(정보) 추출
     */
    fun getAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * 토큰에서 회원 ID(subject) 추출
     */
    fun getMemberId(token: String): Long {
        return getAllClaims(token).subject.toLong()
    }

    /**
     * 토큰 만료 여부 확인
     */
    fun isTokenExpired(token: String): Boolean {
        return getAllClaims(token).expiration.before(Date())
    }

    /**
     * 토큰에서 역할(roles) 정보 추출
     */
    @Suppress("UNCHECKED_CAST")
    fun getRoles(token: String): List<String> {
        return getAllClaims(token)["roles"] as List<String>? ?: emptyList()
    }
}