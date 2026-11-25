/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a player in the football team management system.
 * It extends the Person class and adds specific attributes related to a player, such
 * as position, jersey number, goals, and status.
 */

package oop_project.entities;

import oop_project.enums.PlayerStatus;
import oop_project.exceptions.InvalidDataException;

public class Player extends Person {

    // The brief does not state the minimum age, so the group assumes 16
    public static final int MIN_AGE = 16;
    public static final int MAX_JERSEY_NUMBER = 99;

    private String position;
    private int jerseyNumber;
    private int gaols;
    private PlayerStatus status;

    public Player(String id, String name, int age, String gender, String position, int jerseyNumber, int gaols, PlayerStatus status) {
        super(id, name, age, gender);
        setPosition(position);
        setJerseyNumber(jerseyNumber);
        setGaols(gaols);
        setStatus(status);
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (position == null || position.isBlank()) {
            throw new IllegalArgumentException("Position is required.");
        }
        this.position = position.trim();
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    // Use FootballTeam.changeJerseyNumber for a player already in a squad, so the number stays unique
    public void setJerseyNumber(int jerseyNumber) {
        if (jerseyNumber < 1 || jerseyNumber > MAX_JERSEY_NUMBER) {
            throw new IllegalArgumentException("Jersey number must be from 1 to " + MAX_JERSEY_NUMBER + ".");
        }
        this.jerseyNumber = jerseyNumber;
    }

    public int getGaols() {
        return gaols;
    }

    public void setGaols(int gaols) {
        if (gaols < 0) {
            throw new IllegalArgumentException("Goals cannot be negative.");
        }
        this.gaols = gaols;
    }

    // Records goals scored in a match
    public void addGoals(int goals) throws InvalidDataException {
        // SRS rule: goal counts cannot be negative (or zero, since nothing would change)
        if (goals < 1) {
            throw new InvalidDataException("Goals to add must be at least 1.");
        }
        this.gaols += goals;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Player status is required.");
        }
        this.status = status;
    }

    // SRS rule: players must satisfy the league's minimum-age rule
    public boolean isEligible() {
        return age >= MIN_AGE;
    }

     @Override
     public String displayInfo() {
         return super.displayInfo() + ", Position: " + position + ", Jersey Number: " + jerseyNumber + ", Goals: " + gaols + ", Status: " + status.getStatus();
     }

     @Override
     public String toFileRecord(){
         return super.toFileRecord() + "," + position + "," + jerseyNumber + "," + gaols + "," + status.name();
     }

     @Override
     public String getRole(){
         return "Player";
     }

}
