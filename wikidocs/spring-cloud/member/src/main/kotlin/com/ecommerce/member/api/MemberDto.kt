package com.ecommerce.member.api

import com.ecommerce.member.domain.Member

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

// ====== 확장 함수 ====== //
/**
 * DTO -> Entity 변환
 */
fun JoinMemberRequest.toEntity(): Member {
    return Member(
        email = this.email,
        name = this.name,
        address = this.address
    )
}

/**
 * Entity -> DTO 변환
 */
fun Member.toResponse(): MemberResponse {
    return MemberResponse(
        id = this.id!!, // Entity의 id는 null일 수 없다고 확신 (DB에서 조회했거나 저장된 후이므로)
        email = this.email,
        name = this.name
    )
}