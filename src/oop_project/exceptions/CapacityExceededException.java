/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when a stadium has no seats left for a fixture, or a team's squad is already
 * full.
 */

package oop_project.exceptions;

public class CapacityExceededException extends LeagueException {

    public CapacityExceededException(String message) {
        super(message);
    }
}
