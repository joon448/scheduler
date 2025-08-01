# Spring 일정 관리 프로젝트

## 목표  
Spring Boot를 활용하여 일정과 댓글을 관리하는 웹 애플리케이션 개발  
(JPA, Controller/Service/Repository 구조 기반)

---

## 개발 프로세스

### Lv 1. 일정 생성
- 일정 제목, 내용, 작성자명, 비밀번호 저장
- JPA Auditing으로 작성일/수정일 자동 처리
- 수정일 = 생성일로 초기화
- 비밀번호는 응답에서 제외됨

### Lv 2. 일정 조회
- **전체 일정 조회**: 작성자명 조건 O/X, 수정일 기준 내림차순
- **단건 일정 조회**: ID 기준 조회
- 응답 시 비밀번호 제외

### Lv 3. 일정 수정
- 제목, 작성자명 수정 가능
- 수정 요청 시 비밀번호 필요
- 작성일 유지, 수정일 갱신
- 비밀번호 일치하지 않으면 수정 실패

### Lv 4. 일정 삭제
- 삭제 요청 시 비밀번호 필요
- 일치하지 않으면 삭제 실패

---

## 도전 기능

### Lv 5. 댓글 생성
- 댓글 내용, 작성자명, 비밀번호 저장
- 해당 일정 ID에 연결
- 댓글 최대 10개
- JPA Auditing 적용

### Lv 6. 일정 단건 조회 시 댓글 포함
- 일정 + 연결된 댓글 리스트 반환

### Lv 7. 입력값 검증
- 제목: 필수, 최대 30자  
- 내용: 필수, 최대 200자  
- 댓글 내용: 필수, 최대 100자  
- 비밀번호 및 작성자명: 필수  
- 비밀번호 불일치 시 적절한 오류코드 및 메시지 반환  

---

## 실행 환경
- Java 17
- Spring Boot 3.x
- MySQL

---

## 디렉터리 구조

```
scheduler/
├── controller/             # API Controller
│   └── ScheduleController
│
├── dto/                    # Request/Response DTO
│   ├── comment/
│   │   ├── CommentRequestDto
│   │   └── CommentResponseDto
│   └── schedule/
│       ├── ScheduleRequestDto
│       ├── ScheduleResponseDto
│       ├── ScheduleDeleteRequestDto
│       ├── ScheduleUpdateRequestDto
│       └── ScheduleWithCommentsResponseDto
│
├── entity/                 # Entity
│   ├── BaseEntity (for auditing)
│   ├── Schedule
│   └── Comment
│
├── repository/             # JPA Repository
│   ├── ScheduleRepository
│   └── CommentRepository
│
├── service/                # Service layer
│   ├── ScheduleService
│   └── CommentService
│
└── SchedulerApplication    # SpringBoot main
```

---

## 주요 클래스 설명

| 클래스 | 설명 |
|--------|------|
| `Schedule` | 일정 정보를 담는 Entity |
| `Comment` | 일정에 달린 댓글 정보를 담는 Entity |
| `ScheduleController` | 일정 및 댓글 관련 API를 처리하는 컨트롤러 |
| `ScheduleService` | 일정 로직을 처리하는 서비스 |
| `CommentService` | 댓글 로직을 처리하는 서비스 |
| `BaseEntity` | 생성일, 수정일을 자동 처리하는 공통 추상 클래스 (JPA Auditing) |

---

## API 명세

| 기능 | Method | URL | 설명 |
|------|--------|-----|------|
| 일정 생성 | POST | `/schedules` | 새 일정 등록 |
| 전체 조회 | GET | `/schedules?name=` | 작성자명 필터 가능 (선택사항) |
| 단일 조회 | GET | `/schedules/{id}` | 댓글 포함 응답 |
| 일정 수정 | PATCH | `/schedules/{id}` | 제목/작성자명 수정, 비밀번호 필요 |
| 일정 삭제 | DELETE | `/schedules/{id}` | 비밀번호 필요 |
| 댓글 작성 | POST | `/schedules/{id}/comments` | 댓글 10개 제한, 비밀번호 필요 |


### 일정 생성
- **Method**: POST
- **URL**: /schedules
- **Request Body**:
```json
{
  "title": String,
  "content": String,
  "name": String,
  "password": String
}
```
- **Response**: 
  - 성공시: 200 OK
```json
{
  "id": Long,
  "title": String,
  "content": String,
  "name": String,
  "createdAt": DateTime,
  "modifiedAt": DateTime
}
```
  - 실패시:
    - 400 Bad Request: 필수값이 없는 경우, 길이 제한을 초과한 경우

---

### 전체 일정 조회
- **Method**: GET
- **URL**: /schedules?name=
- **Response**:
  - 성공시: 200 OK
```json
[
  {
    "id": Long,
    "title": String,
    "content": String,
    "name": String,
    "createdAt": DateTime,
    "modifiedAt": DateTime
  }
]
```
  - 조건에 맞는 결과가 없을 경우:
```json
[]
```

---

### 단일 일정 조회
- **Method**: GET
- **URL**: /schedules/{id}
- **Response**:
  - 성공시: 200 OK
```json
{
  schedule: {
    "id": Long,
    "title": String,
    "content": String,
    "name": String,
    "createdAt": DateTime,
    "modifiedAt": DateTime
  }
  comments: [
    //...
  ]
}
```
  - 실패시:
    - 404 Not Found: 해당 ID가 존재하지 않을 경우

---

### 일정 수정
- **Method**: PATCH
- **URL**: /schedules/{id}
- **Request Body**:
```json
{
  "title": String, 
  "name": String,
  "password": String
}
```
- **Response**:
  - 성공시: 200 OK
```json
{
  "id": Long,
  "title": String,
  "content": String,
  "name": String,
  "createdAt": DateTime,
  "modifiedAt": DateTime
}
```
  - 실패시:
    - 404 Not Found: ID가 존재하지 않음
    - 400 Bad Request: 필수값 누락, 길이 제한 초과, title&name 둘 다 없는 경우
    - 401 Unauthorized: 비밀번호 불일치

---

### 일정 삭제
- **Method**: DELETE
- **URL**: /schedules/{id}
- **Request Body**:
```json
{
  "password": String
}
```
- **Response**:
  - 성공시: 200 OK
  - 실패시:
    - 404 Not Found: ID가 존재하지 않음
    - 400 Bad Request: 필수값 누락
    - 401 Unauthorized: 비밀번호 불일치



---

## ERD

### 1) 필수 과제 ERD

<img width="460" height="242" alt="ScheduleApp (2)" src="https://github.com/user-attachments/assets/65b35eeb-fb8a-4a38-a8f4-61c0796b120d" />


### 2) 도전 과제 ERD

<img width="730" height="242" alt="ScheduleApp (3)" src="https://github.com/user-attachments/assets/cacab463-e2f8-4fca-b0d8-431d966e9069" />

---

## 개발 중 해결한 문제
#### 생성자 주입 오류 (final 누락)
- @RequiredArgsConstructor 사용 시 final 누락으로 생성자 주입이 되지 않던 문제 해결

#### 예외 처리 방식 개선
- ResponseEntity 대신 ResponseStatusException 사용으로 코드 간결화 및 일관성 확보

#### modifiedAt 값 미반영
- @LastModifiedDate가 반영되지 않아 repository.flush()로 수동 반영

#### 기능별 Request DTO 분리
- 생성/수정/삭제 요청을 명확하게 구분하여 유지보수성과 가독성 향상

#### 공통 검증 로직 분리
- 중복 유효성 검사 로직을 validate 메서드로 분리해 재사용성과 가독성 향상

#### 일정 삭제 시 댓글 자동 삭제
- 일정 삭제 시 해당 일정의 댓글도 함께 삭제되도록 로직 추가

#### 에러 메시지 클라이언트 응답 설정
- application.yml에 아래 설정 추가하여 클라이언트에 상세 에러 메시지 전달
```yaml
server:
  error:
    include-message:always
```

---

## 학습 포인트
- Spring MVC 구조 이해 (Controller-Service-Repository)
- DTO와 Entity 분리
- JPA Auditing
- 예외 처리 (`ResponseStatusException`)
