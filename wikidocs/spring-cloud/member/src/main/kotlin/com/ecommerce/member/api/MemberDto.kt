package com.ecommerce.member.api

// 회원 가입 요청 DTO
data class JoinMemberRequest(
    val email: String,
    val name: String,
    val address: String,
)

// 회원 정보 응답 DTO
data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
)
