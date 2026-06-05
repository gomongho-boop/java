package studycafe.reservation;

import studycafe.seat.Seat;
import studycafe.ticket.Ticket;

/**
 * 예약 1건의 정보를 보관하는 클래스
 * - 예약자, 좌석, 선택한 이용권 포함
 */
public class Reservation {
    private static int nextId = 1;          // 예약 번호 자동 증가

    private int     reservationId;
    private String userName;
    private Seat   seat;
    private Ticket ticket;                  // 선택한 이용권 (null 가능)

    public Reservation(String userName, Seat seat, Ticket ticket) {
        this.reservationId = nextId++;
        this.userName      = userName;
        this.seat          = seat;
        this.ticket        = ticket;
    }

    public void showReservationInfo() {
        System.out.println("\n===== 예약 정보 =====");
        System.out.println("예약 번호 : " + reservationId);
        System.out.println("예약자   : " + userName);
        System.out.println("좌석 번호 : " + seat.getSeatNum()
                           + " (" + seat.getSeatType() + ")");
        if (ticket != null) {
            System.out.printf("이용권   : %s (%,d원)%n",
                              ticket.getName(), ticket.calculatePrice());
        }
        System.out.println("예약 상태 : 예약 완료");
        System.out.println("=====================");
    }

    // 외부 클래스(GUI 등)에서 데이터를 안전하게 읽어가기 위한 Getter 메서드들
    public int    getReservationId() { return reservationId; }
    public String getUserName()      { return userName; }
    public Seat   getSeat()          { return seat; }
    public Ticket getTicket()        { return ticket; }
}