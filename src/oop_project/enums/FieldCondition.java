/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This enum represents the condition of a stadium's playing field.
 */

package oop_project.enums;

public enum FieldCondition {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    POOR("Poor"),
    UNPLAYABLE("Unplayable");

    private final String condition;

    FieldCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isPlayable() {
        return this != UNPLAYABLE;
    }

}
