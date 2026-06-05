package studycafe;

/** 사용자 이용 패턴 정보 (티켓 추천용) */
public class UserUsage {
    private int monthlyVisits;  // 월 방문 횟수
    private int hoursPerVisit;  // 방문당 이용 시간
    private int totalDays;      // 기간권으로 이용할 일수

    public UserUsage(int monthlyVisits, int hoursPerVisit, int totalDays) {
        this.monthlyVisits = monthlyVisits;
        this.hoursPerVisit = hoursPerVisit;
        this.totalDays     = totalDays;
    }

    public int getMonthlyVisits() { return monthlyVisits; }
    public int getHoursPerVisit() { return hoursPerVisit; }
    public int getTotalDays()     { return totalDays; }
}
