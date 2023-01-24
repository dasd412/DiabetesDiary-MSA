# DiabetesDiary-MSA
MSA project by dasd412

***

## DDD 이벤트 스토밍
![ddd](https://velog.velcdn.com/images/dasd412/post/03122079-a3d8-4d79-b3da-18269a9b7944/image.png)

***

## 히스토리
1. 일지 서비스와 작성자 서비스 간단히 분할 ✅
2. 게이트 웨이 추가 및 상관 관계 Id 필터 추가 ✅
3. 일지 CUD 서비스의 Create 완성 및 테스트 ✅
4. 일지 CUD 서비스의 카프카 메시징 추가 ✅
5. 일지 R 서비스 뼈대 구성 ✅
6. ELK 및 집킨 추가 ✅
7. config server 설정 저장소 보안 추가 ✅
8. 헥사고날 아키텍쳐에 맞게 패키지 구조 리팩토링 ✅
9. AWS Lightsail 에 배포 실패(ELK 스택에 필요한 메모리 부족 문제 때문) ❌
10. 일지 CUD 서비스의 Update 완성 및 테스트 ✅
11. 일지 CUD 서비스의 Delete 완성 및 테스트 ✅
12. 회원 가입 완성 및 테스트 ✅
13. JWT 발급 및 검증 완성 및 테스트 ✅
14. JWT 리프레시 토큰 로직 완성 및 테스트 ✅
15. 로그아웃, 액세스 토큰 검증 로직 완성 및 테스트 ✅
16. 회원 탈퇴 및 전체 일지 삭제 완성 및 테스트 (카프카 비동기 메시지 활용)✅
17. 인증 서비스 내의 OAuth 인증 로직 완성 및 테스트 ✅
18. 게이트웨이를 경유할 경우의 OAuth 인증 실패 ❌
19.  Mongo DB + Querydsl 조합으로 읽기 전용 서비스 로직 완성 및 테스트 ✅
20. 읽기 전용 서비스에 페이징 로직 완성 ✅
21. 읽기 전용 서비스에 카프카 메시징 도입

***

## API 설계서 (dev 프로파일 기준)
***
* WriterService
    - API 명 : **회원 가입**
        + 리소스 URI : /signup
        + HTTP 메서드 : POST
        + 요청 매개변수 : 회원가입 정보 DTO
        + 응답 값 : 성공 시 200 코드, 실패 시 400 코드 (이메일 중복 또는 유저네임 중복)
        + ```
            http://localhost:8062/writer/signup
            
            {
             "username": "test22",
             "email":"test22@naver.com",
             "password":"testtest",
             "roles":[
             "USER",
             "TESTER",
             "DOCTOR"
             ]
            }
            ```
  - API 명 : **일반 로그인** 
      + 리소스 URI : auth/login
      + HTTP 메서드 : POST
      + 요청 매개변수 : 로그인 정보 DTO
      + 응답 값 : 성공 시 accesstoken과 만료 시간, 리스폰스 헤더에는 리프레시 토큰이 저장됨
      + ```
        http://localhost:8062/writer/auth/login
        {
         "username": "test22",
         "password":"testtest"
        }
        ```
  - API 명 : **구글 로그인**
    + 리소스 URI : /oauth2/authorization/google
    + HTTP 메서드 : GET
    + 특이 사항 : `http://localhost:8082/oauth2/authorization/google` 와 같이 게이트웨이를 경유하지 않아야만 OAuth 회원 가입 및 로그인이 됩니다. (아직 해결하지 못했습니다.)

  - API 명 : **토큰 재발급**
      + 리소스 URI : /refresh
      + HTTP 메서드 : POST 
      + 요청 헤더 : X-AUTH-TOKEN 헤더 (엑세스 토큰 값)와 쿠키 (이전에 발급한 리프레시 토큰)
      + 응답 값: 성공 시 새로운 리프레시 토큰 쿠키에 저장, 실패 시 기존 쿠키에 저장된 리프레시 토큰 제거
      + ```
        http://localhost:8062/writer/refresh
        
        헤더 예시 (차례로 KEY -VALUE)
        X-AUTH-TOKEN : 로그인 성공 시 받은 액세스 토큰 값
        Cookie : 로그인 성공 시 받은 리스폰스 헤더 내의 리프레시 토큰 값
        ```
  - API 명 : **로그아웃** 
    + 리소스 URI : auth/logout 
    + HTTP 메서드 : POST
    + 요청 헤더 : X-AUTH-TOKEN 헤더 (엑세스 토큰 값)
    + 응답 값 : 성공 시 200, 실패시 400
    + ```
      http://localhost:8062/writer/auth/logout
      
      X-AUTH-TOKEN : 로그인 성공 시 받은 액세스 토큰 값
      ```
  
  - API 명 : **회원 탈퇴**
    + 리소스 URI : /withdrawal
    + HTTP 메서드 : DELETE
    + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값), X-AUTH-TOKEN 헤더 (엑세스 토큰 값)
    + ```
      http://localhost:8062/writer/withdrawal
      
      Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
      X-AUTH-TOKEN : 로그인 성공 시 받은 액세스 토큰 값
      ```
  
  - API 명 : **토큰 검증**
    + 리소스 URI : /validation/access-token
    + HTTP 메서드 : GET
    + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
    + ```
      http://localhost:8062/writer/validation/access-token
      
      Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
      ```
***
* DiaryService
  - API 명 : **일지 작성**
    + 리소스 URI : /diabetes-diary
    + HTTP 메서드 : POST
    + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
    + 요청 매개변수 : 일지 작성 DTO
    + 응답 값 : 성공 시 200 코드 + 작성된 일지 id , 실패 시 400 (DTO 문제)또는 500 (그 외 문제) 
    + 요청 예시
```
http://localhost:8062/diary/diabetes-diary

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값

{
    "fastingPlasmaGlucose":100,
    "remark":"test",
    "year":"2022",
    "month":"12",
    "day":"11",
    "hour":"11",
    "minute":"25",
    "second":"43",
    "dietList":[
        {
            "eatTime":"LUNCH",
            "bloodSugar":100,
            "foodList":[
{
                    "foodName":"TOAST",
                    "amount":50.0,
                    "amountUnit":"g"
                },
                {
                    "foodName":"coke",
                    "amount":100.0,
                    "amountUnit":"mL"
                }
            ]
        }

    ]
}
```
  - API 명 : **일지 수정**
    + 리소스 URI : /diabetes-diary
    + HTTP 메서드 : PUT
    + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
    + 요청 매개변수 : 일지 수정 DTO
    + 응답 값 :  성공 시 200 코드 + 수정된 일지 id , 실패 시 400 (DTO 문제)또는 500 (그 외 문제)
    + 요청 예시
```
http://localhost:8062/diary/diabetes-diary

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값

{
    "diaryId":1,
    "fastingPlasmaGlucose":120,
    "remark":"test11",
    "year":"2022",
    "month":"12",
    "day":"11",
    "hour":"11",
    "minute":"25",
    "second":"43",
    "dietList":[
        {
            "dietId":1,
            "eatTime":"DINNER",
            "bloodSugar":200,
            "foodList":[
                {
                    "foodId":1,
                    "foodName":"test1",
                    "amount":5.0,
                    "amountUnit":"g"
                },
                {
                    "foodId":2,
                    "foodName":"test2",
                    "amount":6.50,
                    "amountUnit":"g"
                }
            ]
        }

    ]
}
```
- API 명 : **일지 삭제**
  + 리소스 URI : /diabetes-diary
  + HTTP 메서드 : DELETE
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 일지 삭제 id
  + 응답 값 :  성공 시 200 코드 + 삭제된 일지 id , 실패 시 400 (해당 id 없을 경우)또는 500 (그 외 문제)
  + 요청 예시
```
http://localhost:8062/diary/diabetes-diary/1

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```

***
* ReadDiaryService

***

## 참고 서적
+ 스프링 마이크로 서비스 코딩 공작소 개정 2판
+ 도메인 주도 설계로 시작하는 마이크로 서비스 개발

***
## 소스 코드 참고

1. JWT : https://github.com/Development-team-1/just-pickup


***