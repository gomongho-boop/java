package studycafe.seat;

/** 일반 오픈석 */
public class StandardOpenSeat extends Seat {
    public StandardOpenSeat(String seatNum) { super(seatNum); }

    @Override
    public String getSeatType() { return "[일반] 오픈석"; }

    @Override
    public void displaySeatStatus() {
        String status = isReserved() ? "[X 예약됨]" : "[O 빈자리]";
        System.out.printf("  %-20s %s%n", getSeatType() + " " + getSeatNum(), status);
    }
}
