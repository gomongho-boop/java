package studycafe;

import studycafe.reservation.Reservation;
import studycafe.reservation.ReservationManager;
import studycafe.seat.Seat;
import studycafe.seat.SeatMap;
import studycafe.ticket.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class StudyCafeApp {

    private static SeatMap            seatMap     = new SeatMap();
    private static ReservationManager manager     = new ReservationManager(seatMap);
    private static TicketRecommender  recommender = new TicketRecommender();

    // ★ 한글 깨짐 수정: StandardCharsets.UTF_8 명시
    private static BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));

    private static final Object lock = new Object();

    public static void main(String[] args) {
        printBanner();

        while (true) {
            printMenu();
            String input = readLineSafe();

            switch (input) {
                case "1": menuTicketRecommend(); break;
                case "2": openSeatGui(null, null, false); break;
                case "3": menuReserve(); break;
                case "4": menuCancel(); break;
                case "5": menuMyReservation(); break;
                case "6": seatMap.showDashboard(); break;
                case "0":
                    System.out.println("\n이용해 주셔서 감사합니다. 프로그램을 종료합니다.");
                    try { reader.close(); } catch (IOException e) {}
                    return;
                default:
                    System.out.println("[안내] 0~6 사이의 번호를 입력해 주세요.");
                    break;
            }
        }
    }

    // ───────── 문자 스트림 안전 읽기 ─────────
    private static String readLineSafe() {
        try {
            String line = reader.readLine();
            return (line != null) ? line.trim() : "";
        } catch (IOException e) {
            return "";
        }
    }

    // ───────── 메뉴 1: 이용권 추천 ─────────
    private static void menuTicketRecommend() {
        System.out.println("\n----- 이용권 추천 -----");
        System.out.print("월 방문 횟수를 입력하세요 (1~30): ");
        int visits = readIntWithRange(1, 30);
        System.out.print("방문당 이용 시간(h)을 입력하세요 (1~24): ");
        int hours  = readIntWithRange(1, 24);
        System.out.print("이용할 개월 수를 입력하세요 (1~12): ");
        int months = readIntWithRange(1, 12);

        UserUsage user = new UserUsage(visits, hours, months);
        recommender.recommend(user);
    }

    // ───────── 메뉴 3: 좌석 예약 ─────────
    private static void menuReserve() {
        System.out.println("\n----- 좌석 예약 -----");

        System.out.print("예약자 이름: ");
        String name = readLineSafe();
        if (name.isEmpty()) { System.out.println("[안내] 이름을 입력해 주세요."); return; }

        System.out.println("\n이용권을 선택하세요:");
        System.out.println("  1. 시간권 (2,000원/h)");
        System.out.println("  2. 하루권 (15,000원)");
        System.out.println("  3. 기간권 (200,000원/월)");
        System.out.println("  4. 이용권 없이 예약");
        System.out.print("선택 (1~4): ");
        String ticketChoice = readLineSafe();

        Ticket ticket = null;
        switch (ticketChoice) {
            case "1":
                System.out.print("이용 시간(h)을 입력하세요 (1~24): ");
                int h = readIntWithRange(1, 24);
                ticket = new DayTicket(h);
                break;
            case "2":
                ticket = new FullDayTicket();
                break;
            case "3":
                System.out.print("이용 월 수를 입력하세요 (1~12): ");
                int d = readIntWithRange(1, 12);
                ticket = new PeriodTicket(d);
                break;
            case "4":
                ticket = null;
                break;
            default:
                System.out.println("[안내] 잘못된 선택입니다.");
                return;
        }

        System.out.println("[안내] 좌석 선택을 위한 GUI 창이 실행되었습니다.");
        openSeatGui(name, ticket, true);
    }

    private static void openSeatGui(String name, Ticket ticket, boolean isReservationMode) {
        SwingUtilities.invokeLater(() -> new InternalSeatFrame(name, ticket, isReservationMode));
        synchronized (lock) {
            try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // ───────── 메뉴 4: 예약 취소 ─────────
    private static void menuCancel() {
        System.out.println("\n----- 예약 취소 -----");
        manager.showAllReservations();
        if (manager.getReservations().isEmpty()) return;
        System.out.print("취소할 예약 번호를 입력하세요: ");
        int id = readInt();
        manager.cancelReservation(id);
    }

    // ───────── 메뉴 5: 내 예약 조회 ─────────
    private static void menuMyReservation() {
        System.out.println("\n----- 예약 내역 조회 -----");
        System.out.print("조회할 이름을 입력하세요: ");
        String name = readLineSafe();
        manager.showReservationsByName(name);
    }

    // ───────── 정수 입력 유틸 ─────────
    private static int readInt() {
        while (true) {
            try {
                int v = Integer.parseInt(readLineSafe());
                if (v < 0) { System.out.print("[안내] 0 이상의 숫자를 입력하세요: "); continue; }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("[안내] 숫자를 입력하세요: ");
            }
        }
    }

    private static int readIntWithRange(int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(readLineSafe());
                if (v < min || v > max) {
                    System.out.printf("[안내] %d ~ %d 사이의 숫자를 입력해주세요: ", min, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("[안내] 올바른 숫자를 입력하세요: ");
            }
        }
    }

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     스터디카페 통합 예약 시스템        ║");
        System.out.println("║     Study Cafe Reservation System    ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│  1. 이용권 추천               │");
        System.out.println("│  2. 빈 좌석 보기 (GUI)        │");
        System.out.println("│  3. 좌석 예약 (GUI)           │");
        System.out.println("│  4. 예약 취소                 │");
        System.out.println("│  5. 내 예약 조회               │");
        System.out.println("│  6. 전체 좌석 현황             │");
        System.out.println("│  0. 종료                     │");
        System.out.println("└─────────────────────────────┘");
        System.out.print("메뉴 선택: ");
    }


    // ═══════════════════════════════════════════════════════════════
    // GUI: 120석 전체 표시 (1층 탭 / 2층 탭)
    // ═══════════════════════════════════════════════════════════════
    private static class InternalSeatFrame extends JFrame {

        // 색상 상수
        private static final Color COLOR_STANDARD_FREE  = new Color(76,  175, 80);   // 초록 – 일반 빈석
        private static final Color COLOR_LAPTOP_FREE    = new Color(33,  150, 243);  // 파랑 – 노트북 빈석
        private static final Color COLOR_RESERVED       = new Color(244, 67,  54);   // 빨강 – 예약됨
        private static final Color COLOR_BTN_TEXT       = Color.WHITE;

        private final String  currentUserName;
        private final Ticket  currentTicket;
        private final boolean isReservationMode;

        public InternalSeatFrame(String userName, Ticket ticket, boolean isReservationMode) {
            this.currentUserName    = userName;
            this.currentTicket      = ticket;
            this.isReservationMode  = isReservationMode;

            setTitle(isReservationMode ? "좌석 선택 (예약 진행 중)" : "전체 좌석 현황 조회");
            // ★ 120석 모두 보이도록 넉넉한 크기
            setSize(1000, 620);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(8, 8));

            // 상단 안내 레이블
            JLabel infoLabel = new JLabel(
                isReservationMode ? "예약할 좌석을 클릭하세요." : "좌석 현황 확인용 화면입니다.",
                SwingConstants.CENTER);
            infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            add(infoLabel, BorderLayout.NORTH);

            // 범례 패널
            JPanel legendPanel = buildLegendPanel();
            add(legendPanel, BorderLayout.SOUTH);

            // ★ 탭 패널: 1층 / 2층
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("1층  (60석)", buildFloorPanel(1));
            tabbedPane.addTab("2층  (60석)", buildFloorPanel(2));
            add(tabbedPane, BorderLayout.CENTER);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    synchronized (lock) { lock.notify(); }
                }
            });

            setVisible(true);
        }

        /**
         * 한 층의 60개 좌석 버튼 패널 생성
         * 배열에 담긴 실제 자식 객체(StandardOpenSeat / LaptopOpenSeat)의
         * getSeatType() 오버라이딩 결과로 버튼 라벨 색상을 구분
         */
        private JPanel buildFloorPanel(int floor) {
            // 바깥 패널: 구역 레이블 + 그리드
            JPanel outerPanel = new JPanel(new BorderLayout(4, 6));
            outerPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

            // 구역 설명 (일반 45석 / 노트북 15석)
            JLabel zoneLabel = new JLabel(
                String.format("  %d층  |  1~45번 : 일반 오픈석  |  46~60번 : 노트북 오픈석", floor),
                SwingConstants.LEFT);
            zoneLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            outerPanel.add(zoneLabel, BorderLayout.NORTH);

            // 10열 × 6행 = 60석 그리드
            JPanel gridPanel = new JPanel(new GridLayout(6, 10, 6, 6));

            for (int i = 1; i <= 60; i++) {
                String seatNum = String.format("%dF-%02d", floor, i);
                // ★ SeatMap에서 실제 Seat 객체(자식 클래스)를 꺼냄 → 다형성 적용
                Seat seat = seatMap.getSeat(seatNum);
                gridPanel.add(buildSeatButton(seat, seatNum));
            }

            outerPanel.add(gridPanel, BorderLayout.CENTER);
            return outerPanel;
        }

        /** 좌석 하나짜리 버튼 생성 */
        private JButton buildSeatButton(Seat seat, String seatNum) {
            // ★ 다형성: 실제 자식 객체의 getSeatType() 호출
            //   → StandardOpenSeat → "[일반] 오픈석"
            //   → LaptopOpenSeat   → "[노트북] 오픈석"
            boolean isLaptop   = seat != null && seat.getSeatType().contains("노트북");
            boolean isReserved = isReservedSeat(seatNum);

            // 버튼 라벨: 좌석번호 + 종류 아이콘
            String label = isLaptop ? seatNum + "\n💻" : seatNum + "\n📖";
            JButton btn = new JButton("<html><center>"
                + seatNum + "<br><small>" + (isLaptop ? "노트북" : "일반") + "</small></center></html>");
            btn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            btn.setForeground(COLOR_BTN_TEXT);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);

            if (isReserved) {
                btn.setBackground(COLOR_RESERVED);
                btn.setToolTipText(seatNum + " – 예약됨");
            } else if (isLaptop) {
                btn.setBackground(COLOR_LAPTOP_FREE);
                btn.setToolTipText(seatNum + " – [노트북] 오픈석 (빈자리)");
            } else {
                btn.setBackground(COLOR_STANDARD_FREE);
                btn.setToolTipText(seatNum + " – [일반] 오픈석 (빈자리)");
            }

            final boolean finalReserved = isReserved;
            final String  finalSeatNum  = seatNum;
            btn.addActionListener(e -> handleSeatClick(finalSeatNum, finalReserved));

            return btn;
        }

        /**
         * 예약 여부 판별
         * ★ Map 컬렉션 적용: containsKey(seatNum)로 O(1) 즉시 조회
         *   기존: List를 for문으로 순회 + if로 좌석번호 비교
         *   변경: HashMap에 좌석번호(key)가 존재하는지 바로 확인
         */
        private boolean isReservedSeat(String seatNum) {
            if (manager == null) return false;
            // ★ containsKey(key): 해당 key가 Map에 있으면 true 반환
            return manager.isReserved(seatNum);
        }

        /** 좌석 클릭 처리 */
        private void handleSeatClick(String seatNum, boolean reserved) {
            if (reserved) {
                JOptionPane.showMessageDialog(this,
                    "이미 예약된 좌석입니다.", "예약 불가", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isReservationMode) {
                JOptionPane.showMessageDialog(this,
                    seatNum + " – 현재 예약 가능한 좌석입니다.", "좌석 정보", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                seatNum + " 좌석을 예약하시겠습니까?", "예약 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Reservation r = manager.reserveSeat(currentUserName, seatNum, currentTicket);
                if (r != null) {
                    JOptionPane.showMessageDialog(this,
                        currentUserName + "님의 예약이 완료되었습니다!\n배정 좌석: " + seatNum,
                        "예약 완료", JOptionPane.INFORMATION_MESSAGE);
                    r.showReservationInfo();
                }
                dispose();
            }
        }

        /** 하단 범례 */
        private JPanel buildLegendPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
            panel.add(makeLegendItem(COLOR_STANDARD_FREE, "일반 오픈석 (빈자리)"));
            panel.add(makeLegendItem(COLOR_LAPTOP_FREE,   "노트북 오픈석 (빈자리)"));
            panel.add(makeLegendItem(COLOR_RESERVED,      "예약됨"));
            return panel;
        }

        private JPanel makeLegendItem(Color color, String text) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            JLabel box = new JLabel("  ");
            box.setOpaque(true);
            box.setBackground(color);
            box.setPreferredSize(new Dimension(18, 18));
            item.add(box);
            item.add(new JLabel(text));
            return item;
        }
    }
}
