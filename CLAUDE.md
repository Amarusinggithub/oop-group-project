# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

This is a Java console application for a six-week OOP group project: the Jamaican Football League and Match-Day Management System (KPFL). The brief is `docs/Jamaican_Football_League_6_Week_OOP_Group_Project.pdf`. Section 5 of the brief lists the business rules, section 6 the recommended classes, section 7 the OOP features that are graded, and sections 8 and 9 the weekly checkpoints.

## Group members and contribution log

| Member | Student ID |
|---|---|
| Amar Campbell | 2501977 |
| Kemece Hinds | 2501990 |
| Shenecia Williams | 2501202 |

Write the Author line of a file header as `Name - ID`, for example `Author: Shenecia Williams - 2501202`.

The colours in the UML diagram show who owns each entity class:
- Blue, plus `Person`, is Kemece: `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator`.
- Green is Amar: `FootballTeam`, `FootballLeague`, `Fixture`, `Stadium`, `MatchResult`.
- Orange is Shenecia: `Tickets`, `Transaction`, `StadiumShop`, `Merchandise`, `FoodItem`. She is also credited with `Product` and `Taxable`.

Keep `CONTRIBUTION_LOG.md` (in the repo root) up to date as the project progresses. The brief marks individual contributions from the weekly logs and the repo history, so the log must be accurate:
- When the user says what a member has completed or still has to do, add it under that member's name. Tick completed items (`- [x]`) and list outstanding ones unticked (`- [ ]`).
- When work is done in a session, add a dated row (YYYY-MM-DD) to the activity log. Credit the member whose part it is, and name the files or commit as evidence.
- Record only what the user reports or what the repo shows. Don't claim work for anyone.

## Committing

The user commits by hand. Never run `git commit` or `git push` yourself.
- Split the work into batches by member or area, so the history shows who did what. The brief uses repo history as evidence of each member's contribution.
- Stage one batch at a time with `git add <paths>`. Then give the user the commit message in a fenced block, along with the list of other batches still waiting.
- Wait until the user says they've committed before staging the next batch.
- A commit message has a short summary line, a blank line, then a few lines on what changed. Name the member whose work it is. Don't add a `Co-Authored-By` line or any other Claude attribution.
- Update `CONTRIBUTION_LOG.md` before staging, so the log goes in with the work.

## Build and run

There is no build tool (no Maven or Gradle). It is a plain IntelliJ module:
- `src/` is the only source root.
- The language level is JDK 21.
- Output goes to `out/`, which is gitignored.

It is also an Eclipse project, because the group downloads the GitHub ZIP and imports it into Eclipse. Keep `.project`, `.classpath`, `.settings/` and the two `.launch` files in git. `.classpath` uses the workspace's default JRE on purpose, so members with different Java versions can open it.

**Keep the code simple.** Every member has to explain the code in an interview, so it must compile with `javac --release 8` and must not use:
- lambdas, method references, streams, `Comparator` or functional interfaces
- generic methods, varargs, `var`, records or nested classes
- arrow `switch`, pattern-matching `instanceof`, text blocks, the ternary operator or multi-catch
- `java.nio.file`: use `File`, `BufferedReader`, `BufferedWriter` and `RandomAccessFile`

Use plain loops, `if`/`else`, `switch`/`case`/`break` and separate `catch` blocks. Sorting uses a bubble sort.

The group decided against Maven and Gradle: keep this layout and don't add a build tool.

From the repo root, in Git Bash:

```bash
javac --release 21 -d out $(find src -name '*.java')
java -cp out oop_project.Main
```

`Main` loads `data/` (or the folder given as the first argument) and opens `MainMenu`.

## Testing

The group decided on plain Java tests with no test library (no JUnit). The tests cover T-01 to T-28 (83 checks), and they all pass. When you add to them:
- Put them in the `oop_project.tests` package under `src/`, so the `javac` line above compiles them.
- Run them with `java -cp out oop_project.tests.TestRunner`.
- Name each check after its ID in the SRS test plan: T-01 to T-28 in appendix A.3. The plan covers normal, boundary and invalid cases for each business rule. `docs/KPFL_SRS_AI_Reference.pdf` holds that test plan. It is an AI-generated reference SRS, there to guide Kemece's own SRS, so don't present it as Kemece's work or as the group's final `KPFL_SRS.pdf`.
- Print PASS or FAIL for every check, with the expected and actual result. The final submission uses that output as its test "results and evidence".
- T-27 (letters typed where a number is expected) is a manual console test. Its evidence is a screenshot in `docs/test-evidence/`.
- `PersistenceTests` saves to a temp folder, never to `data/`, so it can't overwrite the demo data.

## Planned layout

The user agreed this layout. The folders and files below exist as empty placeholders: fill them in rather than inventing a different structure. All the `.java` files are now written. The user guide and technical report are drafted as Markdown (`docs/User_Guide.md`, `docs/Technical_Report.md`) and get exported to PDF. The SRS PDF and the test plan PDF aren't created yet. The menu areas, data files and test IDs come from the SRS.

```
data/                          loaded at start-up, written on Save; committed as the demo sample data
  teams.txt  players.txt  coaches.txt  commissioner.txt  officials.txt
  stadiums.txt  fixtures.txt (with results)  tickets.txt  transactions.txt
  products.dat                 random-access file, fixed-length records
docs/                          submission documents, added alongside the existing files:
  KPFL_SRS.pdf  Test_Plan_and_Results.pdf  test-evidence/  User_Guide.pdf
  Technical_Report.pdf  reflections/  presentation/
CONTRIBUTION_LOG.md            who did what, kept up to date (see "Group members and contribution log")
src/oop_project/
  Main.java                    loads the data, opens MainMenu
  exceptions/DataFileException.java      a bad line in a data file
  menu/                        console menus, one class per SRS menu area
    MainMenu                   areas 1 to 10 (10 is Save); 0 exits
    TeamMenu  PersonnelMenu  StadiumMenu  FixtureMenu  OfficialMenu
    ResultsMenu  TicketMenu  ShopMenu  ReportMenu
    InputHelper                safe number, date, time and Y/N input; numbered picks
  persistence/
    LeagueFileManager          sequential .txt files, saved through a temp file
    ProductFile                random access to products.dat; stock updated after each paid sale
  tests/
    TestRunner  Check          runner, and a PASS/FAIL printer
    TeamTests (T-01 to T-07)  FixtureTests (T-08 to T-13)  TicketTests (T-14 to T-16)
    ResultTests (T-17 to T-20, T-28)  ShopTests (T-21 to T-25)  PersistenceTests (T-26)
```

- **Dependencies point one way.** `menu` uses `entities` and `persistence`. Classes in `entities` never read input or print. Each menu catches `LeagueException`, prints its message and returns to the same menu.
- **Keep `FootballLeague.saveData()` and `loadData()`.** They're in the UML, so they hand off to `LeagueFileManager` rather than being removed. Add the new menu and persistence classes to the UML diagram.
- **Each member owns their own parts.** `CONTRIBUTION_LOG.md` has the full task lists:

  | Member | Menus | Tests | Also |
  |---|---|---|---|
  | Amar | `Main`, `MainMenu`, `InputHelper`, `StadiumMenu`, `FixtureMenu` | `FixtureTests`, `PersistenceTests`, T-27 | `persistence/`, `DataFileException` |
  | Kemece | `TeamMenu`, `PersonnelMenu`, `OfficialMenu`, `ResultsMenu` | `TeamTests`, `ResultTests` | the SRS document |
  | Shenecia | `TicketMenu`, `ShopMenu`, `ReportMenu` | `TestRunner`, `Check`, `TicketTests`, `ShopTests` | the presentation slides |

- **Keep build output out of git.** That means `out/` and the runnable `KPFL.jar` built for the submission.
- **A GUI is an optional extension.** If one is ever added, use Swing, which ships with the JDK. JavaFX would need an external library.

## Architecture

There are three packages under `oop_project`: `entities` (with `entities/interfaces/Taxable`), `enums` and `exceptions`.

### Entry point and lifecycle

- **`FootballLeague` is the entry point for league operations.**
  - It registers teams, stadiums and officials, and rejects duplicate IDs.
  - `createFixture` checks for clashes: the same team, or the same stadium, at the same date and kick-off time.
  - `recordResult` records the score and then completes the fixture.
  - `getStandings` ranks teams by points, then goal difference, then goals scored, then name.
  - `generateReports` builds the text reports.
  - `saveData(folder)` and `loadData(folder)` hand off to `LeagueFileManager`. The `toFileRecord()` methods on `FootballTeam` and the `Person` subclasses write those records.
- **`Fixture` has a lifecycle.** It goes from `SCHEDULED` to `COMPLETED` or `CANCELLED`. Every mutator calls `requireScheduled`, so a finished fixture throws `IllegalStateException`. `complete()` needs a recorded `MatchResult`.

### Two-way links

Each two-way link is kept in sync from one side only. Call the owning method, not the low-level adder on the other side:

| Call this | It also updates |
|---|---|
| `Fixture.sellTicket` | `Spectator.addTicket` (it checks capacity first) |
| `Fixture.assignOfficial` | `MatchOfficial.assignTo` (it checks for schedule conflicts) |
| `Fixture.complete` | `FootballTeam.recordMatch` for both teams (win 3, draw 1, loss 0) |
| `Transaction.pay` | `Spectator.addPurchase` |
| `Coach.assignTeam` | the team's coaching staff |

Collection getters return `Collections.unmodifiableList`, so mutate only through these methods.

### Persistence

- `LeagueFileManager` writes each `.txt` file through a `.tmp` file, then deletes the old file and renames the new one. On load, a bad line throws `DataFileException`; the loader reports it, skips it and keeps going.
- Files load in ID-dependency order: stadiums, teams, commissioner, players, coaches, officials, fixtures, tickets, products, then transactions. Spectators are rebuilt from tickets and transactions.
- Team statistics aren't saved. `Fixture.restoreState` replays each completed result into both teams' records.
- `Fixture.restoreState`, `Fixture.restoreTicket`, `StadiumShop.restoreTransaction` and `FootballLeague.restoreFixture` rebuild saved objects without re-checking rules. Only the loader calls them.
- `ProductFile` holds fixed-length 243-byte records. `ShopMenu` calls `updateStock` after each paid sale.
- `data/` is the committed demo league, generated through the real classes. Its people are fictional. Don't use real people's names in test or demo data.

### Composition and generated IDs

- `Stadium` creates its own `StadiumShop` in its constructor (composition).
- `StadiumShop.startTransaction` creates each `Transaction`.
- IDs are generated:
  - fixtures: `FX001`
  - tickets: `<fixtureId>-T0001`
  - transactions: `<shopId>-TX0001`, using the next unused number. An abandoned sale is removed with `StadiumShop.cancelTransaction`.

### Money

- `Taxable.GCT_RATE` is 0.15. `Tickets` and `Product` implement `Taxable`.
- A ticket's base price is the stadium's `ticketPrice` times the `TicketCategory` multiplier (1.0, 1.5 or 2.5). GCT is added on top.
- `Transaction` rounds to cents.
- `pay()` checks every item's stock before reducing any, so a failed sale changes nothing.

### Errors

- Business-rule violations throw checked exceptions that extend `LeagueException`. The future menu can catch them all in one block.
- Bad arguments in constructors and setters throw `IllegalArgumentException`.
- Wrong lifecycle state throws `IllegalStateException`.
- Code that enforces a rule from the brief is marked `// SRS rule:`.

## Conventions and quirks

- Every source file except the placeholder `Main` opens with a comment block (Author, Date, Description) above the `package` line. Keep it on new files.
- The ticket class is `Tickets` (plural), although the brief and the UML say `Ticket`.
- `Player`'s goals field is spelled `gaols` (`getGaols` and `setGaols`).
- `Player.MIN_AGE` is 16. This is an assumption: the brief doesn't state the age.
- `Person` uses protected fields, and its ID field is capitalised: `Id`.

## UML diagram

The class diagram is `docs/KPFL_UML_Class_Diagram.drawio`. Update it when entity fields or methods change. `docs/KPFL_UML_Class_Diagram.png` is an export of it, last updated 2026-11-25.

The user agreed on these notation rules:
- Attributes and parameters are written `name: Type`, and lists as `List<Type>`.
- `#` marks protected members.
- Abstract classes are in italics and carry `{abstract}`. Interfaces carry `«interface»`.
- Enum types appear as attribute types, but there are no enum boxes.
- There are no exception classes in the diagram.
- Composition and aggregation lines are labelled "(has-a)".
- `~` marks package-private members. A dashed open arrow labelled «use» is a dependency. The menu and persistence classes are pink.
