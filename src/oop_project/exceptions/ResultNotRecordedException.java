/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when a fixture is completed before a valid result has been recorded.
 */

package oop_project.exceptions;

public class ResultNotRecordedException extends LeagueException {

    public ResultNotRecordedException(String message) {
        super(message);
    }
}
