/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: Test T-26 from the SRS test plan: saving the league and loading it back gives
 * identical data. It also checks the in-place stock update in products.dat, that a missing data
 * folder gives an empty league, and that a bad line is skipped and reported while the rest loads.
 * Everything is written to temporary folders, never to data/, so the demo data is never touched.
 */

package oop_project.tests;

import oop_project.entities.Coach;
import oop_project.entities.Fixture;
import oop_project.entities.FoodItem;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.LeagueCommissioner;
import oop_project.entities.MatchOfficial;
import oop_project.entities.Merchandise;
import oop_project.entities.Player;
import oop_project.entities.Product;
import oop_project.entities.Spectator;
import oop_project.entities.Stadium;
import oop_project.entities.Transaction;
import oop_project.enums.FieldCondition;
import oop_project.enums.PlayerStatus;
import oop_project.enums.TeamStatus;
import oop_project.enums.TicketCategory;
import oop_project.persistence.LeagueFileManager;
import oop_project.persistence.ProductFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class PersistenceTests {

    public static void run() {
        Path first = null;
        Path second = null;
        Path empty = null;
        try {
            first = Files.createTempDirectory("kpfl-save-1-");
            second = Files.createTempDirectory("kpfl-save-2-");
            empty = Files.createTempDirectory("kpfl-empty-");
            runChecks(first, second, empty);
        } catch (Exception e) {
            Check.equal("T-26", "Run the persistence tests", "no error", e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            deleteFolder(first);
            deleteFolder(second);
            deleteFolder(empty);
        }
    }

    private static void runChecks(Path first, Path second, Path empty) throws Exception {
        FootballLeague original = buildSampleLeague();
        int saved = original.saveData(first);

        FootballLeague reloaded = new FootballLeague("Test League", 2026);
        List<String> warnings = reloaded.loadData(first);
        Check.equal("T-26", "Save, restart and load: lines skipped", "[]", warnings.toString());
        Check.equal("T-26", "Record counts after reloading", describe(original), describe(reloaded));
        Check.equal("T-26", "Standings after reloading", standings(original), standings(reloaded));
        Check.money("T-26", "Shop sales after reloading", original.getStadiums().get(0).getShop().getTotalSales(),
                reloaded.getStadiums().get(0).getShop().getTotalSales());

        // Saving the reloaded league must give byte-for-byte the same files
        int savedAgain = reloaded.saveData(second);
        Check.equal("T-26", "Records saved the first and second time", saved, savedAgain);
        List<String> different = new ArrayList<>();
        for (String file : LeagueFileManager.ALL_FILES) {
            if (!Arrays.equals(Files.readAllBytes(first.resolve(file)), Files.readAllBytes(second.resolve(file)))) {
                different.add(file);
            }
        }
        Check.equal("T-26", "Every record identical to before saving (files that differ)", "[]", different.toString());

        // Random access: one product's stock is rewritten in place
        ProductFile productFile = new LeagueFileManager(first).getProductFile();
        long sizeBefore = Files.size(productFile.getFile());
        Product jersey = original.findStadium("ST1").getShop().findProduct("M001");
        Transaction sale = original.findStadium("ST1").getShop().startTransaction(original.findSpectator("S01"));
        sale.addItem(jersey, 1);
        sale.pay(10_000);
        boolean updated = productFile.updateStock("ST1", jersey);
        Check.equal("T-26", "products.dat stock updated in place after a sale", "true, " + jersey.getQuantityInStock(),
                updated + ", " + productFile.readStock("ST1", "M001"));
        Check.equal("T-26", "products.dat size unchanged by the in-place update", sizeBefore, Files.size(productFile.getFile()));
        Check.equal("T-26", "products.dat holds whole fixed-length records", 0L, Files.size(productFile.getFile()) % ProductFile.RECORD_SIZE);

        // A missing data folder means no records yet
        FootballLeague fresh = new FootballLeague("Test League", 2026);
        List<String> noWarnings = fresh.loadData(empty.resolve("no-such-folder"));
        Check.equal("T-26", "Loading from a folder with no data files", "0 teams, 0 warnings",
                fresh.getTeams().size() + " teams, " + noWarnings.size() + " warnings");

        // A bad line is skipped and reported; the rest of the file still loads
        Files.writeString(first.resolve(LeagueFileManager.TEAMS), "T99,Broken FC,Kingston,not-a-number,PENDING\n",
                StandardOpenOption.APPEND);
        FootballLeague partly = new FootballLeague("Test League", 2026);
        List<String> badLine = partly.loadData(first);
        Check.equal("T-26", "A bad line is reported and skipped", "1 warning, 3 teams loaded",
                badLine.size() + " warning, " + partly.getTeams().size() + " teams loaded");
        Check.equal("T-26", "The warning names the file, line and field",
                "teams.txt line 4: maximum squad size \"not-a-number\" is not a whole number (skipped)",
                badLine.isEmpty() ? "none" : badLine.get(0));
    }

    // A small league with at least one record of every kind
    static FootballLeague buildSampleLeague() throws Exception {
        FootballLeague league = new FootballLeague("Test League", 2026);
        league.setCommissioner(new LeagueCommissioner("LC01", "Winston Barrett", 58, "Male", "League Commissioner"));

        Stadium national = new Stadium("ST1", "National Stadium", "Kingston", 35000, 1500);
        Stadium spaulding = new Stadium("ST2", "Anthony Spaulding Sports Complex", "Kingston", 7000, 1000);
        spaulding.setFieldCondition(FieldCondition.EXCELLENT);
        league.registerStadium(national);
        league.registerStadium(spaulding);

        FootballTeam harbourView = new FootballTeam("T1", "Harbour View", "Kingston", 25);
        FootballTeam waterhouse = new FootballTeam("T2", "Waterhouse", "Kingston", 25);
        FootballTeam arnett = new FootballTeam("T3", "Arnett Gardens", "Kingston", 25);
        league.registerTeam(harbourView);
        league.registerTeam(waterhouse);
        league.registerTeam(arnett);
        harbourView.setStatus(TeamStatus.REGISTERED);
        waterhouse.setStatus(TeamStatus.REGISTERED);

        harbourView.addPlayer(new Player("P01", "Kevaughn Brown", 21, "Male", "Forward", 9, 0, PlayerStatus.ACTIVE));
        harbourView.addPlayer(new Player("P02", "Junior Gordon", 27, "Male", "Goalkeeper", 1, 0, PlayerStatus.INJURED));
        waterhouse.addPlayer(new Player("P03", "Andre Clarke", 19, "Male", "Defender", 4, 0, PlayerStatus.ACTIVE));
        harbourView.findPlayer("P01").addGoals(2);
        harbourView.addCoach(new Coach("C01", "Desmond Tulloch", 52, "Male", "CONCACAF A Licence", "Head Coach", null));
        new Coach("C02", "Garfield Ellis", 45, "Male", "JFF B Licence", "Assistant Coach", waterhouse);

        MatchOfficial referee = new MatchOfficial("MO1", "Kemar Whyte", 38, "Male", "Referee", "FIFA", true);
        MatchOfficial assistant = new MatchOfficial("MO2", "Rohan Pinnock", 33, "Male", "Assistant Referee", "National", false);
        league.registerOfficial(referee);
        league.registerOfficial(assistant);

        Spectator fan = new Spectator("S01", "Tashana Reid", 29, "Female", null, null);
        Spectator otherFan = new Spectator("S02", "Damion Lewis", 41, "Male", null, null);
        league.registerSpectator(fan);
        league.registerSpectator(otherFan);

        Fixture played = league.createFixture(harbourView, waterhouse, national, LocalDate.of(2026, 11, 7), LocalTime.of(15, 0));
        played.assignOfficial(referee);
        played.sellTicket(fan, TicketCategory.VIP);
        played.sellTicket(otherFan, TicketCategory.GENERAL);
        league.recordResult(played, 2, 1);
        Fixture upcoming = league.createFixture(waterhouse, harbourView, spaulding, LocalDate.of(2026, 12, 12), LocalTime.of(19, 30));
        upcoming.sellTicket(fan, TicketCategory.GRANDSTAND);
        Fixture cancelled = league.createFixture(harbourView, waterhouse, spaulding, LocalDate.of(2026, 12, 19), LocalTime.of(15, 0));
        league.cancelFixture(cancelled);

        national.getShop().addProduct(new Merchandise("M001", "Harbour View Home Jersey", 4500, 10, "Jersey", "L", "Harbour View"));
        national.getShop().addProduct(new FoodItem("F001", "Beef Patty", 350, 40, false, LocalDate.of(2026, 12, 31)));
        national.getShop().addProduct(new FoodItem("F002", "Ting", 250, 30, true, null));
        Transaction sale = national.getShop().startTransaction(fan);
        sale.addItem(national.getShop().findProduct("M001"), 2);
        sale.addItem(national.getShop().findProduct("F001"), 3);
        sale.pay(12_000);
        Transaction unpaid = national.getShop().startTransaction(otherFan);
        unpaid.addItem(national.getShop().findProduct("F002"), 1); // never paid, so it is not saved
        return league;
    }

    private static String describe(FootballLeague league) {
        int players = 0;
        int coaches = 0;
        for (FootballTeam team : league.getTeams()) {
            players += team.getPlayers().size();
            coaches += team.getCoaches().size();
        }
        int tickets = 0;
        for (Fixture fixture : league.getFixtures()) {
            tickets += fixture.getTickets().size();
        }
        int products = 0;
        int paidSales = 0;
        for (Stadium stadium : league.getStadiums()) {
            products += stadium.getShop().getInventory().size();
            for (Transaction transaction : stadium.getShop().getTransactions()) {
                if (transaction.isPaid()) {
                    paidSales++;
                }
            }
        }
        return String.format("%d stadiums, %d teams, %d players, %d coaches, %d officials, commissioner %s, %d fixtures, "
                        + "%d tickets, %d spectators, %d products, %d paid sales",
                league.getStadiums().size(), league.getTeams().size(), players, coaches, league.getOfficials().size(),
                league.getCommissioner() == null ? "none" : league.getCommissioner().getId(), league.getFixtures().size(),
                tickets, league.getSpectators().size(), products, paidSales);
    }

    private static String standings(FootballLeague league) {
        List<String> rows = new ArrayList<>();
        for (FootballTeam team : league.getStandings()) {
            rows.add(team.getName() + " P" + team.getPlayed() + " Pts" + team.getPoints() + " GD" + team.getGoalDifference());
        }
        return rows.toString();
    }

    private static void deleteFolder(Path folder) {
        if (folder == null || !Files.exists(folder)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(folder)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            System.out.println("(could not delete temporary folder " + folder + ": " + e.getMessage() + ")");
        }
    }
}
