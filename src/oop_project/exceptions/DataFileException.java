/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: Thrown when a line in a data file cannot be read, for example because it has the
 * wrong number of fields or a number that is not a number. The loader catches it, reports the
 * line and skips it, so the rest of the file still loads. It is unchecked because it signals bad
 * file contents rather than a league business rule.
 */

package oop_project.exceptions;

public class DataFileException extends RuntimeException {

    public DataFileException(String message) {
        super(message);
    }

    public DataFileException(String fileName, int lineNumber, String reason) {
        super(fileName + " line " + lineNumber + ": " + reason);
    }
}
