/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class runs every test class in order and prints a summary. T-27 (letters typed where a
 * number is expected) is a manual console test; its evidence is in docs/test-evidence/.
 *
 * Run from the repo root:  java -cp out oop_project.tests.TestRunner
 */

package oop_project.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("KPFL test run - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Java " + System.getProperty("java.version"));

        section("TeamTests (T-01 to T-07): teams, squads and the minimum age");
        TeamTests.run();
        section("FixtureTests (T-08 to T-13): fixtures, clashes and officials");
        FixtureTests.run();
        section("TicketTests (T-14 to T-16): ticket sales and capacity");
        TicketTests.run();
        section("ResultTests (T-17 to T-20, T-28): results, points and standings");
        ResultTests.run();
        section("ShopTests (T-21 to T-25): shop sales, GCT and payment");
        ShopTests.run();
        section("PersistenceTests (T-26): save and reload");
        PersistenceTests.run();
        section("Manual test");
        System.out.println("T-27  Letters typed where a number is expected: run the program and type letters at a menu prompt.");
        System.out.println("      Evidence: docs/test-evidence/ (the prompt is repeated and the program keeps running).");

        System.out.println();
        System.out.println("==================================================");
        System.out.printf("Summary: %d passed, %d failed, %d checks%n", Check.getPassed(), Check.getFailed(),
                Check.getPassed() + Check.getFailed());
        if (Check.getFailed() == 0) {
            System.out.println("ALL CHECKS PASSED");
        } else {
            System.out.println("SOME CHECKS FAILED");
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }
}
