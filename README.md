## 코드리뷰 커뮤니티 - 쇼미더코드(SMTC)

<br/>

![](src/main/resources/static/images/header_logo.png)
> https://smtc.shop/


<br/>
<br/>

### 🏠 소개


| ![](https://i.imgur.com/bjw3nEH.png) |
|:--:|
| *메인페이지* |

<br/>

| ![](https://i.imgur.com/YBJGcYJ.png) |
|:--:|
| *코드리뷰 요청페이지* |



유저들간 코드리뷰를 할 수 있는 커뮤니티 사이트

<br/>
<br/>

### ⏲️ 개발기간
2021년 09월 23일(목) ~ 2021년 12월 09일(목)

<br/>
<br/>

### 🧙 맴버구성
[김대현](https://github.com/kimdh-hi) [김은아](https://github.com/eunag63) [심은철](https://github.com/scm1400)

<br/>
<br/>

### 📌


<br/>
<br/>

### 📌 핵심 기능
- 리뷰어를 지정하여 코드리뷰를 요청
- 리뷰어는 리뷰요청을 받아 답변하거나 거절
- 일반 사용자는 답변에 대한 평가를 수행
- 실시간 알림
  - 리뷰 등록시 지정된 리뷰어에게 실시간 알림 (알림 클릭시 답변페이지로 이동)
  - 리뷰 거절시 리뷰 요청자에게 실시간 알림 (코드리뷰 요청 상세페이지로 이동 - 삭제 or 리뷰어 변경)
  

<br/>
<br/>

### 📌 문제를 이렇게 해결했어요!
1. (Querydsl (+Mysql) Group by 성능 최적화) [https://velog.io/@dhk22/TIL-Day-65-Querydsl-Group-by]
2. (Querydsl Jpa exist 최적화)[https://velog.io/@dhk22/TIL-Day-66-Jpa-exist-%EC%B5%9C%EC%A0%81%ED%99%94]
3. (Querydsl 페이징 쿼리 최적화)[https://velog.io/@dhk22/TIL-Day-62]

<br/>
<br/>

### Environment

- `Java8`

### Prerequisite
- `SpringBoot`
    - `spring-boot-data-jpa`
    - `spring-boot-security`
    - `spring-boot-oauth2-client`
    - `querydsl`
    - `spring rest docs`
- `asciidoctor`
- `jjwt`
- `gson
- `h2`
- `mysql`

