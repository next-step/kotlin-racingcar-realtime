# kotlin-racingcar-realtime
## 기능정리
- 자동차 입력받기
- 목표거리 입력받기
- 자동차별로 움직이기
  - 개별로 Coroutine 돌리기
  - 특정 range 범위내의 delay 
- 우승 조건 판단하기
  - 게임종료 변수 업데이트

공용자원
- ConcurrentHashMap<Car, Integer> 
- AtomicBoolean - 게임종료변수