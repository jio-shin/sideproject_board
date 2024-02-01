#게시판 만들기

##사용된 기술

- Spring Boot
- Spring MVC
- Spring JDBC
- Mysql
- Thymeleaf 템플릿 엔진

````
                     Spring Core
                     Spring MVC                   Spring JDBC    MySQL
브라우저 ---- 요청 ---> Controller ----> Service ----> DAO ----> DB
        <--- 응답 --- 템플릿 <---           <----         <----
                      <------------ layer간에 데이터 전송은 DTO -->
````

##게시판 만드는 순서
1. controller와 템플릿
2. service(비지니스 로직)는 DAO를 사용해서 데이터를 CRUD한다. 