### 1. H2 데이터베이스 설치 및 활용
- H2: 인메모리 데이터베이스/가볍고 편리.
- 인메모리 데이터: '메인 메모리(RAM)'에 저장하고 관리하는 데이터베이스. *기존의 데이터베이스(예: MySQL, Oracle)-> 하드디스크 같은 저장장치에 저장
- 설치 후 웹 콘솔을 통해 DB를 직접 확인하고 조작
- 장: 빠른 속도 / 단: 휘발성
1. H2 공식 사이트에서 설치 파일을 다운로드, 실행
2. 웹 콘솔에서 데이터베이스를 생성, SQL로 테이블을 만들고 데이터를 넣기.
3. 스프링 프로젝트에 H2와 JDBC(JPA) 관련 의존성을 추가.
4. application.properties에서 H2 연결 정보를 설정.
5. H2 콘솔을 통해 데이터베이스의 상태를 쉽게 확인하고 관리.

### 2. 순수 JDBC를 이용한 DB 접근
#### JDBC(Java Database Connectivity): 자바에서 데이터베이스에 직접 연결해 데이터를 저장하고 조회하는 가장 기본적인 방법
- DataSource 활용해 DB 연결을 설정, 
- SQL을 직접 작성해 데이터를 저장/조회 
- 반복적인 코드가 많음. 실무에서는 사용 x. 기본 원리를 이해 위함.
- 스프링의 DI -> 구현 클래스를 변경에서 기존 코드 수정x, 설정만 수정(개방-폐쇄 원칙, OCP).
#### DataSource란?
   커넥션을 획득할 때 사용하는 객체 
   - 스프링 부트가 DB 접속 정보 통해 DataSource 생성, 스프링 빈으로 등록. 
   - DI(의존성 주입)로 DataSource 사용 가능 
#### JDBC 코드 흐름
1. Connection 획득: DataSource에서 커넥션을 얻음
2. PreparedStatement 생성: SQL문을 준비
3. 파라미터 바인딩: SQL문의 ?에 값 입력
4. SQL 실행: executeUpdate(), executeQuery() 등으로 쿼리 실행
5. ResultSet 처리: 결과가 있으면 데이터를 꺼냄
6. 리소스 해제: ResultSet, PreparedStatement, Connection을 반드시 닫음
(try-catch-finally로 자원 누수를 막음).
#### 스프링 설정 변경
- 메모리 기반 저장소(MemoryMemberRepository) -> JdbcMemberRepository로 스프링 설정 파일(SpringConfig)을 수정
- 구현체만 수정하면 됨= 개방-폐쇄 원칙(OCP)

### 3. 스프링 통합 테스트
#### 통합 테스트란?
   - 실제 환경(스프링 컨테이너, 데이터베이스 등)과 최대한 비슷하게 모든 구성 요소 실행 -> 전체 시스템 확인 테스트. 
   - 이전: 최소 단위(예: 클래스, 메서드), 단위 테스트/ 통합 테스트: 실제 서비스처럼 스프링이 관리하는 빈, DB까지 모두 연동
#### 통합 테스트 코드 작성 방법
- 테스트 클래스 생성 : 기존의 단위 테스트 클래스(MemberServiceTest) 복사 -> MemberServiceIntegrationTest 클래스 
- [주요 애너테이션]
- @SpringBootTest, 스프링 컨테이너로 테스트
- @Transactional-> 테스트 이후 데이터베이스 변경사항 자동 롤백. 다음 테스트 영향 X.

### 4. 스프링 JdbcTemplate
- 순수 JDBC의 반복 코드를 줄여주는 스프링의 DB 접근 기술
- SQL은 직접 작성해야 하지만, 코드가 훨씬 간결해 짐.
- 생성자를 통한 의존성 주입 방식으로 설정.
- JdbcTemplate은 직접 @Autowired로 주입받는 방식 대신 DataSource를 생성자 주입받아 내부에서 JdbcTemplate을 생성

### 5. JPA(Java Persistence API)
- 반복적인 SQL 작성 없이 객체와 DB를 매핑해주는 ORM(Object Relational Mapping) 기술 
#### JPA 동작 방식 
스프링 부트 -> 설정된 DataSource 바탕으로 EntityManager를 자동으로 생성하고 스프링 빈으로 등록
#### 엔티티 매핑
JPA를 사용 전 먼저 엔티티 클래스를 정의해야 함. 엔티티: 데이터베이스 테이블과 매핑되는 자바 객체
- 기본적인 CRUD 및 쿼리 메서드(예: findByName())를 자동 제공
- 실무에서는 JPA, 스프링 데이터 JPA 함께 사용
- 데이터 저장 시 persist() 메서드 한 줄로 insert 쿼리와 id 값 설정까지 자동으로 처리
- 항상 트랜잭션 안에서 데이터 변경이 이루어져야 함. @Transactional 어노테이션을 통해 트랜잭션 관리 가능

### 6. 스프링 데이터 JPA
- JPA 위에서 동작하며, 인터페이스만 정의하면 구현체를 자동 생성 ->리포지토리에 구현 클래스 없어도 개발 가능
- CRUD, 페이징 등 다양한 기능을 메서드 이름만으로 사용 가능
- 복잡한 동적 쿼리는 Querydsl, 네이티브 쿼리, JdbcTemplate 등과 함께 사용
#### 스프링 데이터 JPA의 특징
- JpaRepository 인터페이스를 상속 -> 기본적인 CRUD 기능 자동 제공
- 메서드 이름만으로 쿼리를 생성 가능 (예: findByName). 
- 페이징 기능을 자동으로 제공.
- 스프링 데이터 JPA는 메서드 이름을 분석해 쿼리를 자동 생성: findByName → name으로 찾기/ findByNameAndId → name과 id로 찾기 

#### 실무에서의 활용
실무에서는 JPA와 스프링 데이터 JPA를 기본으로 사용/ 복잡한 동적 쿼리는 Querydsl이라는 라이브러리를 함께 사용. 이 조합으로 해결하기 어려운 쿼리는 네이티브 SQL이나 JdbcTemplate을 사용.