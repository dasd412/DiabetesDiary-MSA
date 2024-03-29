# DiabetesDiary-MSA


기존 모놀리식 프로젝트(https://github.com/dasd412/RemakeDiabetesDiaryAPI)를 MSA로 재설계한 프로젝트

## 실행 방법
1. msa/docker/development 디렉토리로 이동합니다.
2. docker-compose up 명령어를 실행합니다.
***

## 아키텍쳐

### development 환경
![architecture](https://velog.velcdn.com/images/dasd412/post/bb35a9e9-934d-429c-a98b-6603fa0e8e0c/image.png)

#### 세부 사항
1. 컨피그 서버는 외부 git 저장소에서 properties, yaml 설정을 로드합니다. 외부 저장소는 private repository이며 암호화되어 있습니다.  
  

2.  CUD 서비스는 CREATE, UPDATE ,DELETE 전용 서비스를 뜻합니다.  


3. 인증 서비스와 CUD 서비스는 H2를, 조회 서비스는 embed mongo를 사용하고 있습니다.  


4. 인증 서비스에서 '탈퇴' 이벤트가 발생하면, CUD 서비스와 조회서비스에 메시지를 보냅니다. 각각 writerChange, writerChangeToReader 토픽을 사용합니다. 보내는 메시지는 작성자 id입니다.  


5. CUD 서비스에서 생성, 변경, 삭제 이벤트가 발생하면, 인증 서비스와 조회 서비스 각각에 메시지를 보냅니다. 조회 서비스에 보내는 메시지는 DTO입니다.

#### 아쉬운 점 또는 아직 해결하지 못한 것
1. 유레카 서버가 가끔씩 커스텀 포트가 아닌 디폴트 포트에서 구동되는 문제가 있습니다.  


2. 컨트롤러 코드에서 resilience4j를 작성한 부분이 많이 중복됩니다.


3. OAuth가 아닌 로그인의 경우 게이트웨이를 정상 경유합니다. 하지만 OAuth 로그인의 경우 게이트웨이를 경유하면 인증에 실패하는 문제점이 있습니다.  


4. 구현하고 보니, CUD 서비스에서 인증 서비스로 이벤트로 보낼 필요가 없음을 알게 되었습니다.


***


## DDD 이벤트 스토밍
![ddd](https://velog.velcdn.com/images/dasd412/post/03122079-a3d8-4d79-b3da-18269a9b7944/image.png)

***

## 개발 히스토리
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
21. 읽기 전용 서비스에 카프카 메시징 도입  ✅
22. 도커 파일 내 이미지 용량 경량화 ✅

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

- API 명 : **일지 1개 조회**
  + 리소스 URI : /diary
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 일지 id
  + 응답 값 :  성공 시 200 코드 + 일지 DTO , 실패 시 400 (해당 id 없을 경우)또는 500 (그 외 문제)
  + 요청 예시
```
http://localhost:8062/view/diary/1

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```

- API 명 : **모든 공복혈당 조회**
  + 리소스 URI : /fpg/all
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 없음
  + 응답 값 :  성공 시 200 코드 + 공복 혈당 DTO
  + 요청 예시
```
http://localhost:8062/view/fpg/all

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```

- API 명 : **기간 내 공복혈당 조회**
  + 리소스 URI : /fpg/between
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 날짜 문자열
  + 응답 값 :  성공 시 200 코드 + 공복 혈당 DTO
  + 요청 예시
```
http://localhost:8062/view/fpg/between?startYear=2022&startMonth=02&startDay=01&endYear=2023&endMonth=02&endDay=10

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```
- API 명 : **모든 식사 혈당 조회**
  + 리소스 URI : /blood-sugar/all
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 없음
  + 응답 값 :  성공 시 200 코드 + 식사 혈당 DTO
  + 요청 예시
```
http://localhost:8062/view/blood-sugar/all

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```

- API 명 : **기간 내 식사 혈당 조회**
  + 리소스 URI : /blood-sugar/between
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 날짜 문자열
  + 응답 값 :  성공 시 200 코드 + 식사 혈당 DTO
  + 요청 예시
```
http://localhost:8062/view/blood-sugar/between?startYear=2022&startMonth=02&startDay=01&endYear=2023&endMonth=02&endDay=10

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값
```

- API 명 : **음식 페이징 조회**
  + 리소스 URI : /food/list
  + HTTP 메서드 : GET
  + 요청 헤더 : Authorization 헤더 (Bearer 액세스 토큰 값)
  + 요청 매개변수 : 식사 혈당 부등호 조건, 날짜 조건
  + 응답 값 :  성공 시 200 코드 + 페이징된 DTO 리스트
  + 요청 예시
```
http://localhost:8062/view/food/list

Authorization : Bearer 로그인 성공 시 받은 액세스 토큰 값

form-data :
    bloodSugar:100
    sign:ge
    startYear:2022
    startMonth:01
    startDay:01
    endYear:2023
    endMonth:01
    endDay:01
```
***

## 참고 서적
+ 스프링 마이크로 서비스 코딩 공작소 개정 2판
+ 도메인 주도 설계로 시작하는 마이크로 서비스 개발
+ 컨테이너 인프라 환경 구축을 위한 쿠버네티스 / 도커

***
## 소스 코드 참고

1. JWT : https://github.com/Development-team-1/just-pickup


***