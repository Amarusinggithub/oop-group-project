/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This interface represents a taxable entity.
 * It defines a constant for the GCT (General Consumption Tax)
 * rate and a method to calculate the price with tax.
 */

package oop_project.entities.interfaces;

public interface Taxable {
    final double GCT_RATE = 0.15;

    double getPriceWithTax();
}
