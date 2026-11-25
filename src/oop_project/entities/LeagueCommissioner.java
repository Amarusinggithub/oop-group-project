/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a League Commissioner in the sports league management system.
 * It extends the Person class and adds specific attributes related to a League Commissioner, such as office title.
 * The commissioner approves or suspends teams, inspects fixtures and assigns match officials.
 */

package oop_project.entities;

import oop_project.enums.FixtureStatus;
import oop_project.enums.TeamStatus;
import oop_project.exceptions.ScheduleConflictException;

public class LeagueCommissioner extends Person {
    private String officeTitle;

    public LeagueCommissioner(String id, String name, int age, String gender, String officeTitle) {
        super(id, name, age, gender);
        setOfficeTitle(officeTitle);
    }

    public String getOfficeTitle() {
        return officeTitle;
    }

    public void setOfficeTitle(String officeTitle) {
        if (officeTitle == null || officeTitle.isBlank()) {
            throw new IllegalArgumentException("Office title is required.");
        }
        this.officeTitle = officeTitle.trim();
    }

    public void approveTeam(FootballTeam team) {
        team.setStatus(TeamStatus.REGISTERED);
    }

    public void suspendTeam(FootballTeam team) {
        team.setStatus(TeamStatus.SUSPENDED);
    }

    public void assignOfficial(MatchOfficial official, Fixture fixture) throws ScheduleConflictException {
        fixture.assignOfficial(official);
    }

    // A fixture is ready when it is still scheduled, both teams are eligible,
    // at least one official is assigned and the field is playable
    public boolean inspectFixture(Fixture fixture) {
        return fixture.getStatus() == FixtureStatus.SCHEDULED
                && fixture.getHomeTeam().isEligible()
                && fixture.getAwayTeam().isEligible()
                && !fixture.getOfficials().isEmpty()
                && fixture.getStadium().getFieldCondition().isPlayable();
    }

    // The same checks as inspectFixture, one line each, for the menu to show
    public String inspectionReport(Fixture fixture) {
        StringBuilder report = new StringBuilder();
        report.append("Inspection of ").append(fixture.getFixtureId()).append(": ").append(fixture).append("\n");
        report.append(checkLine("Fixture is scheduled", fixture.getStatus() == FixtureStatus.SCHEDULED,
                fixture.getStatus().getStatus()));
        report.append(checkLine(fixture.getHomeTeam().getName() + " is registered", fixture.getHomeTeam().isEligible(),
                fixture.getHomeTeam().getStatus().getStatus()));
        report.append(checkLine(fixture.getAwayTeam().getName() + " is registered", fixture.getAwayTeam().isEligible(),
                fixture.getAwayTeam().getStatus().getStatus()));
        report.append(checkLine("At least one official assigned", !fixture.getOfficials().isEmpty(),
                fixture.getOfficials().size() + " assigned"));
        report.append(checkLine("Field is playable", fixture.getStadium().getFieldCondition().isPlayable(),
                fixture.getStadium().getFieldCondition().getCondition()));
        report.append(inspectFixture(fixture) ? "Result: READY\n" : "Result: NOT READY\n");
        return report.toString();
    }

    private static String checkLine(String check, boolean passed, String detail) {
        return String.format("  [%s] %-40s (%s)%n", passed ? "OK" : "X ", check, detail);
    }

    @Override
    public String displayInfo() {
        return super.displayInfo() + ", Office Title: " + officeTitle;
    }

    @Override
    public String toFileRecord(){
        return super.toFileRecord() + "," + officeTitle;
    }

    @Override
    public String getRole() {
        return "League Commissioner";
    }
}
