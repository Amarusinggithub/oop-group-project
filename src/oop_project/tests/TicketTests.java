/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: Tests T-14 to T-16 from the SRS test plan: ticket sales stop at the stadium's
 * seating capacity, and a ticket's price is the category multiplier times the base price plus GCT.
 */

package oop_project.tests;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Spectator;
import oop_project.entities.Stadium;
import oop_project.entities.Tickets;
import oop_project.enums.TeamStatus;
import oop_project.enums.TicketCategory;
import oop_project.exceptions.LeagueException;

import java.time.LocalDate;
import java.time.LocalTime;

public class TicketTests {

    public static void run() throws LeagueException {
        FootballLeague league = new FootballLeague("Test League", 2026);
        FootballTeam harbourView = new FootballTeam("T1", "Harbour View", "Kingston", 25);
        FootballTeam waterhouse = new FootballTeam("T2", "Waterhouse", "Kingston", 25);
        Stadium smallGround = new Stadium("ST9", "Sabina Park", "Kingston", 3, 1000);
        Spectator fan = new Spectator("S01", "Tashana Reid", 29, "Female", null, null);
        harbourView.setStatus(TeamStatus.REGISTERED);
        waterhouse.setStatus(TeamStatus.REGISTERED);
        league.registerTeam(harbourView);
        league.registerTeam(waterhouse);
        league.registerStadium(smallGround);
        Fixture fixture = league.createFixture(harbourView, waterhouse, smallGround, LocalDate.of(2026, 12, 5), LocalTime.of(15, 0));

        // T-14 (boundary): the ticket that takes the last seat is sold
        try {
            fixture.sellTicket(fan, TicketCategory.GENERAL);
            fixture.sellTicket(fan, TicketCategory.GENERAL);
            fixture.sellTicket(fan, TicketCategory.GRANDSTAND);
            Check.completed("T-14", "Capacity 3: sell the 3rd ticket (boundary)");
        } catch (Exception e) {
            Check.unexpected("T-14", "Capacity 3: sell the 3rd ticket (boundary)", e);
        }
        Check.equal("T-14", "Seats remaining", 0, fixture.getSeatsRemaining());
        Check.equal("T-14", "The spectator holds all three tickets", 3, fan.getTickets().size());

        // T-15 (invalid): one more than capacity
        try {
            fixture.sellTicket(fan, TicketCategory.GENERAL);
            Check.notThrown("T-15", "Sell a 4th ticket (invalid)", "CapacityExceededException");
        } catch (Exception e) {
            Check.exception("T-15", "Sell a 4th ticket (invalid)", "CapacityExceededException", e);
        }
        Check.equal("T-15", "Tickets sold is unchanged", 3, fixture.getTickets().size());

        // T-16 (normal): VIP is 2.5 x the base price, plus 15% GCT
        Stadium national = new Stadium("ST1", "National Stadium", "Kingston", 35000, 1000);
        league.registerStadium(national);
        Fixture vipFixture = league.createFixture(waterhouse, harbourView, national, LocalDate.of(2026, 12, 12), LocalTime.of(19, 30));
        Tickets vip = vipFixture.sellTicket(fan, TicketCategory.VIP);
        Check.money("T-16", "VIP ticket at a base price of $1,000.00 (normal)", 2875.00, vip.getPriceWithTax());
        Check.money("T-16", "VIP base price before GCT", 2500.00, vip.getBasePrice());
        Check.equal("T-16", "Ticket number", "FX002-T0001", vip.getTicketNo());
        Check.money("T-16", "Fixture ticket revenue including GCT", 2875.00, vipFixture.getTicketRevenue());
    }
}
