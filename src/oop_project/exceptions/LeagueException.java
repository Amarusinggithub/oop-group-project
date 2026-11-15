/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This is the base class for the league's business-rule exceptions,
 * so a menu can catch all of them with one catch block.
 */

package oop_project.exceptions;

public class LeagueException extends Exception {

    public LeagueException(String message) {
        super(message);
    }
}
