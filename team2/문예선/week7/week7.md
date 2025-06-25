## 9. 프로젝트 생성
- 스프링 부트 스타터 사이트 이용 -> 프로젝트 기본 구조 생성
- 핵심: 스프링의 도움 없이 순수한 자바 코드로만 비즈니스 로직 개발.

## 10. 비즈니스 요구사항과 설계
- 두 가지 핵심 비즈니스 도메인(회원과 주문)에 대한 요구사항 정의
- 역할(인터페이스)과 구현(구현 클래스)을 명확히 나누기
  - 목표: 향후 요구사항 변경에 유연하게 대처할 수 있도록 설계하는 것

## 11. 회원 도메인 설계
- 요구사항 : 회원 가입, 조회
  - 회원 분류: '일반(BASIC)', 'VIP'
- 회원 데이터 저장소: 아직 미확정 상태 (자체 DB나 외부 시스템 연동 가능성)

- 설계 구조 
  1. 역할(인터페이스): MemberService (회원 서비스 로직), MemberRepository (회원 데이터 저장)
  2. 구현(클래스): MemberServiceImpl, MemoryMemberRepository (메모리 기반 저장소)
  3. 클래스 다이어그램 -> 클래스 간 관계(정적), 객체 다이어그램-> 런타임 시 실제 객체 간 의존 관계

## 12. 회원 도메인 개발
- 설계에 따라 각 인터페이스, 구현 클래스 코딩

  - Member: 회원 정보를 담는 엔티티 (id, name, grade). 
  - MemberRepository: save(Member member), findById(Long memberId) 메서드를 가진 인터페이스. 
  - MemoryMemberRepository: HashMap을 사용해 회원 데이터를 메모리에 저장하는 구현체. 
  - MemberService: join(Member member), findMember(Long memberId) 메서드를 가진 인터페이스. 
  - MemberServiceImpl: MemberRepository에 의존하여 비즈니스 로직을 처리하는 구현체. 
    - 이 때 new MemoryMemberRepository() 코드로 구현 클래스를 직접 생성, 의존

## 13. 회원 도메인 실행과 테스트
- main 메서드를 이용한 초기 테스트 방식 -> 번거롭고 비효율적임 확인
- JUnit(단위 테스트 프레임워크) 도입, 테스트 코드 작성
  - @Test 어노테이션 사용
  - 'given-when-then' 패턴에 따라 테스트 구성
  - assertThat(검증 라이브러리)로 결과 효율적 확인

## 14. 주문과 할인 도메인 설계
- 요구사항
  - 회원은 상품 주문 가능.
  - 회원 등급에 따라 할인 정책(예: VIP는 1000원 고정 할인) 적용.
  - 할인 정책은 향후 변경될 가능성이 매우 높다.

- 설계 구조
  - 역할(인터페이스): OrderService (주문 생성), DiscountPolicy (할인 정책)
  - 구현(클래스): OrderServiceImpl, FixDiscountPolicy (고정 금액 할인)
  - OrderService는 주문 생성 요청을 받으면 MemberRepository에서 회원 조회, DiscountPolicy에 할인 계산을 위임하는 구조로 설계

## 15. 주문과 할인 도메인 개발
- 설계에 따라 주문과 할인 관련 인터페이스, 구현 클래스들 개발
- OrderServiceImpl 내부에서 MemberRepository와 DiscountPolicy 인터페이스에 의존
  - 실제로는 new MemoryMemberRepository()와 new FixDiscountPolicy()처럼 구현 객체 직접 생성해 사용

## 16. 주문과 할인 도메인 실행과 테스트
- JUnit 사용헤 주문 생성 및 할인 정책 적용 여부 테스트
- 핵심 문제점 발견: MemberServiceImpl이 MemberRepository 인터페이스뿐만 아니라 MemoryMemberRepository라는 구체적인 구현 클래스에도 직접 의존한다. 
  -  이는 의존관계 역전 원칙(DIP)을 위반한 것
  - 나중에 회원 저장소를 MemoryMemberRepository에서 다른 구현체(예: DbMemberRepository)로 변경하려면 MemberServiceImpl의 코드를 직접 수정해야 함
    - '변경에는 닫혀 있고 확장에는 열려 있어야 한다'(개방-폐쇄 원칙(OCP)) 또한 위반 
    - 스프링을 통해 해결 가능

## 17. 새로운 할인 정책 개발 및 적용의 문제점
- 기존의 고정 금액 할인 정책(FixDiscountPolicy) 외에 새로운 요구사항으로 정률 할인 정책(RateDiscountPolicy) 추가하는 상황 가정
- 새로운 RateDiscountPolicy 클래스를 만들기
  - 새로운 할인 정책 적용하기 위해 주문 서비스 담당 OrderServiceImpl의 코드를 직접 수정해야 하는 문제가 발생

```java
// AS-IS: 기존 코드
private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

// TO-BE: 새로운 정책 적용을 위해 코드 수정 필요
private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
```
- 클라이언트 코드가 구체적인 구현 클래스에 직접 의존하게 만듦
  - 좋은 객체 지향 설계 원칙인 **개방-폐쇄 원칙(OCP)**, **의존관계 역전 원칙(DIP)** 위반 
  - 정책이 변경될 때마다 클라이언트의 코드를 수정해야 하므로 유연성이 하락

## 18. 관심사의 분리와 AppConfig 리팩터링
- 문제 해결하기 위해 관심사 분리
  - 즉, 애플리케이션의 동작 방식 구성, 객체 생성, 연결하는 책임을 별도의 설정 클래스로 분리하기 
  - AppConfig 클래스 : 애플리케이션 실제 동작에 필요한 구현 객체 생성, 생성자 통해 의존관계 주입(DI)

``` java
// AppConfig.java
public class AppConfig {
public MemberService memberService() {
return new MemberServiceImpl(new MemoryMemberRepository());
}

    public OrderService orderService() {
        return new OrderServiceImpl(
            new MemoryMemberRepository(),
            new FixDiscountPolicy() // 할인 정책을 여기서 결정
        );
    }
}
``` 
- OrderServiceImpl은 DiscountPolicy 인터페이스에만 의존
- 어떤 구현체가 사용될지는 AppConfig가 결정 
- 이를 통해 DIP 원칙을 지킬 수 있음

## 19. 새로운 구조의 장점과 원칙 적용
- AppConfig를 도입한 새로운 구조에선 할인 정책 변경하고 싶을 때 AppConfig 코드 한 줄만 수정하면 됨
- OrderServiceImpl 같은 클라이언트 코드는 전혀 수정할 필요가 없어 OCP 원칙을 준수하게 됨
### 좋은 객체 지향 설계의 5가지 원칙(SOLID) 적용 확인
- SRP (단일 책임 원칙): 클라이언트 객체는 실행 책임만, AppConfig는 구성 책임만 맡게 되어 책임이 분리됨
- DIP (의존관계 역전 원칙): 클라이언트가 구체 클래스가 아닌 인터페이스에만 의존하게 됨
- OCP (개방-폐쇄 원칙): 새로운 기능을 확장(정책 추가)해도 사용 영역의 코드는 변경되지 않음

## IoC, DI, 그리고 컨테이너
- 이전에는 개발자가 직접 객체를 생성하고 의존관계를 연결
- AppConfig 도입 이후엔 제어의 흐름 역전됨 
- 프로그램의 제어 흐름을 AppConfig가 가져감. **제어의 역전(IoC)**

- AppConfig가 클라이언트 객체가 의존하는 구현 객체를 생성하고 연결함. **의존관계 주입(DI)**
  - AppConfig 같이 객체 생성, 의존관계 관리하는 존재 = DI 컨테이너

## 스프링으로 전환하기
- 순수 자바 코드로 구현했던 AppConfig와 DI 컨테이너의 역할을 스프링 프레임워크가 대신하도록 전환 
- 개발자가 직접 자바 코드로 관리하던 객체들을 스프링 컨테이너에 '스프링 빈'으로 등록
  - 스프링 컨테이너를 통해 필요한 객체(빈)를 찾아 사용 
  - DI/IoC 개념이 스프링만의 고유 기술이 아니며, 스프링이 좋은 객체지향 설계를 돕는다.