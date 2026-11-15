/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This enum represents the registration status of a team in the league.
 * Only REGISTERED teams can be assigned to fixtures.
 */

package oop_project.enums;

public enum TeamStatus {
    PENDING("Pending"),
    REGISTERED("Registered"),
    SUSPENDED("Suspended");

    private final String status;

    TeamStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
