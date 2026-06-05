package studycafe.seat;

/**
 * 120석(1층 60석 + 2층 60석) 전체 좌석 배열 관리
 * - 각 층: 1~45번 일반석, 46~60번 노트북석
 */
public class SeatMap {
    private Seat[] seats;
    private int    totalSeats = 120;

    public SeatMap() {
        seats = new Seat[totalSeats];
        int index = 0;
        for (int floor = 1; floor <= 2; floor++) {
            for (int i = 1; i <= 60; i++) {
                String seatNum = String.format("%dF-%02d", floor, i);
                seats[index++] = (i <= 45)
                        ? new StandardOpenSeat(seatNum)
                        : new LaptopOpenSeat(seatNum);
            }
        }
    }

    /** 좌석 번호로 좌석 객체 반환, 없으면 null */
    public Seat getSeat(String seatNum) {
        for (Seat s : seats)
            if (s.getSeatNum().equalsIgnoreCase(seatNum)) return s;
        return null;
    }

    /** 특정 층의 빈 좌석 개수 */
    public int countAvailable(int floor) {
        int count = 0;
        String prefix = floor + "F";
        for (Seat s : seats)
            if (s.getSeatNum().startsWith(prefix) && !s.isReserved()) count++;
        return count;
    }

    /** 전체 대시보드 출력 */
    public void showDashboard() {
        System.out.println("\n========== 실시간 좌석 현황 (총 120석) ==========");
        for (int floor = 1; floor <= 2; floor++) {
            int avail = countAvailable(floor);
            System.out.printf("%n[ %d층 오픈 스터디존 (60석) — 빈자리 %d석 ]%n", floor, avail);
            String prefix = floor + "F";
            int col = 0;
            for (Seat s : seats) {
                if (s.getSeatNum().startsWith(prefix)) {
                    s.displaySeatStatus();
                    col++;
                    if (col % 15 == 0) System.out.println(); // 15석마다 줄 구분
                }
            }
        }
        System.out.println("==================================================");
    }

    /** 빈 좌석만 출력 */
    public void showAvailableSeats() {
        System.out.println("\n---------- 예약 가능한 좌석 ----------");
        boolean found = false;
        for (int floor = 1; floor <= 2; floor++) {
            System.out.printf("[ %d층 ]%n", floor);
            String prefix = floor + "F";
            for (Seat s : seats) {
                if (s.getSeatNum().startsWith(prefix) && !s.isReserved()) {
                    System.out.printf("  %s (%s)%n", s.getSeatNum(), s.getSeatType());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("  현재 예약 가능한 좌석이 없습니다.");
        System.out.println("--------------------------------------");
    }

    public int getTotalSeats() { return totalSeats; }
}
