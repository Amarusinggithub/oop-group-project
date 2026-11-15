# KPFL: Jamaican Football League and Match-Day Management System

A Java console application for managing a football league and its match days. It handles teams and personnel, stadiums, fixtures and officials, results and standings, ticket sales and the stadium shop. This is a six-week object-oriented programming group project. The brief is in [`docs/Jamaican_Football_League_6_Week_OOP_Group_Project.pdf`](docs/Jamaican_Football_League_6_Week_OOP_Group_Project.pdf).

> **Status: in progress.** Most of the entity classes are written, but the console menus, file storage and tests are still empty placeholders. The project won't compile until `Fixture` and `FootballTeam` are written. See [`CONTRIBUTION_LOG.md`](CONTRIBUTION_LOG.md) for who is doing what.

## Team

| Member | Student ID | Responsible for |
|---|---|---|
| Amar Campbell | 2501977 | League classes: `FootballTeam`, `FootballLeague`, `Fixture`, `Stadium`, `MatchResult` |
| Kemece Hinds | 2501990 | Person hierarchy: `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator`; the SRS document |
| Shenecia Williams | 2501202 | Shop and tickets: `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Tickets`, `Transaction`; the presentation slides |

The colours in the [UML class diagram](docs/KPFL_UML_Class_Diagram.drawio) match these assignments: blue is Kemece, green is Amar, orange is Shenecia.

## Features

These are the brief's core requirements. The system should:
- register, update, search for and display football teams
- add, update and remove players and coaches on a team
- register stadiums, with their seating capacity and field condition
- create fixtures, assigning teams, a stadium, a date and a kick-off time
- assign match officials without scheduling conflicts
- record match results and calculate points and standings (win 3, draw 1, loss 0)
- sell tickets without going over the stadium's capacity
- manage shop products and complete sales, with subtotal, 15% GCT, total and change
- save and reload all data from files
- produce at least three management reports
- handle invalid data and prohibited operations with exceptions

## Requirements

- JDK 21
- There is no build tool (no Maven or Gradle). The project is a plain IntelliJ module, and `src/` is its only source root.

## Build and run

From the repo root, in Git Bash:

```bash
javac --release 21 -d out $(find src -name '*.java')
```

```bash
java -cp out oop_project.Main
```

You can also open the folder in IntelliJ IDEA and run `oop_project.Main`. Build output goes to `out/`, which git ignores.

## Tests

The tests are plain Java, with no JUnit. They live in `src/oop_project/tests/`, and each check is named after its ID in the SRS test plan (T-01 to T-28). Once they're written, run them with:

```bash
java -cp out oop_project.tests.TestRunner
```

## Project layout

```
src/oop_project/
  Main.java          entry point: loads the data and opens the main menu
  entities/          domain classes (Person hierarchy, league, fixtures, shop, tickets)
    interfaces/      Taxable
  enums/             FieldCondition, FixtureStatus, TeamStatus, TicketCategory, PlayerStatus
  exceptions/        LeagueException and its business-rule subclasses
  menu/              one console menu class per area, plus InputHelper
  persistence/       LeagueFileManager (text files), ProductFile (random-access file)
  tests/             TestRunner and the test classes
data/                demo data, loaded at start-up and written on Save
docs/                brief, UML diagram, and the submission documents as they're written
CONTRIBUTION_LOG.md  who did what, and what's still to do
```

## Submission documents

Once they're written, these will be in `docs/`: the SRS, the test plan and results (with screenshots in `docs/test-evidence/`), the user guide, the technical report, each member's reflection (`docs/reflections/`) and the presentation slides (`docs/presentation/`).
