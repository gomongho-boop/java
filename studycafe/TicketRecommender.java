package studycafe;

import studycafe.ticket.*;

/**
 * 사용자 이용 패턴을 기반으로 최적 이용권 추천
 */
public class TicketRecommender {

    /**
     * 세 이용권 비용을 계산해 비교하고 최저가 추천
     * @return 추천 Ticket 객체
     */
    public Ticket recommend(UserUsage user) {
        int monthlyVisits = user.getMonthlyVisits();
        int totalMonths = user.getTotalDays(); // 이용할 전체 월 수

        Ticket dayTicket     = new DayTicket(user.getHoursPerVisit());
        Ticket fullDayTicket = new FullDayTicket();
        Ticket periodTicket  = new PeriodTicket(totalMonths);

        // [수정] 월 방문 횟수 기반 요금에 '전체 이용 월수'를 곱해 총액을 맞춥니다.
        int dayCost     = dayTicket.calculatePrice() * monthlyVisits * totalMonths;
        int fullDayCost = fullDayTicket.calculatePrice() * monthlyVisits * totalMonths;
        int periodCost  = periodTicket.calculatePrice();

        System.out.println("\n========== 이용권 요금 비교 ==========");
        System.out.printf("  %-6s : %,6d원  (1회 %,d원 × 월 %d회 × %d달)%n",
                dayTicket.getName(), dayCost,
                dayTicket.calculatePrice(), monthlyVisits, totalMonths);
        System.out.printf("  %-6s : %,6d원  (1회 %,d원 × 월 %d회 × %d달)%n",
                fullDayTicket.getName(), fullDayCost,
                fullDayTicket.calculatePrice(), monthlyVisits, totalMonths);
        System.out.printf("  %-6s : %,6d원  (총 %,d원 / %d달)%n",
                periodTicket.getName(), periodCost,
                periodCost, totalMonths);
        System.out.println("======================================");

        int min = Math.min(dayCost, Math.min(fullDayCost, periodCost));

        Ticket best;
        if      (min == dayCost)     best = dayTicket;
        else if (min == fullDayCost) best = fullDayTicket;
        else                         best = periodTicket;

        System.out.println("  >> 추천 이용권 : " + best.getName()
                + " (" + String.format("%,d", min) + "원)");
        System.out.println("======================================\n");
        return best;
    }
} 