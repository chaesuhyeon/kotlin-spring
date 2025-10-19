## 게이트 웨이
### 게이트 웨이 동작 방식
1. member-service를 8081 포트로 실행한다.
2. gateway-service를 8080 포트로 실행한다.

```
# 회원 가입 요청 (게이트웨이를 통해)
curl -X POST http://localhost:8080/api/v1/members/join \
     -H "Content-Type: application/json" \
     -d '{"email":"gateway@email.com", "name":"Gateway User", "address":"Seoul"}'

# 회원 조회 요청 (게이트웨이를 통해)
curl http://localhost:8080/api/v1/members/1

```
3. 게이트웨이는 요청 경로가 /api/v1/members/1이므로 application.yml에서 설정한 Path 조건자에 따라 member-service-route가 일치 함을 확인 한다.
4. 게이트웨이는 이 요청을 uri에 명시된 http://localhost:8081로 그대로 전달(Proxy)한다.
5. member-service는 localhost:8081/api/v1/members/1로 요청을 받은 것처럼 처리하고 응답을 반환한다.
6. 게이트웨이는 member-service의 응답을 받아서 다시 클라이언트에게 전달한다.

