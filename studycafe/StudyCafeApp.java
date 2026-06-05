package studycafe;

import studycafe.reservation.Reservation;
import studycafe.reservation.ReservationManager;
import studycafe.seat.SeatMap;
import studycafe.ticket.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class StudyCafeApp {

    private static SeatMap            seatMap   = new SeatMap();
    private static ReservationManager manager   = new ReservationManager(seatMap);
    private static TicketRecommender  recommender = new TicketRecommender();
    
    // [핵심 변경] Scanner 대신 (BufferedReader) 선언
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    
    // GUI 제어용 동기화 락 객체
    private static final Object lock = new Object();

    public static void main(String[] args) {
        printBanner();

        while (true) {
            printMenu();
            String input = readLineSafe();

            switch (input) {
                case "1":
                    menuTicketRecommend();
                    break;
                case "2":
                    openSeatGui(null, null, false);
                    break;
                case "3":
                    menuReserve();
                    break;
                case "4":
                    menuCancel();
                    break;
                case "5":
                    menuMyReservation();
                    break;
                case "6":
                    seatMap.showDashboard();
                    break;
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

    // ───────── 문자 스트림 안전 읽기 유틸 ─────────
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
        int hours = readIntWithRange(1, 24);
        System.out.print("이용할 개월 수를 입력하세요 (1~12): ");
        int days = readIntWithRange(1, 12);

        UserUsage user = new UserUsage(visits, hours, days);
        recommender.recommend(user);
    }

    // ───────── 메뉴 3: 좌석 예약 (GUI 연동) ─────────
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
        SwingUtilities.invokeLater(() -> {
            new InternalSeatFrame(name, ticket, isReservationMode);
        });

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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

    // ═════════════════════════════════════════════════════════════════════════
    // 🖥️ 최상위 컨테이너 JFrame 구현 스태틱 클래스 (기존 로직 유지)
    // ═════════════════════════════════════════════════════════════════════════
    private static class InternalSeatFrame extends JFrame {
        private String currentUserName;
        private Ticket currentTicket;
        private boolean isReservationMode;

        public InternalSeatFrame(String userName, Ticket ticket, boolean isReservationMode) {
            this.currentUserName = userName;
            this.currentTicket = ticket;
            this.isReservationMode = isReservationMode;

            setTitle(isReservationMode ? "좌석 선택 (예약 진행 중)" : "전체 좌석 현황 조회");
            setSize(500, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            JLabel infoLabel = new JLabel(isReservationMode ? "예약할 좌석을 클릭하세요." : "좌석 현황 확인용 화면입니다.", SwingConstants.CENTER);
            infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            add(infoLabel, BorderLayout.NORTH);

            JPanel gridPanel = new JPanel(new GridLayout(5, 5, 10, 10));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (int i = 1; i <= 25; i++) {
                String seatNum = String.format("1F-%02d", i);
                JButton seatButton = new JButton(seatNum);
                seatButton.setFont(new Font("Arial", Font.BOLD, 12));

                boolean reservedCheck = false;
                if (manager != null && manager.getReservations() != null) {
                    for (Reservation r : manager.getReservations()) {
                        if (r.getSeat() != null && r.getSeat().getSeatNum().equalsIgnoreCase(seatNum)) {
                            reservedCheck = true;
                            break;
                        }
                    }
                }

                if (reservedCheck) {
                    seatButton.setBackground(new Color(255, 102, 102));
                    seatButton.setForeground(Color.WHITE);
                } else {
                    seatButton.setBackground(new Color(102, 204, 102));
                    seatButton.setForeground(Color.WHITE);
                }

                final boolean finalIsReserved = reservedCheck;
                final String finalSeatNum = seatNum;

                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (finalIsReserved) {
                            JOptionPane.showMessageDialog(InternalSeatFrame.this, 
                                    "이미 예약된 좌석입니다.", "예약 불가", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(InternalSeatFrame.this, 
                                    "예약 가능합니다.", "확인", JOptionPane.INFORMATION_MESSAGE);

                            if (isReservationMode) {
                                Reservation r = manager.reserveSeat(currentUserName, finalSeatNum, currentTicket);
                                if (r != null) {
                                    JOptionPane.showMessageDialog(InternalSeatFrame.this, 
                                            currentUserName + "님의 예약이 완료되었습니다!\n배정 좌석: " + finalSeatNum, 
                                            "예약 완료", JOptionPane.INFORMATION_MESSAGE);
                                    r.showReservationInfo();
                                }
                                dispose();
                            }
                        }
                    }
                });

                gridPanel.add(seatButton);
            }

            add(gridPanel, BorderLayout.CENTER);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });

            setVisible(true);
        }
    }
}