package studycafe.ticket;

/**
 * [추상 클래스] 이용권의 공통 속성과 행동을 정의
 * - 상속: DayTicket, FullDayTicket, PeriodTicket
 */
public abstract class Ticket {
    protected String name;   // 이용권 이름
    protected int price;     // 기본 단가

    public Ticket(String name, int price) {
        this.name  = name;
        this.price = price;
    }

    // 하위 클래스에서 반드시 구현 (다형성 핵심)
    public abstract int calculatePrice();

    public String getName()  { return name; }
    public int    getPrice() { return price; }
}
