package com.ecommerce.member.api

import com.ecommerce.member.domain.MemberRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

/**
 * 통합 테스트'는 'API 요청부터 DB 저장까지' 시스템의 전체 흐름을 테스트
 * MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션하고, DB는 H2 같은 인메모리 DB를 사용
 */
@SpringBootTest // 전체 스프링 컨텍스트를 로드 (통합 테스트)
@AutoConfigureMockMvc // MockMvc 주입
@Transactional // 테스트 후 롤백을 위한 트랜잭션 (DB 상태 유지)
@ActiveProfiles("test") // 'application-test.yml' (H2 DB 설정) 사용
class MemberControllerTest (
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper, // JSON 직렬화/역직렬화
    private val memberRepository: MemberRepository //  DB 상태 검증용
) : DescribeSpec() {

    // Kotest와 SpringBootTest를 연동
    override fun extensions() = listOf(SpringExtension)

    // 각 테스트 케이스 실행 전 DB 초기화 (선택적)
    init {
        beforeEach {
            memberRepository.deleteAll()
        }

        describe("POST /api/v1/members/join") {
            context("유효한 회원 가입 요청이 주어지면") {
                val request = JoinMemberRequest(
                    email = "integration@email.com",
                    name = "Integration User",
                    address = "Pangyo"
                )
                val jsonBody = objectMapper.writeValueAsString(request)

                it("200 OK와 함께 회원 정보를 반환하고, DB에 저장된다") {
                    // When: 실제 HTTP POST 요청 시뮬레이션
                    mockMvc.post("/api/v1/members/join") {
                        contentType = MediaType.APPLICATION_JSON
                        content = jsonBody
                    }.andExpect {
                        // Then 1: HTTP 응답 검증
                        status { isOk() } // 200 OK
                        jsonPath("$.id") { value(1L) } // (DB ID가 1로 시작)
                        jsonPath("$.email") { value(request.email) }
                    }

                    // Then 2: DB 상태 검증
                    val savedMember = memberRepository.findByEmail(request.email)
                    savedMember shouldNotBe null
                    savedMember?.name shouldBe request.name
                }
            }

            // (중복 이메일 요청 시 400 Bad Request 반환 테스트 등...)
        }
    }
}