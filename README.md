# kotlin-racingcar-realtime

## 기능 요구 사항
실시간으로 자동차들이 독립적으로 움직이며, 가장 먼저 목표 거리에 도달한 1대의 자동차가 승리한다.
- 각 자동차는 별도의 코루틴에서 독립적으로 움직인다.
- 각 자동차는 0ms ~ 500ms 사이의 랜덤한 시간 동안 delay한 후, 1칸 전진한다.
- 매번 움직일 때마다 현재 위치를 즉시 출력한다.

## 기능 구현 목록
- Car MODEL 구현
  - [x] 이름, 위치 property
  - [x] 전진 function(조건: 각 자동차는 0ms ~ 500ms 사이의 랜덤한 시간 동안 delay한 후, 1칸 전진)
  - [x] 위치출력 function
- VIEW
  - [x] 자동차 이름과 목표거리를 입력 받는 입력 console
  - [x] 최종우승 차량을 출력하는 결과 console
- CONTROLLER
  - [ ] Model - View 연결
  - [ ] 생성된 Car 객체관리
  - [ ] Racing 함수

```aiignore
경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)
car1,car2,car3
목표 거리를 입력하세요.
5

실행 결과
car2 : -
car1 : -
car3 : -
car2 : --
car2 : ---
car3 : --
car1 : --
car2 : ----
car3 : ---
car1 : ---
car2 : -----

car2가 최종 우승했습니다.

```
