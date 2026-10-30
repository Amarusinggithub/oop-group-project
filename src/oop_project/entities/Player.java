/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This class represents a player in the football team management system.
 * It extends the Person class and adds specific attributes related to a player, such
 * as position, jersey number, goals, and status.
 */

package oop_project.entities;

import oop_project.enums.PlayerStatus;

public class Player extends Person {

    private String position;
    private int jerseyNumber;
    private int gaols;
    private PlayerStatus status;

     static public int MIN_AGE =16;

    public Player(String id, String name, int age, String gender, String position, int jerseyNumber, int gaols, PlayerStatus status) {
        super(id, name, age, gender);
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.gaols = gaols;
        this.status = status;
}

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public int getGaols() {
        return gaols;
    }

    public void setGaols(int gaols) {
        this.gaols = gaols;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

     @Override
     public String displayInfo() {
         return super.displayInfo() + ", Position: " + position + ", Jersey Number: " + jerseyNumber + ", Goals: " + gaols + ", Status: " + status;
     }

     @Override
     public String toFileRecord(){
         return super.toFileRecord() + "," + position + "," + jerseyNumber + "," + gaols + "," + status;
     }

     @Override
     public String getRole(){
         return "Player";
     }

}
