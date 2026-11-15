/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This class represents a single ticket for a fixture. It stores the ticket number,
 * the fixture, the spectator who holds it, the ticket category and the base price.
 * It implements Taxable so it can calculate the price with GCT.
 */

package oop_project.entities;

import oop_project.entities.interfaces.Taxable;
import oop_project.enums.TicketCategory;

public class Tickets implements Taxable {
    private String ticketNo;
    private Fixture fixture;
    private Spectator holder;
    private TicketCategory category;
    private double basePrice;

    // basePrice is the stadium's ticket price times the category multiplier; Fixture.sellTicket works it out
    public Tickets(String ticketNo, Fixture fixture, Spectator holder, TicketCategory category, double basePrice) {
        if (ticketNo == null || ticketNo.isBlank()) {
            throw new IllegalArgumentException("Ticket number is required.");
        }
        if (fixture == null) {
            throw new IllegalArgumentException("A ticket must be for a fixture.");
        }
        if (holder == null) {
            throw new IllegalArgumentException("A ticket must have a holder.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Ticket category is required.");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        this.ticketNo = ticketNo;
        this.fixture = fixture;
        this.holder = holder;
        this.category = category;
        this.basePrice = basePrice;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Spectator getHolder() {
        return holder;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public double getPriceWithTax() {
        return basePrice * (1 + GCT_RATE);
    }

    public String displayInfo() {
        return "Ticket No: " + ticketNo
                + ", Holder: " + holder.getName()
                + ", Category: " + category.getCategory()
                + ", Base Price: $" + String.format("%.2f", basePrice)
                + ", Price With Tax: $" + String.format("%.2f", getPriceWithTax());
    }

    @Override
    public String toString() {
        return ticketNo;
    }
}
