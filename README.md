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
- [x] feat: 사용자의 입력값을 받는다. (InputView)
- [x] feat: 승리자를 출력한다.
- [x] feat: Enter를 입력하면 경기를 멈춘다.
- [x] feat: 사용자가 차를 추가하거나 다시 엔터를 눌려 경기를 재개할 수 있다.