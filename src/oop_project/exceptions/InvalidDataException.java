/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when data breaks a league rule, such as a negative score or quantity, or a
 * player below the minimum age.
 */

package oop_project.exceptions;

public class InvalidDataException extends LeagueException {

    public InvalidDataException(String message) {
        super(message);
    }
}
