package studycafe.reservation;

import studycafe.seat.Seat;
import studycafe.seat.SeatMap;
import studycafe.ticket.Ticket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 예약 생성·취소·조회를 담당하는 관리 클래스
 *
 * ★ Map 컬렉션 적용
 *   - reservationMap : HashMap<String, Reservation>
 *     key   = 좌석번호 (예: "1F-03")
 *     value = Reservation 객체
 *   - 좌석번호로 예약 여부를 O(1)에 즉시 조회 가능
 *     (기존 List + for + if 순회 불필요)
 */
public class ReservationManager {

    private SeatMap seatMap;

    // ★ Map 컬렉션: 좌석번호(key) → 예약 정보(value)
    //   HashMap<K, V> — 수업 자료 13장 HashMap 선언 형식 참고
    private Map<String, Reservation> reservationMap;

    public ReservationManager(SeatMap seatMap) {
        this.seatMap        = seatMap;
        this.reservationMap = new HashMap<>();  // 빈 HashMap 초기화
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

        // ★ put(key, value): 좌석번호를 key로 예약 정보 저장
        reservationMap.put(seatNum, r);

        System.out.println("\n[완료] " + userName + "님, " + seatNum + " 좌석 예약 성공!");
        return r;
    }

    /**
     * 예약 취소 (예약 번호로)
     */
    public boolean cancelReservation(int reservationId) {
        // ★ entrySet()으로 Map 전체 순회하여 예약 번호 일치 항목 탐색
        for (Map.Entry<String, Reservation> entry : reservationMap.entrySet()) {
            Reservation r = entry.getValue();
            if (r.getReservationId() == reservationId) {
                r.getSeat().release();
                // ★ remove(key): 해당 좌석번호 키 삭제
                reservationMap.remove(entry.getKey());
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
        // ★ values()로 Reservation 객체만 순회
        for (Reservation r : reservationMap.values()) {
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
        if (reservationMap.isEmpty()) {
            System.out.println("  현재 예약이 없습니다.");
        } else {
            // ★ values()로 전체 Reservation 순회 출력
            for (Reservation r : reservationMap.values()) {
                r.showReservationInfo();
            }
        }
        System.out.println("=====================================");
    }

    /**
     * ★ Map의 containsKey(key)로 예약 여부를 O(1)에 즉시 확인
     *   GUI의 isReservedSeat()에서 사용
     */
    public boolean isReserved(String seatNum) {
        return reservationMap.containsKey(seatNum);
    }

    /**
     * 기존 코드 호환용: Map의 value 목록을 List로 반환
     * (getReservations()를 참조하는 다른 코드가 있을 경우 대비)
     */
    public List<Reservation> getReservations() {
        return new ArrayList<>(reservationMap.values());
    }
}
