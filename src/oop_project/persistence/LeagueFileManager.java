/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class saves the league to the data folder and loads it back. League records
 * go in sequential text files, one record per line, with commas between the fields. Shop products
 * go in the random-access file products.dat, through ProductFile. Each text file is written to a
 * temporary file first, and replaces the old file only after it has been written completely.
 * A missing file means there are no records yet. A line that cannot be read is skipped and
 * reported, and the rest of the file still loads.
 */

package oop_project.persistence;

import oop_project.entities.Coach;
import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.LeagueCommissioner;
import oop_project.entities.MatchOfficial;
import oop_project.entities.MatchResult;
import oop_project.entities.Person;
import oop_project.entities.Player;
import oop_project.entities.Product;
import oop_project.entities.Spectator;
import oop_project.entities.Stadium;
import oop_project.entities.Tickets;
import oop_project.entities.Transaction;
import oop_project.enums.FieldCondition;
import oop_project.enums.FixtureStatus;
import oop_project.enums.PlayerStatus;
import oop_project.enums.TeamStatus;
import oop_project.enums.TicketCategory;
import oop_project.exceptions.DataFileException;
import oop_project.exceptions.LeagueException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeagueFileManager {
    public static final String STADIUMS = "stadiums.txt";
    public static final String TEAMS = "teams.txt";
    public static final String COMMISSIONER = "commissioner.txt";
    public static final String PLAYERS = "players.txt";
    public static final String COACHES = "coaches.txt";
    public static final String OFFICIALS = "officials.txt";
    public static final String FIXTURES = "fixtures.txt";
    public static final String TICKETS = "tickets.txt";
    public static final String PRODUCTS = "products.dat";
    public static final String TRANSACTIONS = "transactions.txt";

    // Written in place of an empty value, such as a fixture with no result yet
    private static final String NONE = "-";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String folder;
    // A message for each line skipped while loading
    private List<String> warnings;

    public LeagueFileManager(String folder) {
        this.folder = folder;
        this.warnings = new ArrayList<>();
    }

    public String getFolder() {
        return folder;
    }

    public ProductFile getProductFile() {
        return new ProductFile(new File(folder, PRODUCTS));
    }

    // ---------------------------------------------------------------- saving

    // Writes every file and returns the total number of records saved
    public int save(FootballLeague league) throws IOException {
        new File(folder).mkdirs();
        int count = 0;
        count += saveStadiums(league);
        count += saveTeams(league);
        count += saveCommissioner(league);
        count += savePlayers(league);
        count += saveCoaches(league);
        count += saveOfficials(league);
        count += saveFixtures(league);
        count += saveTickets(league);
        count += getProductFile().saveAll(league.getStadiums());
        count += saveTransactions(league);
        return count;
    }

    private int saveStadiums(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Stadium stadium : league.getStadiums()) {
            lines.add(stadium.getStadiumId() + "," + stadium.getName() + "," + stadium.getLocation() + ","
                    + stadium.getSeatingCapacity() + "," + stadium.getFieldCondition().name() + ","
                    + money(stadium.getTicketPrice()));
        }
        return writeLines(STADIUMS, lines);
    }

    private int saveTeams(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (FootballTeam team : league.getTeams()) {
            lines.add(team.toFileRecord());
        }
        return writeLines(TEAMS, lines);
    }

    private int saveCommissioner(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        if (league.getCommissioner() != null) {
            lines.add(personRecord(league.getCommissioner()));
        }
        return writeLines(COMMISSIONER, lines);
    }

    private int savePlayers(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (FootballTeam team : league.getTeams()) {
            for (Player player : team.getPlayers()) {
                lines.add(team.getRegistrationNo() + "," + personRecord(player));
            }
        }
        return writeLines(PLAYERS, lines);
    }

    private int saveCoaches(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (FootballTeam team : league.getTeams()) {
            for (Coach coach : team.getCoaches()) {
                lines.add(personRecord(coach));
            }
        }
        return writeLines(COACHES, lines);
    }

    private int saveOfficials(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (MatchOfficial official : league.getOfficials()) {
            lines.add(personRecord(official));
        }
        return writeLines(OFFICIALS, lines);
    }

    private int saveFixtures(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Fixture fixture : league.getFixtures()) {
            String officialIds = "";
            for (MatchOfficial official : fixture.getOfficials()) {
                if (!officialIds.isEmpty()) {
                    officialIds += ";";
                }
                officialIds += official.getId();
            }
            if (officialIds.isEmpty()) {
                officialIds = NONE;
            }
            String score = NONE + "," + NONE + "," + NONE;
            MatchResult result = fixture.getResult();
            if (result != null) {
                score = result.getHomeGoals() + "," + result.getAwayGoals() + "," + result.getAttendance();
            }
            lines.add(fixture.getFixtureId() + "," + fixture.getDate() + "," + fixture.getKickOff() + ","
                    + fixture.getStatus().name() + "," + fixture.getHomeTeam().getRegistrationNo() + ","
                    + fixture.getAwayTeam().getRegistrationNo() + "," + fixture.getStadium().getStadiumId() + ","
                    + officialIds + "," + score);
        }
        return writeLines(FIXTURES, lines);
    }

    private int saveTickets(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Fixture fixture : league.getFixtures()) {
            for (Tickets ticket : fixture.getTickets()) {
                lines.add(ticket.getTicketNo() + "," + fixture.getFixtureId() + "," + ticket.getCategory().name() + ","
                        + money(ticket.getBasePrice()) + "," + personRecord(ticket.getHolder()));
            }
        }
        return writeLines(TICKETS, lines);
    }

    private int saveTransactions(FootballLeague league) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Stadium stadium : league.getStadiums()) {
            for (Transaction transaction : stadium.getShop().getTransactions()) {
                // An unpaid sale was never completed, so it is not kept
                if (transaction.isPaid()) {
                    String items = "";
                    for (Product product : transaction.getItems().keySet()) {
                        if (!items.isEmpty()) {
                            items += ";";
                        }
                        items += product.getProductCode() + ":" + transaction.getItems().get(product);
                    }
                    lines.add(transaction.getTransactionId() + "," + stadium.getStadiumId() + ","
                            + transaction.getDateTime().format(DATE_TIME) + "," + money(transaction.getAmountPaid()) + ","
                            + personRecord(transaction.getCustomer()) + "," + items);
                }
            }
        }
        return writeLines(TRANSACTIONS, lines);
    }

    // Polymorphism: each kind of person writes its own record through the same Person method
    private String personRecord(Person person) {
        return person.toFileRecord();
    }

    private String money(double amount) {
        return String.format("%.2f", amount);
    }

    // Writes the lines to a temporary file, then replaces the real file with it. Returns the line count.
    private int writeLines(String fileName, List<String> lines) throws IOException {
        File temp = new File(folder, fileName + ".tmp");
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        try {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } finally {
            writer.close();
        }
        replace(temp, new File(folder, fileName));
        return lines.size();
    }

    // Removes the old file and renames the finished temporary file to take its place
    static void replace(File temp, File target) throws IOException {
        if (target.exists() && !target.delete()) {
            throw new IOException("Could not replace " + target.getName());
        }
        if (!temp.renameTo(target)) {
            throw new IOException("Could not rename " + temp.getName());
        }
    }

    // ---------------------------------------------------------------- loading

    // Loads every file and returns a message for each line that was skipped. The order matters:
    // a record can only refer to records that are already loaded.
    public List<String> load(FootballLeague league) throws IOException {
        warnings = new ArrayList<>();
        loadStadiums(league);
        loadTeams(league);
        loadCommissioner(league);
        loadPlayers(league);
        loadCoaches(league);
        loadOfficials(league);
        loadFixtures(league);
        loadTickets(league);
        warnings.addAll(getProductFile().loadAll(league));
        loadTransactions(league);
        return warnings;
    }

    private void loadStadiums(FootballLeague league) throws IOException {
        List<String> lines = readLines(STADIUMS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 6);
                Stadium stadium = new Stadium(fields[0], fields[1], fields[2], wholeNumber(fields[3], "seating capacity"),
                        decimal(fields[5], "ticket price"));
                stadium.setFieldCondition(FieldCondition.valueOf(fields[4]));
                league.registerStadium(stadium);
            } catch (LeagueException e) {
                skip(STADIUMS, i, e);
            } catch (RuntimeException e) {
                skip(STADIUMS, i, e);
            }
        }
    }

    private void loadTeams(FootballLeague league) throws IOException {
        List<String> lines = readLines(TEAMS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 5);
                FootballTeam team = new FootballTeam(fields[0], fields[1], fields[2], wholeNumber(fields[3], "maximum squad size"));
                team.setStatus(TeamStatus.valueOf(fields[4]));
                league.registerTeam(team);
            } catch (LeagueException e) {
                skip(TEAMS, i, e);
            } catch (RuntimeException e) {
                skip(TEAMS, i, e);
            }
        }
    }

    private void loadCommissioner(FootballLeague league) throws IOException {
        List<String> lines = readLines(COMMISSIONER);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 5);
                if (league.getCommissioner() != null) {
                    throw new DataFileException("the league already has a commissioner");
                }
                league.setCommissioner(new LeagueCommissioner(fields[0], fields[1], wholeNumber(fields[2], "age"), fields[3], fields[4]));
            } catch (RuntimeException e) {
                skip(COMMISSIONER, i, e);
            }
        }
    }

    private void loadPlayers(FootballLeague league) throws IOException {
        List<String> lines = readLines(PLAYERS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 9);
                FootballTeam team = findTeam(league, fields[0]);
                if (league.findPlayer(fields[1]) != null) {
                    throw new DataFileException("player ID " + fields[1] + " is already in a squad");
                }
                Player player = new Player(fields[1], fields[2], wholeNumber(fields[3], "age"), fields[4], fields[5],
                        wholeNumber(fields[6], "jersey number"), wholeNumber(fields[7], "goals"), PlayerStatus.valueOf(fields[8]));
                team.addPlayer(player);
            } catch (LeagueException e) {
                skip(PLAYERS, i, e);
            } catch (RuntimeException e) {
                skip(PLAYERS, i, e);
            }
        }
    }

    private void loadCoaches(FootballLeague league) throws IOException {
        List<String> lines = readLines(COACHES);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 7);
                if (league.findCoach(fields[0]) != null) {
                    throw new DataFileException("coach ID " + fields[0] + " is already on a team");
                }
                FootballTeam team = findTeam(league, fields[6]);
                Coach coach = new Coach(fields[0], fields[1], wholeNumber(fields[2], "age"), fields[3], fields[4], fields[5], null);
                team.addCoach(coach);
            } catch (RuntimeException e) {
                skip(COACHES, i, e);
            }
        }
    }

    private void loadOfficials(FootballLeague league) throws IOException {
        List<String> lines = readLines(OFFICIALS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 7);
                MatchOfficial official = new MatchOfficial(fields[0], fields[1], wholeNumber(fields[2], "age"), fields[3],
                        fields[4], fields[5], trueOrFalse(fields[6], "available"));
                league.registerOfficial(official);
            } catch (LeagueException e) {
                skip(OFFICIALS, i, e);
            } catch (RuntimeException e) {
                skip(OFFICIALS, i, e);
            }
        }
    }

    private void loadFixtures(FootballLeague league) throws IOException {
        List<String> lines = readLines(FIXTURES);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 11);
                FootballTeam home = findTeam(league, fields[4]);
                FootballTeam away = findTeam(league, fields[5]);
                Stadium stadium = findStadium(league, fields[6]);
                List<MatchOfficial> officials = findOfficials(league, fields[7]);
                MatchResult result = null;
                if (!fields[8].equals(NONE)) {
                    result = new MatchResult(wholeNumber(fields[8], "home goals"), wholeNumber(fields[9], "away goals"),
                            wholeNumber(fields[10], "attendance"));
                }
                FixtureStatus status = FixtureStatus.valueOf(fields[3]);
                Fixture fixture = new Fixture(fields[0], home, away, stadium, LocalDate.parse(fields[1]), LocalTime.parse(fields[2]));
                if (league.findFixture(fixture.getFixtureId()) != null) {
                    throw new DataFileException("fixture " + fixture.getFixtureId() + " is already recorded");
                }
                fixture.restoreState(status, officials, result);
                league.restoreFixture(fixture);
            } catch (LeagueException e) {
                skip(FIXTURES, i, e);
            } catch (RuntimeException e) {
                skip(FIXTURES, i, e);
            }
        }
    }

    private void loadTickets(FootballLeague league) throws IOException {
        List<String> lines = readLines(TICKETS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 8);
                Fixture fixture = league.findFixture(fields[1]);
                if (fixture == null) {
                    throw new DataFileException("no fixture with ID " + fields[1]);
                }
                for (Tickets existing : fixture.getTickets()) {
                    if (existing.getTicketNo().equalsIgnoreCase(fields[0])) {
                        throw new DataFileException("ticket " + fields[0] + " is already recorded");
                    }
                }
                TicketCategory category = TicketCategory.valueOf(fields[2]);
                double basePrice = decimal(fields[3], "base price");
                Spectator holder = findOrAddSpectator(league, fields[4], fields[5], fields[6], fields[7]);
                fixture.restoreTicket(fields[0], holder, category, basePrice);
            } catch (LeagueException e) {
                skip(TICKETS, i, e);
            } catch (RuntimeException e) {
                skip(TICKETS, i, e);
            }
        }
    }

    private void loadTransactions(FootballLeague league) throws IOException {
        List<String> lines = readLines(TRANSACTIONS);
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = split(lines.get(i), 9);
                Stadium stadium = findStadium(league, fields[1]);
                Map<Product, Integer> items = findItems(stadium, fields[8]);
                LocalDateTime dateTime = LocalDateTime.parse(fields[2], DATE_TIME);
                double amountPaid = decimal(fields[3], "amount paid");
                Spectator customer = findOrAddSpectator(league, fields[4], fields[5], fields[6], fields[7]);
                stadium.getShop().restoreTransaction(fields[0], customer, items, amountPaid, dateTime);
            } catch (LeagueException e) {
                skip(TRANSACTIONS, i, e);
            } catch (RuntimeException e) {
                skip(TRANSACTIONS, i, e);
            }
        }
    }

    // ---------------------------------------------------------------- helpers for loading

    // Reads a whole file into a list, one entry per line. A missing file gives an empty list.
    private List<String> readLines(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(folder, fileName);
        if (!file.exists()) {
            return lines;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return lines;
    }

    // Splits a line at its commas and checks that it has the right number of fields, none of them empty
    private String[] split(String line, int expected) {
        String[] fields = line.split(",", -1);
        if (fields.length != expected) {
            throw new DataFileException("expected " + expected + " fields but found " + fields.length);
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                throw new DataFileException("field " + (i + 1) + " is empty");
            }
        }
        return fields;
    }

    private int wholeNumber(String text, String field) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new DataFileException(field + " \"" + text + "\" is not a whole number");
        }
    }

    private double decimal(String text, String field) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new DataFileException(field + " \"" + text + "\" is not a number");
        }
    }

    private boolean trueOrFalse(String text, String field) {
        if (text.equalsIgnoreCase("true")) {
            return true;
        }
        if (text.equalsIgnoreCase("false")) {
            return false;
        }
        throw new DataFileException(field + " \"" + text + "\" should be true or false");
    }

    private FootballTeam findTeam(FootballLeague league, String registrationNo) {
        FootballTeam team = league.findTeam(registrationNo);
        if (team == null) {
            throw new DataFileException("no team with registration number " + registrationNo);
        }
        return team;
    }

    private Stadium findStadium(FootballLeague league, String stadiumId) {
        Stadium stadium = league.findStadium(stadiumId);
        if (stadium == null) {
            throw new DataFileException("no stadium with ID " + stadiumId);
        }
        return stadium;
    }

    // Turns text such as MO01;MO06 into a list of officials
    private List<MatchOfficial> findOfficials(FootballLeague league, String text) {
        List<MatchOfficial> officials = new ArrayList<>();
        if (text.equals(NONE)) {
            return officials;
        }
        String[] ids = text.split(";");
        for (String id : ids) {
            MatchOfficial official = league.findOfficial(id);
            if (official == null) {
                throw new DataFileException("no match official with ID " + id);
            }
            officials.add(official);
        }
        return officials;
    }

    // Turns text such as M001:2;F001:3 into products and quantities
    private Map<Product, Integer> findItems(Stadium stadium, String text) {
        Map<Product, Integer> items = new LinkedHashMap<>();
        String[] itemTexts = text.split(";");
        for (String itemText : itemTexts) {
            String[] parts = itemText.split(":");
            if (parts.length != 2) {
                throw new DataFileException("item \"" + itemText + "\" should be CODE:QUANTITY");
            }
            Product product = stadium.getShop().findProduct(parts[0]);
            if (product == null) {
                throw new DataFileException("no product " + parts[0] + " in " + stadium.getName() + "'s shop");
            }
            int quantity = wholeNumber(parts[1], "item quantity");
            if (quantity < 1) {
                throw new DataFileException("item quantity must be at least 1");
            }
            items.put(product, quantity);
        }
        return items;
    }

    // Spectators are saved with their tickets and purchases; the first record for an ID creates them
    private Spectator findOrAddSpectator(FootballLeague league, String id, String name, String age, String gender) throws LeagueException {
        Spectator spectator = league.findSpectator(id);
        if (spectator == null) {
            spectator = new Spectator(id, name, wholeNumber(age, "age"), gender, null, null);
            league.registerSpectator(spectator);
        }
        return spectator;
    }

    // Records why a line was skipped. Line numbers start at 1, but list positions start at 0.
    private void skip(String fileName, int index, Exception e) {
        warnings.add(fileName + " line " + (index + 1) + ": " + e.getMessage() + " (skipped)");
    }
}
