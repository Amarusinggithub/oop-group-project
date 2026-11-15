/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This class represents a stadium. It records the seating capacity, field
 * condition and ticket price, the teams assigned to its current fixture, and owns the
 * stadium shop.
 */

package oop_project.entities;

import oop_project.enums.FieldCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stadium {
    private String stadiumId;
    private String name;
    private String location;
    private int seatingCapacity;
    private FieldCondition fieldCondition;
    private double ticketPrice;
    private List<FootballTeam> assignedTeams;
    private StadiumShop shop;

    public Stadium(String stadiumId, String name, String location, int seatingCapacity, double ticketPrice) {
        if (stadiumId == null || stadiumId.isBlank()) {
            throw new IllegalArgumentException("Stadium ID is required.");
        }
        this.stadiumId = stadiumId;
        setName(name);
        setLocation(location);
        setSeatingCapacity(seatingCapacity);
        setTicketPrice(ticketPrice);
        this.fieldCondition = FieldCondition.GOOD;
        this.assignedTeams = new ArrayList<>();
        // Composition: the shop is created with the stadium and cannot exist without it
        this.shop = new StadiumShop(stadiumId + "-SHOP");
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Stadium name is required.");
        }
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Stadium location is required.");
        }
        this.location = location;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        if (seatingCapacity < 1) {
            throw new IllegalArgumentException("Seating capacity must be at least 1.");
        }
        this.seatingCapacity = seatingCapacity;
    }

    public FieldCondition getFieldCondition() {
        return fieldCondition;
    }

    public void setFieldCondition(FieldCondition fieldCondition) {
        if (fieldCondition == null) {
            throw new IllegalArgumentException("Field condition is required.");
        }
        this.fieldCondition = fieldCondition;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        if (ticketPrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        this.ticketPrice = ticketPrice;
    }

    public List<FootballTeam> getAssignedTeams() {
        return Collections.unmodifiableList(assignedTeams);
    }

    // Records the two teams assigned to the stadium's current fixture
    public void assignTeams(FootballTeam home, FootballTeam away) {
        assignedTeams.clear();
        assignedTeams.add(home);
        assignedTeams.add(away);
    }

    public StadiumShop getShop() {
        return shop;
    }

    public String displayInfo() {
        return "Stadium ID: " + stadiumId + ", Name: " + name + ", Location: " + location + ", Capacity: " + seatingCapacity
                + ", Field Condition: " + fieldCondition.getCondition() + ", Ticket Price: $" + String.format("%.2f", ticketPrice);
    }

    @Override
    public String toString() {
        return name;
    }
}
