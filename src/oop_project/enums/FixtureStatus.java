/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This enum represents the status of a fixture in the league.
 */

package oop_project.enums;

public enum FixtureStatus {
    SCHEDULED("Scheduled"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String status;

    FixtureStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
