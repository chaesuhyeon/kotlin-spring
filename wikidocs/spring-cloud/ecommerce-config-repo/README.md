## 실제로는 별도의 git repository로 관리 해야함

### 설정 파일 우선순위 규칙
- Config Server는 클라이언트의 application.name과 active profile에 따라, Git 저장소에서 여러 파일을 조합하여 최종 설정을 만든다.
- 파일 이름은 더 구체적일수록 높은 우선순위를 가진다.
- 만약 member-service가 prod 프로파일로 실행된다면, Config Server는 다음 파일들을 순서대로 읽어 설정을 덮어 쓴다. (아래로 갈수록 우선순위가 높음)

  - application.yml: 모든 서비스, 모든 프로파일에 적용되는 공통 설정 (가장 낮은 순위)
  - application-prod.yml: prod 프로파일로 실행되는 모든 서비스에 적용되는 공통 설정
  - member-service.yml: 프로파일과 관계없이 **member-service**에만 적용되는 설정
  - member-service-prod.yml: prod 프로파일로 실행되는 **member-service**에만 적용되는 가장 구체적인 설정 (가장 높은 순위)