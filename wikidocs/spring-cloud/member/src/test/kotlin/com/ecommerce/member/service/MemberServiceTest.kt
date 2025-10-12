package com.ecommerce.member.service

import com.ecommerce.member.api.JoinMemberRequest
import com.ecommerce.member.api.toEntity
import com.ecommerce.member.domain.Member
import com.ecommerce.member.domain.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec // 1. BehaviorSpec 사용
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * '단위 테스트'는 다른 모든 외부 의존성을 격리하고, 오직 '해당 클래스'의 로직만 테스트
 * MemberService를 테스트할 때, MemberRepository (DB)는 '가짜(Mock)'로 대체
 */
class MemberServiceTest : BehaviorSpec({
    // 1. 테스트에 필요한 의존성(Repository)을 Mocking
    val memberRepository = mockk<MemberRepository>()

    // 2. 테스트 대상인 MemberService에 Mock 주입
    val memberService = MemberService(memberRepository)

    // 3. (Best Practice) 각 테스트 케이스 실행 후 Mock 초기화
    afterTest {
        clearMocks(memberRepository)
    }

    // --- 테스트 케이스 1: 회원 가입 (성공) ---
    Given("회원 가입 요청 정보가 주어졌을 때") {
        val request = JoinMemberRequest(
            email = "test@email.com",
            name = "Test User",
            address = "Seoul"
        )
        val requestEntity = request.toEntity()

        // Mocking 정의:
        // 1. 이메일 중복 검사는 'null' (중복 없음)을 반환
        every { memberRepository.findByEmail(request.email) } returns null
        // 2. save(member)가 호출되면, id가 1L로 채워진 member를 반환
        every { memberRepository.save(any()) } returns Member(
            email = request.email,
            name = request.name,
            address = request.address
        ).apply { id = 1L } // 오류남. 테스트 코드 편의상 임시로 작성
        // (더 좋은 방법은 Member의 private id를 reflection으로 세팅)

        When("회원 가입을 시도하면") {
            val response = memberService.join(request)

            Then("회원 정보가 반환되고, ID는 null이 아니어야 한다") {
                response.id shouldBe 1L
                response.email shouldBe request.email

                // save 함수가 1번 호출되었는지 검증
                verify(exactly = 1) { memberRepository.save(any()) }
            }
        }
    }

    // --- 테스트 케이스 2: 회원 가입 (실패 - 이메일 중복) ---
    Given("이미 존재하는 이메일로 회원 가입 요청이 주어졌을 때") {
        val request = JoinMemberRequest("duplicate@email.com", "Test", "Seoul")

        // Mocking 정의: 이메일 중복 검사 시 'Member' 객체 반환
        every { memberRepository.findByEmail(request.email) } returns Member("dup", "dup", "dup")

        When("회원 가입을 시도하면") {
            Then("IllegalArgumentException 예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    memberService.join(request)
                }
                exception.message shouldBe "이미 가입된 이메일입니다: ${request.email}"

                // save 함수는 절대 호출되면 안 됨
                verify(exactly = 0) { memberRepository.save(any()) }
            }
        }
    }

    // (getMember에 대한 테스트 케이스도 유사하게 작성...)
})