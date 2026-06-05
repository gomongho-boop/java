package studycafe.reservation;

import studycafe.seat.Seat;
import studycafe.seat.SeatMap;
import studycafe.ticket.Ticket;
import java.util.ArrayList;
import java.util.List;

/**
 * 예약 생성·취소·조회를 담당하는 관리 클래스
 */
public class ReservationManager {
    private SeatMap            seatMap;
    private List<Reservation>  reservations;  // 예약 목록

    public ReservationManager(SeatMap seatMap) {
        this.seatMap      = seatMap;
        this.reservations = new ArrayList<>();
    }

    /**
     * 좌석 예약
     * @return 성공 시 Reservation 객체, 실패 시 null
     */
    public Reservation reserveSeat(String userName, String seatNum, Ticket ticket) {
        Seat seat = seatMap.getSeat(seatNum);

        if (seat == null) {
            System.out.println("[오류] 존재하지 않는 좌석입니다: " + seatNum);
            return null;
        }
        if (seat.isReserved()) {
            System.out.println("[오류] 이미 예약된 좌석입니다: " + seatNum);
            return null;
        }

        seat.reserve();
        Reservation r = new Reservation(userName, seat, ticket);
        reservations.add(r);
        System.out.println("\n[완료] " + userName + "님, " + seatNum + " 좌석 예약 성공!");
        return r;
    }

    /**
     * 예약 취소 (예약 번호로)
     */
    public boolean cancelReservation(int reservationId) {
        for (Reservation r : reservations) {
            if (r.getReservationId() == reservationId) {
                r.getSeat().release();
                reservations.remove(r);
                System.out.println("[완료] 예약 번호 " + reservationId + " 취소 완료.");
                return true;
            }
        }
        System.out.println("[오류] 해당 예약 번호를 찾을 수 없습니다: " + reservationId);
        return false;
    }

    /**
     * 이름으로 예약 내역 조회
     */
    public void showReservationsByName(String userName) {
        System.out.println("\n---------- [" + userName + "] 예약 내역 ----------");
        boolean found = false;
        for (Reservation r : reservations) {
            if (r.getUserName().equals(userName)) {
                r.showReservationInfo();
                found = true;
            }
        }
        if (!found) System.out.println("  예약 내역이 없습니다.");
        System.out.println("---------------------------------------");
    }

    /** 전체 예약 목록 출력 */
    public void showAllReservations() {
        System.out.println("\n========== 전체 예약 목록 ==========");
        if (reservations.isEmpty()) {
            System.out.println("  현재 예약이 없습니다.");
        } else {
            for (Reservation r : reservations) r.showReservationInfo();
        }
        System.out.println("=====================================");
    }

    public List<Reservation> getReservations() { return reservations; }
}
