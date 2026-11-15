/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when an unregistered or suspended team is assigned to a fixture.
 */

package oop_project.exceptions;

public class TeamNotEligibleException extends LeagueException {

    public TeamNotEligibleException(String message) {
        super(message);
    }
}
