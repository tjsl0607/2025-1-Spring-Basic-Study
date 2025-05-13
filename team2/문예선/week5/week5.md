# AOP(Aspect Oriented Programming)
## 관점 지향 프로그래밍 패러다임 
    * 여러 부분에서 반복되는 공통 관심사 -> 핵심 비즈니스 로직에서 *분리, 모듈화*

## AOP가 필요한 상황

### 예시

모든 메서드의 호출 시간을 측정해야 하는 상황.

#### 문제

1. 시간 측정 로직을 모든 메서드에 각각 추가해야 함

* 2~4: 1번 때문에 파생되는 문제점 *

3. 시간 측정은 핵심 관심사항이 아닌 공통 관심사항임
3. 핵심 비즈니스 로직과 공통 관심사항이 섞여 유지보수가 어려워짐
4. 시간 측정 로직을 변경할 때 모든 코드를 찾아가며 수정해야 함

## AOP 적용 방법
- 공통 관심사항과 핵심 관심사항을 분리 가능.
- 시간 측정용 AOP 클래스 생성:

``` java
@Aspect
@Component
public class TimeTraceAop {
@Around("execution(* hello.hellospring..*(..))")
public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
long start = System.currentTimeMillis();
System.out.println("START: " + joinPoint.toString());
try {
return joinPoint.proceed();
} finally {
long finish = System.currentTimeMillis();
long timeMs = finish - start;
System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
}
}
}
```

### 주요 어노테이션 설명:

1. @Aspect: 이 클래스가 AOP로 사용됨을 명시

2. @Component: 스프링 빈으로 등록

3. @Around("execution( hello.hellospring..(..))")**: AOP를 적용할 대상을 지정하는 포인트컷 표현식


### AOP 동작 원리

1. 스프링은 AOP가 적용된 클래스의 프록시(가짜) 객체를 생성

2. 실제 호출 시 프록시 객체가 먼저 호출됨

3. 프록시는 공통 관심사항(예: 시간 측정)을 수행한 후

4. 실제 대상 객체의 메서드를 호출

5. 메서드 실행 후 다시 공통 관심사항의 나머지 로직을 수행