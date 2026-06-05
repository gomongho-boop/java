package studycafe.ticket;

public class PeriodTicket extends Ticket {
    private int months;

    public PeriodTicket(int months) {
        super("정기권", 0);
        this.months = months;
    }

    @Override
    public int calculatePrice() {
        switch (months) {
            case 1:  return 180000;
            case 3:  return 560000;
            case 6:  return 1100000;
            default: 
                // 지정된 개월 외에는 월 200,000원으로 계산하는 방어 코드
                return months * 200000; 
        }
    }
}