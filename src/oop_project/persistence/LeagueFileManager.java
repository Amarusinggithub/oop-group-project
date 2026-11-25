/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class saves the league to the data folder and loads it back. League records
 * go in sequential-access text files, one record per line with comma-separated fields; shop
 * products go in the random-access file products.dat through ProductFile. Every file is written
 * to a temporary file first and only replaces the old one once the write has succeeded. Records
 * refer to each other by ID, so the files are loaded in an order where each one's references
 * already exist. A missing file means "no records yet"; a line that cannot be read is skipped and
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

    // Every file the league is saved in, in the order they are loaded
    public static final List<String> ALL_FILES = List.of(STADIUMS, TEAMS, COMMISSIONER, PLAYERS, COACHES, OFFICIALS,
            FIXTURES, TICKETS, PRODUCTS, TRANSACTIONS);

    // Written in place of an empty value, such as a fixture with no result yet
    private static final String NONE = "-";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final Path folder;

    public LeagueFileManager(Path folder) {
        this.folder = folder;
    }

    public Path getFolder() {
        return folder;
    }

    public ProductFile getProductFile() {
        return new ProductFile(folder.resolve(PRODUCTS));
    }

    // ---------------------------------------------------------------- saving

    // Writes every file and returns the total number of records saved
    public int save(FootballLeague league) throws IOException {
        Files.createDirectories(folder);
        int count = 0;

        List<String> stadiums = new ArrayList<>();
        for (Stadium stadium : league.getStadiums()) {
            stadiums.add(join(stadium.getStadiumId(), stadium.getName(), stadium.getLocation(),
                    String.valueOf(stadium.getSeatingCapacity()), stadium.getFieldCondition().name(), money(stadium.getTicketPrice())));
        }
        count += writeLines(STADIUMS, stadiums);

        List<String> teams = new ArrayList<>();
        List<String> players = new ArrayList<>();
        List<String> coaches = new ArrayList<>();
        for (FootballTeam team : league.getTeams()) {
            teams.add(team.toFileRecord());
            for (Player player : team.getPlayers()) {
                players.add(team.getRegistrationNo() + "," + player.toFileRecord());
            }
            for (Coach coach : team.getCoaches()) {
                coaches.add(coach.toFileRecord());
            }
        }
        count += writeLines(TEAMS, teams);

        List<String> commissioner = new ArrayList<>();
        if (league.getCommissioner() != null) {
            commissioner.add(league.getCommissioner().toFileRecord());
        }
        count += writeLines(COMMISSIONER, commissioner);
        count += writeLines(PLAYERS, players);
        count += writeLines(COACHES, coaches);

        List<String> officials = new ArrayList<>();
        for (MatchOfficial official : league.getOfficials()) {
            officials.add(official.toFileRecord());
        }
        count += writeLines(OFFICIALS, officials);

        List<String> fixtures = new ArrayList<>();
        List<String> tickets = new ArrayList<>();
        for (Fixture fixture : league.getFixtures()) {
            List<String> officialIds = new ArrayList<>();
            for (MatchOfficial official : fixture.getOfficials()) {
                officialIds.add(official.getId());
            }
            MatchResult result = fixture.getResult();
            fixtures.add(join(fixture.getFixtureId(), fixture.getDate().toString(), fixture.getKickOff().toString(),
                    fixture.getStatus().name(), fixture.getHomeTeam().getRegistrationNo(), fixture.getAwayTeam().getRegistrationNo(),
                    fixture.getStadium().getStadiumId(), officialIds.isEmpty() ? NONE : String.join(";", officialIds),
                    result == null ? NONE : String.valueOf(result.getHomeGoals()),
                    result == null ? NONE : String.valueOf(result.getAwayGoals()),
                    result == null ? NONE : String.valueOf(result.getAttendance())));
            for (Tickets ticket : fixture.getTickets()) {
                tickets.add(join(ticket.getTicketNo(), fixture.getFixtureId(), ticket.getCategory().name(),
                        money(ticket.getBasePrice()), ticket.getHolder().toFileRecord()));
            }
        }
        count += writeLines(FIXTURES, fixtures);
        count += writeLines(TICKETS, tickets);

        count += getProductFile().saveAll(league.getStadiums());

        List<String> transactions = new ArrayList<>();
        for (Stadium stadium : league.getStadiums()) {
            for (Transaction transaction : stadium.getShop().getTransactions()) {
                if (!transaction.isPaid()) {
                    continue; // an unpaid sale was never completed, so it is not kept
                }
                List<String> items = new ArrayList<>();
                for (Map.Entry<Product, Integer> item : transaction.getItems().entrySet()) {
                    items.add(item.getKey().getProductCode() + ":" + item.getValue());
                }
                transactions.add(join(transaction.getTransactionId(), stadium.getStadiumId(),
                        transaction.getDateTime().format(DATE_TIME), money(transaction.getAmountPaid()),
                        transaction.getCustomer().toFileRecord(), String.join(";", items)));
            }
        }
        count += writeLines(TRANSACTIONS, transactions);
        return count;
    }

    // Writes the lines to a temporary file, then replaces the real file with it. Returns the line count.
    private int writeLines(String fileName, List<String> lines) throws IOException {
        Path target = folder.resolve(fileName);
        Path temp = folder.resolve(fileName + ".tmp");
        try {
            StringBuilder text = new StringBuilder();
            for (String line : lines) {
                text.append(line).append('\n');
            }
            Files.writeString(temp, text.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Files.deleteIfExists(temp);
            throw e;
        }
        replace(temp, target);
        return lines.size();
    }

    // Moves the finished temporary file over the real one, atomically where the file system allows it
    static void replace(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String join(String... fields) {
        return String.join(",", fields);
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }

    // ---------------------------------------------------------------- loading

    // Loads every file into the league and returns a message for each line that was skipped
    public List<String> load(FootballLeague league) throws IOException {
        List<String> warnings = new ArrayList<>();

        readFile(STADIUMS, 6, warnings, line -> {
            Stadium stadium = new Stadium(line.text(0), line.text(1), line.text(2), line.integer(3, "seating capacity"),
                    line.decimal(5, "ticket price"));
            stadium.setFieldCondition(line.enumValue(4, FieldCondition.class, "field condition"));
            league.registerStadium(stadium);
        });

        readFile(TEAMS, 5, warnings, line -> {
            FootballTeam team = new FootballTeam(line.text(0), line.text(1), line.text(2), line.integer(3, "maximum squad size"));
            team.setStatus(line.enumValue(4, TeamStatus.class, "team status"));
            league.registerTeam(team);
        });

        readFile(COMMISSIONER, 5, warnings, line -> {
            if (league.getCommissioner() != null) {
                throw line.error("the league already has a commissioner");
            }
            league.setCommissioner(new LeagueCommissioner(line.text(0), line.text(1), line.integer(2, "age"), line.text(3), line.text(4)));
        });

        readFile(PLAYERS, 9, warnings, line -> {
            FootballTeam team = team(league, line, 0);
            if (league.findPlayer(line.text(1)) != null) {
                throw line.error("player ID " + line.text(1) + " is already in a squad");
            }
            team.addPlayer(new Player(line.text(1), line.text(2), line.integer(3, "age"), line.text(4), line.text(5),
                    line.integer(6, "jersey number"), line.integer(7, "goals"), line.enumValue(8, PlayerStatus.class, "player status")));
        });

        readFile(COACHES, 7, warnings, line -> {
            if (league.findCoach(line.text(0)) != null) {
                throw line.error("coach ID " + line.text(0) + " is already on a team");
            }
            FootballTeam team = team(league, line, 6);
            new Coach(line.text(0), line.text(1), line.integer(2, "age"), line.text(3), line.text(4), line.text(5), team);
        });

        readFile(OFFICIALS, 7, warnings, line -> league.registerOfficial(new MatchOfficial(line.text(0), line.text(1),
                line.integer(2, "age"), line.text(3), line.text(4), line.text(5), line.bool(6, "available"))));

        readFile(FIXTURES, 11, warnings, line -> {
            List<MatchOfficial> officials = new ArrayList<>();
            if (!line.text(7).equals(NONE)) {
                for (String id : line.text(7).split(";")) {
                    MatchOfficial official = league.findOfficial(id);
                    if (official == null) {
                        throw line.error("no match official with ID " + id);
                    }
                    officials.add(official);
                }
            }
            MatchResult result = null;
            if (!line.text(8).equals(NONE)) {
                result = new MatchResult(line.integer(8, "home goals"), line.integer(9, "away goals"), line.integer(10, "attendance"));
            }
            league.restoreFixture(Fixture.restore(line.text(0), team(league, line, 4), team(league, line, 5), stadium(league, line, 6),
                    line.date(1), line.time(2), line.enumValue(3, FixtureStatus.class, "fixture status"), officials, result));
        });

        readFile(TICKETS, 8, warnings, line -> {
            Fixture fixture = league.findFixture(line.text(1));
            if (fixture == null) {
                throw line.error("no fixture with ID " + line.text(1));
            }
            for (Tickets existing : fixture.getTickets()) {
                if (existing.getTicketNo().equalsIgnoreCase(line.text(0))) {
                    throw line.error("ticket " + line.text(0) + " is already recorded");
                }
            }
            fixture.restoreTicket(line.text(0), spectator(league, line, 4), line.enumValue(2, TicketCategory.class, "ticket category"),
                    line.decimal(3, "base price"));
        });

        for (String warning : getProductFile().loadAll(league)) {
            warnings.add(warning);
        }

        readFile(TRANSACTIONS, 9, warnings, line -> {
            Stadium stadium = stadium(league, line, 1);
            Map<Product, Integer> items = new LinkedHashMap<>();
            for (String item : line.text(8).split(";")) {
                String[] parts = item.split(":");
                if (parts.length != 2) {
                    throw line.error("item \"" + item + "\" should be CODE:QUANTITY");
                }
                Product product = stadium.getShop().findProduct(parts[0].trim());
                if (product == null) {
                    throw line.error("no product " + parts[0].trim() + " in " + stadium.getName() + "'s shop");
                }
                int quantity = line.parseInt(parts[1].trim(), "item quantity");
                if (quantity < 1) {
                    throw line.error("item quantity must be at least 1");
                }
                items.merge(product, quantity, Integer::sum);
            }
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(line.text(2), DATE_TIME);
            } catch (DateTimeException e) {
                throw line.error("date and time \"" + line.text(2) + "\" should be YYYY-MM-DDTHH:MM:SS");
            }
            stadium.getShop().restoreTransaction(line.text(0), spectator(league, line, 4), items, line.decimal(3, "amount paid"), dateTime);
        });

        return warnings;
    }

    private static FootballTeam team(FootballLeague league, Line line, int index) {
        FootballTeam team = league.findTeam(line.text(index));
        if (team == null) {
            throw line.error("no team with registration number " + line.text(index));
        }
        return team;
    }

    private static Stadium stadium(FootballLeague league, Line line, int index) {
        Stadium stadium = league.findStadium(line.text(index));
        if (stadium == null) {
            throw line.error("no stadium with ID " + line.text(index));
        }
        return stadium;
    }

    // Spectators are saved with their tickets and purchases; the first record for an ID creates them
    private static Spectator spectator(FootballLeague league, Line line, int index) throws LeagueException {
        Spectator spectator = league.findSpectator(line.text(index));
        if (spectator == null) {
            spectator = new Spectator(line.text(index), line.text(index + 1), line.integer(index + 2, "age"), line.text(index + 3), null, null);
            league.registerSpectator(spectator);
        }
        return spectator;
    }

    @FunctionalInterface
    private interface LineReader {
        void read(Line line) throws LeagueException;
    }

    // Reads one file line by line. A line that breaks a rule or cannot be parsed is reported and skipped.
    private void readFile(String fileName, int fieldCount, List<String> warnings, LineReader reader) throws IOException {
        Path path = folder.resolve(fileName);
        if (!Files.exists(path)) {
            return;
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String text = lines.get(i);
            if (text.isBlank()) {
                continue;
            }
            int lineNumber = i + 1;
            try {
                String[] fields = text.split(",", -1);
                if (fields.length != fieldCount) {
                    throw new DataFileException(fileName, lineNumber, "expected " + fieldCount + " fields but found " + fields.length);
                }
                reader.read(new Line(fileName, lineNumber, fields));
            } catch (DataFileException e) {
                warnings.add(e.getMessage() + " (skipped)");
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                warnings.add(fileName + " line " + lineNumber + ": " + e.getMessage() + " (skipped)");
            }
        }
    }

    // One line of a data file, split into fields, with helpers that report which field was wrong
    private static final class Line {
        private final String fileName;
        private final int number;
        private final String[] fields;

        Line(String fileName, int number, String[] fields) {
            this.fileName = fileName;
            this.number = number;
            this.fields = fields;
        }

        DataFileException error(String reason) {
            return new DataFileException(fileName, number, reason);
        }

        String text(int index) {
            String value = fields[index].trim();
            if (value.isEmpty()) {
                throw error("field " + (index + 1) + " is empty");
            }
            return value;
        }

        int integer(int index, String field) {
            return parseInt(text(index), field);
        }

        int parseInt(String value, String field) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw error(field + " \"" + value + "\" is not a whole number");
            }
        }

        double decimal(int index, String field) {
            String value = text(index);
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw error(field + " \"" + value + "\" is not a number");
            }
        }

        boolean bool(int index, String field) {
            String value = text(index);
            if (value.equalsIgnoreCase("true")) {
                return true;
            }
            if (value.equalsIgnoreCase("false")) {
                return false;
            }
            throw error(field + " \"" + value + "\" should be true or false");
        }

        LocalDate date(int index) {
            try {
                return LocalDate.parse(text(index));
            } catch (DateTimeException e) {
                throw error("date \"" + text(index) + "\" should be YYYY-MM-DD");
            }
        }

        LocalTime time(int index) {
            try {
                return LocalTime.parse(text(index));
            } catch (DateTimeException e) {
                throw error("time \"" + text(index) + "\" should be HH:MM");
            }
        }

        <E extends Enum<E>> E enumValue(int index, Class<E> type, String field) {
            try {
                return Enum.valueOf(type, text(index).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw error(field + " \"" + text(index) + "\" is not recognised");
            }
        }
    }
}
