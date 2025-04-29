# kotlin-racingcar-realtime [이민하]

## 프로젝트 궂호
```bash
└── src
    ├── main
    │   └── kotlin
    │       └── racingcar
    │           ├── Application.kt
    │           ├── controller
    │           │   └── RacingGameController.kt
    │           ├── model
    │           │   ├── RaceGame.kt
    │           │   ├── RacingCar.kt
    │           │   └── RandomMovingRule.kt
    │           ├── support
    │           │   └── InputValidator.kt
    │           └── view
    │               ├── InputView.kt
    │               └── OutputView.kt
    └── test
        └── kotlin
            └── racingcar
                ├── RaceGameTest.kt
                ├── RacingCarTest.kt
                └── RandomMovingRuleTest.kt

```

## Diagram
```bash
+----------------------+
|   RacingGameController|
|  - start()            |
+----------------------+
           |
           v
+----------------------+
|        RaceGame       |
|  - startRace()        |
|  - findWinner()       |
+----------------------+
           |
           v
+----------------------+
|      RacingCar        |
|  - moveForward()      |
|  - position      |
+----------------------+

InputView --> 사용자 입력
OutputView --> 경기 결과 출력
```

# 실행 흐름

## 1. 사용자 입력
- 자동차 이름 목록을 입력받는다.
    - 여러 이름을 쉼표(`,`)로 구분하여 입력한다.  
      예시: `pobi,woni,jun`
- 목표 거리를 입력받는다.
    - 자동차들이 도달해야 할 칸 수를 숫자로 입력한다.  
      예시: `5`

## 2. 초기화
- 입력된 이름으로 `RacingCar` 객체 리스트를 생성한다.
- 생성된 자동차 리스트를 `RaceGame` 객체에 등록한다.

## 3. 레이스 시작
- 각 자동차마다 별도의 코루틴을 생성하여 경주를 시작한다.
- 각 자동차 코루틴은 다음을 반복한다:
    - `0ms ~ 500ms` 랜덤 딜레이를 기다린다.
    - 한 칸 앞으로 이동한다 (`position` +1).
    - 이동 결과를 출력한다.
    - 아직 목표 거리에 도달하지 않았다면 다시 반복한다.

## 4. 우승자 판정
- 자동차 중 하나가 목표 거리에 도달하면 다음을 수행한다:
    - 해당 자동차를 `winner`로 기록한다.
    - `raceScope.cancel()`을 호출하여 모든 코루틴을 중단시킨다.
- 가장 먼저 도착한 자동차가 단독 우승자가 된다.

## 5. 경기 종료
- 모든 자동차 코루틴이 종료될 때까지 `joinAll()`로 대기한다.
- 우승자가 존재할 경우:
    - 우승자를 출력한다.