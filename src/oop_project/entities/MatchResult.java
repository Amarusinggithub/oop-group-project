/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This class represents the result of a completed fixture: the score,
 * the attendance and the date the result was recorded.
 */

package oop_project.entities;

import oop_project.exceptions.InvalidDataException;

import java.time.LocalDate;

public class MatchResult {
    private int homeGoals;
    private int awayGoals;
    private int attendance;
    private LocalDate recordedOn;

    public MatchResult(int homeGoals, int awayGoals, int attendance) throws InvalidDataException {
        // SRS rule: match scores cannot be negative
        if (homeGoals < 0 || awayGoals < 0) {
            throw new InvalidDataException("Match scores cannot be negative.");
        }
        if (attendance < 0) {
            throw new InvalidDataException("Attendance cannot be negative.");
        }
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.attendance = attendance;
        this.recordedOn = LocalDate.now();
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public int getAttendance() {
        return attendance;
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public boolean isHomeWin() {
        return homeGoals > awayGoals;
    }

    public boolean isAwayWin() {
        return awayGoals > homeGoals;
    }

    public boolean isDraw() {
        return homeGoals == awayGoals;
    }

    public String getScoreLine() {
        return homeGoals + " - " + awayGoals;
    }
}
