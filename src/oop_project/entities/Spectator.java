/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This class represents a Spectator in the sports league management system.
 * It extends the Person class and adds specific attributes related to a Spectator, such as
 * tickets and purchases.
 */

package oop_project.entities;

import java.util.List;

public class Spectator extends Person {

    private List<Tickets> tickets;
    private List<Transaction> purchases;

    public Spectator(String id, String name, int age, String gender, List<Tickets> tickets, List<Transaction> purchases) {
        super(id, name, age, gender);
        this.tickets = tickets;
        this.purchases = purchases;
    }

    public List<Tickets> getTickets() {
        return tickets;
    }

    public void setTickets(List<Tickets> tickets) {
        this.tickets = tickets;
    }

    public List<Transaction> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Transaction> purchases) {
        this.purchases = purchases;
    }

    @Override
    public  String displayInfo() {
        return super.displayInfo() + ", Ticket: " + tickets + ", Purchases: " + purchases;
    }

    @Override
    public String getRole() {
        return "Spectator";
    }
}
