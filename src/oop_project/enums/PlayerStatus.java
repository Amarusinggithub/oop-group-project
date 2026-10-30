/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This enum represents the status of a player in the football team management system.
 */


package oop_project.enums;

public enum PlayerStatus {
    ACTIVE("Active"),
    INJURED("Injured"),
    SUSPENDED("Suspended"),
    RETIRED("Retired");

    private final String status;

    PlayerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
