/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This class represents a Match Official in the sports league management system.
 * It extends the Person class and adds specific attributes related to a Match Official, such
 * as officiating role, rank, availability, and assigned fixtures.
 */

package oop_project.entities;

import java.util.ArrayList;
import java.util.List;

public class MatchOfficial extends Person {
    private String officiatingRole;
    private String rank;
    private Boolean available;
    private List<Fixture> assignedFixtures;

    public MatchOfficial(String id, String name, int age, String gender, String officiatingRole, String rank, Boolean available) {
        super(id, name, age, gender);
        this.officiatingRole = officiatingRole;
        this.rank = rank;
        this.available = available;
        this.assignedFixtures = new ArrayList<>();
    }

    // Getters and setters for the new attributes
    public String getOfficiatingRole() {
        return officiatingRole;
    }

    public void setOfficiatingRole(String officiatingRole) {
        this.officiatingRole = officiatingRole;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<Fixture> getAssignedFixtures() {
        return assignedFixtures;
    }

    public void setAssignedFixtures(List<Fixture> assignedFixtures) {
        this.assignedFixtures = assignedFixtures;
    }

    @Override
    public String getRole() {
        return "Match Official";
    }

    @Override
    public String displayInfo() {
        return super.displayInfo() + ", Officiating Role: " + officiatingRole + ", Rank: " + rank + ", Available: " + available;
    }

}
