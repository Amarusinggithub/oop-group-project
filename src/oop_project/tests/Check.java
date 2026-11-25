/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class records and prints the result of each test check. Every check is named
 * after its ID in the SRS test plan and prints PASS or FAIL with the expected and actual result,
 * so the test run's output can be used as the test results and evidence.
 */

package oop_project.tests;

public class Check {
    private static int passed = 0;
    private static int failed = 0;

    // Passes when the two values are the same when written as text
    public static void equal(String id, String description, Object expected, Object actual) {
        String expectedText = String.valueOf(expected);
        String actualText = String.valueOf(actual);
        report(id, description, expectedText.equals(actualText), expectedText, actualText);
    }

    // Passes when two amounts of money are the same to the cent
    public static void money(String id, String description, double expected, double actual) {
        String expectedText = String.format("$%,.2f", expected);
        String actualText = String.format("$%,.2f", actual);
        report(id, description, expectedText.equals(actualText), expectedText, actualText);
    }

    // Use in a catch block: passes when the exception caught is the expected one
    public static void exception(String id, String description, String expected, Exception e) {
        String name = e.getClass().getSimpleName();
        report(id, description, name.equals(expected), expected, name + ": " + e.getMessage());
    }

    // Use after code that should have thrown an exception but did not
    public static void notThrown(String id, String description, String expected) {
        report(id, description, false, expected, "no exception");
    }

    // Use after code that should finish normally, and did
    public static void completed(String id, String description) {
        report(id, description, true, "completed", "completed");
    }

    // Use in a catch block when the code should have finished normally
    public static void unexpected(String id, String description, Exception e) {
        report(id, description, false, "completed", e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    private static void report(String id, String description, boolean pass, String expected, String actual) {
        String result = "FAIL";
        if (pass) {
            result = "PASS";
            passed++;
        } else {
            failed++;
        }
        System.out.printf("%s  %-5s %s%n", result, id, description);
        System.out.println("            expected: " + expected);
        System.out.println("            actual:   " + actual);
    }

    public static int getPassed() {
        return passed;
    }

    public static int getFailed() {
        return failed;
    }
}
