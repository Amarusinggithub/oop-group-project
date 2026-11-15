/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This class represents merchandise sold by the stadium shop, such as jerseys and flags.
 * It extends the Product class.
 */

package oop_project.entities;

public class Merchandise extends Product {
    private String type;
    private String size;
    private String teamName;

    public Merchandise(String productCode, String name, double basePrice, int quantityInStock, String type, String size, String teamName) {
        super(productCode, name, basePrice, quantityInStock);
        this.type = type;
        this.size = size;
        this.teamName = teamName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String getCategory() {
        return "Merchandise";
    }
}
