package studycafe.seat;

/**
 * [추상 클래스] 좌석의 공통 속성 및 행동 정의
 * - 상속: StandardOpenSeat, LaptopOpenSeat
 */
public abstract class Seat {
    private String  seatNum;    // 좌석 번호 (예: 1F-01)
    private boolean isReserved; // 예약 여부

    public Seat(String seatNum) {
        this.seatNum    = seatNum;
        this.isReserved = false;
    }

    public void reserve() { this.isReserved = true; }
    public void release() { this.isReserved = false; }

    public String  getSeatNum()   { return seatNum; }
    public boolean isReserved()   { return isReserved; }

    // 하위 클래스에서 좌석 종류에 맞게 출력 (다형성)
    public abstract void displaySeatStatus();

    // 좌석 종류 이름 반환 (하위 클래스 구현)
    public abstract String getSeatType();
}
