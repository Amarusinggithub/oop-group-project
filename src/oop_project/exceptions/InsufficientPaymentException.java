/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when a payment is less than the total due on a transaction.
 */

package oop_project.exceptions;

public class InsufficientPaymentException extends LeagueException {

    public InsufficientPaymentException(String message) {
        super(message);
    }
}
