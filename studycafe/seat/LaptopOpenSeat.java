package studycafe.seat;

/** 노트북 오픈석 */
public class LaptopOpenSeat extends Seat {
    public LaptopOpenSeat(String seatNum) { super(seatNum); }

    @Override
    public String getSeatType() { return "[노트북] 오픈석"; }

    @Override
    public void displaySeatStatus() {
        String status = isReserved() ? "[X 예약됨]" : "[O 빈자리]";
        System.out.printf("  %-20s %s%n", getSeatType() + " " + getSeatNum(), status);
    }
}
