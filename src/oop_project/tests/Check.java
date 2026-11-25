/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class records and prints the result of each test check. Every check is named
 * after its ID in the SRS test plan and prints PASS or FAIL with the expected and actual result,
 * so the test run's output can be used as the test results and evidence.
 */

package oop_project.tests;

import java.util.Objects;

public final class Check {
    private static int passed;
    private static int failed;

    private Check() {
    }

    // A piece of test code that may throw a checked exception
    @FunctionalInterface
    public interface Action {
        void run() throws Exception;
    }

    // Passes when the actual value equals the expected one
    public static void equal(String id, String description, Object expected, Object actual) {
        report(id, description, Objects.equals(expected, actual), String.valueOf(expected), String.valueOf(actual));
    }

    // Passes when two amounts of money are the same to the cent
    public static void money(String id, String description, double expected, double actual) {
        boolean same = Math.round(expected * 100) == Math.round(actual * 100);
        report(id, description, same, String.format("$%,.2f", expected), String.format("$%,.2f", actual));
    }

    // Passes when the action throws the expected exception (or a subclass of it)
    public static void throwsException(String id, String description, Class<? extends Throwable> expected, Action action) {
        String actual;
        boolean pass;
        try {
            action.run();
            actual = "no exception";
            pass = false;
        } catch (Throwable e) {
            actual = e.getClass().getSimpleName() + ": " + e.getMessage();
            pass = expected.isInstance(e);
        }
        report(id, description, pass, expected.getSimpleName(), actual);
    }

    // Passes when the action finishes without throwing
    public static void noException(String id, String description, Action action) {
        String actual;
        boolean pass;
        try {
            action.run();
            actual = "completed";
            pass = true;
        } catch (Throwable e) {
            actual = e.getClass().getSimpleName() + ": " + e.getMessage();
            pass = false;
        }
        report(id, description, pass, "completed", actual);
    }

    private static void report(String id, String description, boolean pass, String expected, String actual) {
        if (pass) {
            passed++;
        } else {
            failed++;
        }
        System.out.printf("%s  %-5s %s%n", pass ? "PASS" : "FAIL", id, description);
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
