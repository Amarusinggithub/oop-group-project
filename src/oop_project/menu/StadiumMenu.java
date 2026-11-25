/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class is the Stadiums menu. It registers stadiums, updates a stadium's field
 * condition, ticket price and capacity, and lists the stadiums.
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.Stadium;
import oop_project.enums.FieldCondition;
import oop_project.enums.FixtureStatus;
import oop_project.exceptions.LeagueException;
import oop_project.persistence.ProductFile;

public class StadiumMenu {
    private FootballLeague league;
    private InputHelper input;

    public StadiumMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Register stadium", "Update field condition, ticket price or capacity", "Display stadiums"};
            int choice = input.menu("Stadiums", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        registerStadium();
                        break;
                    case 2:
                        updateStadium();
                        break;
                    case 3:
                        displayStadiums();
                        break;
                    default:
                        System.out.println("Please choose an option from the menu.");
                        break;
                }
            } catch (LeagueException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registerStadium() throws LeagueException {
        String id = input.readText("Stadium ID (e.g. ST05, up to " + ProductFile.STADIUM_ID_LENGTH + " characters)", ProductFile.STADIUM_ID_LENGTH);
        if (league.findStadium(id) != null) {
            System.out.println("Error: a stadium with ID " + id + " is already registered.");
            return;
        }
        String name = input.readText("Stadium name");
        String location = input.readText("Location");
        int capacity = input.readInt("Seating capacity", 1, 200000);
        double price = input.readMoney("Base (General) ticket price before GCT");
        FieldCondition condition = input.pickFieldCondition();
        Stadium stadium = new Stadium(id, name, location, capacity, price);
        if (condition != null) {
            stadium.setFieldCondition(condition);
        }
        league.registerStadium(stadium);
        System.out.println("Stadium " + stadium.getStadiumId() + " registered: " + stadium.displayInfo());
    }

    private void updateStadium() {
        Stadium stadium = input.pickStadium(league.getStadiums());
        if (stadium == null) {
            return;
        }
        System.out.println("Current field condition: " + stadium.getFieldCondition().getCondition() + " (0 keeps it)");
        FieldCondition condition = input.pickFieldCondition();
        if (condition != null) {
            stadium.setFieldCondition(condition);
        }
        stadium.setTicketPrice(input.readOptionalMoney("Base ticket price", stadium.getTicketPrice()));
        // SRS rule: ticket sales cannot exceed capacity, so capacity cannot drop below tickets already sold
        int minimumCapacity = Math.max(1, mostTicketsSold(stadium));
        stadium.setSeatingCapacity(input.readOptionalInt("Seating capacity", stadium.getSeatingCapacity(), minimumCapacity, 200000));
        System.out.println("Stadium updated: " + stadium.displayInfo());
        if (!stadium.getFieldCondition().isPlayable()) {
            System.out.println("Note: the field is unplayable, so its fixtures will fail inspection.");
        }
    }

    // The most tickets sold for any scheduled fixture at the stadium
    private int mostTicketsSold(Stadium stadium) {
        int most = 0;
        for (Fixture fixture : league.getFixtures(FixtureStatus.SCHEDULED)) {
            if (fixture.getStadium() == stadium) {
                most = Math.max(most, fixture.getTickets().size());
            }
        }
        return most;
    }

    private void displayStadiums() {
        if (league.getStadiums().isEmpty()) {
            System.out.println("No stadiums are registered yet.");
            return;
        }
        System.out.printf("%-6s %-34s %-16s %9s %-11s %12s %s%n", "ID", "Name", "Location", "Capacity", "Field", "Ticket", "Current teams");
        for (Stadium stadium : league.getStadiums()) {
            String teams = "-";
            if (!stadium.getAssignedTeams().isEmpty()) {
                teams = stadium.getAssignedTeams().get(0).getName() + " vs " + stadium.getAssignedTeams().get(1).getName();
            }
            System.out.printf("%-6s %-34s %-16s %,9d %-11s $%,11.2f %s%n", stadium.getStadiumId(), stadium.getName(),
                    stadium.getLocation(), stadium.getSeatingCapacity(), stadium.getFieldCondition().getCondition(),
                    stadium.getTicketPrice(), teams);
        }
    }
}
