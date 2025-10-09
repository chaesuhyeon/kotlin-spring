package com.ecommerce.member.api

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService {
    @Transactional // 2. 쓰기 트랜잭션 추가
    fun join(request: JoinMemberRequest): MemberResponse {

        // TODO: email 중복 검사
        // TODO: request DTO -> Member Entity 변환
        // TODO: memberRepository.save(member)
        // TODO: member Entity -> MemberResponse DTO 변환 후 반환

        println("회원 가입 로직 처리: ${request.name}")

        // 3. 임시 반환 데이터
        return MemberResponse(id = 1L, email = request.email, name = request.name)
    }

    fun getMember(memberId: Long): MemberResponse {

        // TODO: memberRepository.findByIdOrNull(memberId)
        // TODO: Entity -> DTO 변환 후 반환 (Entity가 없으면 예외 처리)

        println("회원 조회 로직 처리: $memberId")

        // 3. 임시 반환 데이터
        return MemberResponse(id = memberId, email = "test@email.com", name = "Test User")
    }
}