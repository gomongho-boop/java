package studycafe.ticket;

public class DayTicket extends Ticket {
    private int hours;

    public DayTicket(int hours) {
        super("시간권", 0);
        this.hours = hours;
    }

    @Override
    public int calculatePrice() {
        // 지정된 패키지 시간이면 할인된 고정 요금 반환
        switch (hours) {
            case 2: return 4000;
            case 4: return 7000;
            case 8: return 13000;
            default: 
                // 9시간처럼 애매한 시간이 들어오면 시간당 2,000원으로 계산 (원하는 기준 요금으로 변경 가능)
                return hours * 2000; 
        }
    }
}