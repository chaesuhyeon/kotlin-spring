package com.ecommerce.member.service

import com.ecommerce.member.api.JoinMemberRequest
import com.ecommerce.member.api.MemberResponse
import com.ecommerce.member.api.toEntity
import com.ecommerce.member.api.toResponse
import com.ecommerce.member.domain.MemberRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {
    @Transactional // 쓰기 트랜잭션 추가
    fun join(request: JoinMemberRequest): MemberResponse {

        // email 중복 검사
        if (memberRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("이미 가입된 이메일입니다: ${request.email}")
        }

        // request DTO -> Member Entity 변환 (확장 함수 사용)
        val newMember = request.toEntity()

        // DB에 저장 (영속화)
        val savedMember = memberRepository.save(newMember)

        // member Entity -> MemberResponse DTO 변환 후 반환
        return savedMember.toResponse()
    }

    fun getMember(memberId: Long): MemberResponse {

        // ID로 회원 조회 (코틀린 확장인 findByIdOrNull 사용)
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException("해당 ID의 회원을 찾을 수 없습니다: $memberId")

        // Entity -> DTO 변환 후 반환 (Entity가 없으면 예외 처리)
        return member.toResponse()
    }
}