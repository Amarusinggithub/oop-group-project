/**
 * Author: Amar Campbell (with AI assistance from Claude)
 * Date: 11/1/2026
 * Description: Thrown when a team, stadium or match official would be booked for two fixtures at
 * the same date and time.
 */

package oop_project.exceptions;

public class ScheduleConflictException extends LeagueException {

    public ScheduleConflictException(String message) {
        super(message);
    }
}
