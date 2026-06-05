package studycafe.ticket;

public class FullDayTicket extends Ticket {

    public FullDayTicket() {
        super("종일권", 15000);
    }

    @Override
    public int calculatePrice() {
        return price;
    }
}