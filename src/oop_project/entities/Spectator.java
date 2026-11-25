/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a Spectator in the sports league management system.
 * It extends the Person class and adds specific attributes related to a Spectator, such as
 * tickets and purchases.
 */

package oop_project.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spectator extends Person {

    private List<Tickets> tickets;
    private List<Transaction> purchases;

    public Spectator(String id, String name, int age, String gender, List<Tickets> tickets, List<Transaction> purchases) {
        super(id, name, age, gender);
        this.tickets = new ArrayList<>();
        if (tickets != null) {
            this.tickets.addAll(tickets);
        }
        this.purchases = new ArrayList<>();
        if (purchases != null) {
            this.purchases.addAll(purchases);
        }
    }

    // Called by Fixture.sellTicket; use that rather than calling this directly
    void addTicket(Tickets ticket) {
        tickets.add(ticket);
    }

    // Called by Transaction.pay; use that rather than calling this directly
    void addPurchase(Transaction transaction) {
        purchases.add(transaction);
    }

    public List<Tickets> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public void setTickets(List<Tickets> tickets) {
        this.tickets = new ArrayList<>();
        if (tickets != null) {
            this.tickets.addAll(tickets);
        }
    }

    public List<Transaction> getPurchases() {
        return Collections.unmodifiableList(purchases);
    }

    public void setPurchases(List<Transaction> purchases) {
        this.purchases = new ArrayList<>();
        if (purchases != null) {
            this.purchases.addAll(purchases);
        }
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
