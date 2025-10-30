package com.ecommerce.member.api

import com.ecommerce.member.service.MemberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @RefreshScope은 프록시를 이용하여 /refresh 이벤트가 발생했을 때, 해당 Bean을 파괴하고 새로운 설정 값으로 다시 생성(Re-initialize) 함
 * [실제로 재배포 없이 설정을 변경]
 * 모든 서비스(discovery, config, member)를 실행
 * GET http://localhost:8081/api/v1/members/greeting을 호출
 * 응답: "Hello, Member!"
 * config-repo Git 저장소의 member-service.yml 파일 내용을 수정하고 커밋/푸시
 * member.greeting: "Welcome, Refreshed Member!"
 * **member-service**의 Actuator 엔드포인트로 빈 POST 요청을 보냄 -> bash curl -X POST http://localhost:8081/actuator/refresh
 * 응답: ["member.greeting"] (변경이 감지된 설정 키 목록)
 * 다시 GET http://localhost:8081/api/v1/members/greeting을 호출
 * 응답: "Welcome, Refreshed Member!"
 * => 해당 방식은 member-service가 1대일 때는 괜찮지만, 100대로 확장된 상대라면? 100개의 인스턴스에 /refresh 요청을 보낼 순 없음 -> spring cloud bus 필요
 *  - spring cloud bus : kafka나 rabbitMQ 같은 메세지 브로커를 이용하여, 단 한번의 refresh 요청을 모든 마이크로 서비스 인스턴스에 변경 전파를 해줌
 */
@RefreshScope // '/actuator/refresh' 호출 시 이 Bean을 재생성하도록 지시 (git에 저장된 member-service.yml 설정 내용 변경 시 '/actuator/refresh' 호출)
@RestController
@RequestMapping("/api/v1/members")
class MemberController (
    private val memberService: MemberService
) {
/*
    ## config-repo/member-service.yml
    member:
        greeting: "Hello, Member!"
*/

    // git의 config-repo에 있는 member-service.yml에 적여있는 설정 값을 주입받음
    @Value("\${member.greeting}")
    private lateinit var greeting: String

    @GetMapping("/greeting")
    fun getGreeting(): String {
        return greeting
    }

    @PostMapping("/join")
    fun join(@RequestBody request: JoinMemberRequest): MemberResponse {
        // 2. 요청 DTO를 Service로 전달
        return memberService.join(request)
    }

    @GetMapping("/{memberId}")
    fun getMember(@PathVariable memberId: Long): MemberResponse {
        // 3. Path Variable을 Service로 전달
        return memberService.getMember(memberId)
    }
}