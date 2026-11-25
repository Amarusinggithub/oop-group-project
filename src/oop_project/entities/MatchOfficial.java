/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a Match Official in the sports league management system.
 * It extends the Person class and adds specific attributes related to a Match Official, such
 * as officiating role, rank, availability, and assigned fixtures.
 */

package oop_project.entities;

import oop_project.exceptions.ScheduleConflictException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchOfficial extends Person {
    private String officiatingRole;
    private String rank;
    private boolean available;
    private List<Fixture> assignedFixtures;

    public MatchOfficial(String id, String name, int age, String gender, String officiatingRole, String rank, boolean available) {
        super(id, name, age, gender);
        setOfficiatingRole(officiatingRole);
        setRank(rank);
        this.available = available;
        this.assignedFixtures = new ArrayList<>();
    }

    public String getOfficiatingRole() {
        return officiatingRole;
    }

    public void setOfficiatingRole(String officiatingRole) {
        if (officiatingRole == null || officiatingRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Officiating role is required.");
        }
        this.officiatingRole = officiatingRole.trim();
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            throw new IllegalArgumentException("Rank is required.");
        }
        this.rank = rank.trim();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Fixture> getAssignedFixtures() {
        return Collections.unmodifiableList(assignedFixtures);
    }

    // True when the official is available and has no other fixture at the same date and time
    public boolean isAvailableFor(Fixture fixture) {
        if (!available) {
            return false;
        }
        for (Fixture other : assignedFixtures) {
            if (other.clashesWith(fixture)) {
                return false;
            }
        }
        return true;
    }

    // Called by Fixture.assignOfficial; use that rather than calling this directly
    void assignTo(Fixture fixture) throws ScheduleConflictException {
        if (!available) {
            throw new ScheduleConflictException(name + " is not available to officiate.");
        }
        // SRS rule: a match official cannot be assigned to conflicting fixtures
        for (Fixture other : assignedFixtures) {
            if (other.clashesWith(fixture)) {
                throw new ScheduleConflictException(name + " is already officiating " + other + ".");
            }
        }
        assignedFixtures.add(fixture);
    }

    // Called by Fixture.restoreState when saved data is loaded; the assignment was checked when it was first made
    void restoreAssignment(Fixture fixture) {
        assignedFixtures.add(fixture);
    }

    @Override
    public String getRole() {
        return "Match Official";
    }

    @Override
    public String displayInfo() {
        String availableText = "No";
        if (available) {
            availableText = "Yes";
        }
        return super.displayInfo() + ", Officiating Role: " + officiatingRole + ", Rank: " + rank
                + ", Available: " + availableText + ", Fixtures: " + assignedFixtures.size();
    }

    @Override
    public String toFileRecord() {
        return super.toFileRecord() + "," + officiatingRole + "," + rank + "," + available;
    }

}
