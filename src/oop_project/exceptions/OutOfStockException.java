/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: Thrown when the stadium shop does not have enough of a product in stock.
 */

package oop_project.exceptions;

public class OutOfStockException extends LeagueException {

    public OutOfStockException(String message) {
        super(message);
    }
}
