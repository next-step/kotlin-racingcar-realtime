# kotlin-racingcar-realtime

## 요구사항 정의
### Model
#### Car
- [x] feat: 자동차의 이름과 현재 위치를 관리한다.(Car)
- [x] feat: 자동차를 0~500ms 사이 랜덤 수로 1 전진한다. (Car-move)

#### Race
- [x] feat: 전체 자동차와 목표 거리를 관리한다.(Race)
- [x] feat: 한 대가 목표거리가 될 때 까지 자동차 여러 대를 동시에 move한다.

### Controller
- [x] feat: 경기를 시작한다. (RaceController)

### View
- [ ] feat: 사용자의 입력값을 받는다. (InputView)
- [ ] feat: 승리자를 출력한다.
- [ ] feat: Enter를 입력하면 경기를 멈춘다.
- [ ] feat: 사용자가 차를 추가하거나 다시 엔터를 눌려 경기를 재개할 수 있다.

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

(사용자 엔터 입력)
add car4
car4 참가 완료!

car4 : -
car1 : --
car2 : ----
car4 : --
car3 : ---
car1 : ---
car2 : -----

car2가 최종 우승했습니다.
