# KPFL League and Match-Day Management System: Technical Report

> Draft for the group to review and complete. Export it to `docs/Technical_Report.pdf` for the submission. Section 10 must list sources and any AI assistance, as the course policy requires (brief, section 14).

## 1. Overview

The system is a Java 21 console application for the Kingston Premier Football League. It manages:
- teams, players, coaches, the league commissioner and match officials
- stadiums and fixtures, with clash checks
- results and standings
- ticket sales within stadium capacity
- stadium shops, with 15% GCT, payment and receipts
- six management reports and four searches

All data is saved to files and loaded again at start-up.

The code is plain Java with no external libraries and no build tool. `src/` is the only source root.

## 2. Architecture

The code is split into six packages under `oop_project`. Dependencies point one way, from the user interface down to the domain.

| Package | Contents | Depends on |
|---|---|---|
| `entities` (+ `entities.interfaces`) | The 18 domain classes and the `Taxable` interface. They hold the data and enforce the business rules. They never read input or print. | `enums`, `exceptions`; `FootballLeague` hands saving and loading to `persistence` |
| `enums` | `TeamStatus`, `PlayerStatus`, `FixtureStatus`, `TicketCategory`, `FieldCondition` | nothing |
| `exceptions` | `LeagueException` and its 7 business-rule subclasses, plus `DataFileException` | nothing |
| `persistence` | `LeagueFileManager` (sequential text files) and `ProductFile` (random-access file) | `entities` |
| `menu` | `MainMenu`, one menu class per SRS area, and `InputHelper` | `entities`, `persistence` |
| `tests` | `TestRunner`, `Check` and six test classes | `entities`, `persistence` |

`Main` loads the data and opens `MainMenu`. `MainMenu` opens the area menus and passes each one the `FootballLeague` and the shared `InputHelper`.

The UML class diagram (`docs/KPFL_UML_Class_Diagram.drawio`) shows every class, including the menu and persistence classes.

### 2.1 Validation in three layers

1. **`InputHelper`** checks that text is a valid number, date, time or Y/N answer, within range and without commas, and asks again until it is. Bad text never reaches the entities.
2. **Constructors and setters** reject impossible values, such as a blank name, a capacity below 1 or a negative price, with `IllegalArgumentException`.
3. **Business operations** enforce the league rules with checked exceptions that extend `LeagueException`.

Each menu catches `LeagueException`, `IllegalArgumentException` and `IllegalStateException` in one place, prints the message and returns to the same menu. `MainMenu` adds a final safety net, so no input can end the program.

## 3. How the OOP features are used

| Feature | Where |
|---|---|
| Encapsulation | Every field is private, or protected in the abstract superclasses `Person` and `Product`. Setters validate. Collection getters return `Collections.unmodifiableList`, so a team's squad can only change through `addPlayer` and `removePlayer`, which apply the rules. |
| Inheritance | `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial` and `Spectator` extend `Person`. `Merchandise` and `FoodItem` extend `Product`. All exceptions extend `LeagueException`. |
| Abstraction | `Person` and `Product` are abstract. `Taxable` is an interface implemented by `Tickets` and `Product`. |
| Polymorphism | `getRole()`, `displayInfo()` and `toFileRecord()` are overridden in each `Person` subclass, and `LeagueFileManager` calls `toFileRecord()` through `Person` references to write people. `getCategory()` is overridden in each `Product` subclass. `getPriceWithTax()` is called the same way on tickets and products through `Taxable`. `ProductFile` writes a different record tail for each product subclass. |
| Composition | `Stadium` creates its own `StadiumShop` in its constructor. A `Fixture` creates its `MatchResult` and `Tickets`. `StadiumShop` creates each `Transaction`. |
| Aggregation | A `FootballTeam` holds `Player` and `Coach` objects that exist on their own (a coach can move teams). `FootballLeague` holds teams, stadiums, officials and spectators. A shop holds products. |
| Association | A `Fixture` refers to two teams, a stadium and its officials. A `Spectator` refers to their tickets and purchases. |

### 3.1 Two-way links kept in step

Where two objects refer to each other, only one method changes the link, and it updates both sides:

| Call | Also updates |
|---|---|
| `Fixture.sellTicket` | `Spectator.addTicket` (after the capacity check) |
| `Fixture.assignOfficial` | `MatchOfficial.assignTo` (which checks for clashes) |
| `Fixture.complete` | both teams' records, through `FootballTeam.recordMatch` |
| `Transaction.pay` | `Spectator.addPurchase` |
| `Coach.assignTeam` / `FootballTeam.addCoach` | the old and new teams' coaching staff |

The low-level methods on the other side are package-private (`~` in the UML), so code outside `entities` cannot break a link.

## 4. Business rules

| ID | Rule | Enforced in | Exception | Tests |
|---|---|---|---|---|
| BR-01 | An unregistered or suspended team cannot be assigned to a fixture | `Fixture` constructor | `TeamNotEligibleException` | T-08 |
| BR-02 | A team cannot play two fixtures at the same date and time | `FootballLeague.createFixture`, `rescheduleFixture` | `ScheduleConflictException` | T-10, T-11 |
| BR-03 | A match official cannot be assigned to clashing fixtures | `MatchOfficial.assignTo`, `FootballLeague.rescheduleFixture` | `ScheduleConflictException` | T-13 |
| BR-04 | Ticket sales cannot exceed seating capacity | `Fixture.sellTicket` | `CapacityExceededException` | T-14, T-15 |
| BR-05 | Scores and quantities cannot be negative | `MatchResult`, `Transaction.addItem`, `Product.restock`, `Player.addGoals` | `InvalidDataException` | T-18, T-21 |
| BR-06 | Players must be at least the minimum age (16) | `FootballTeam.addPlayer`, using `Player.isEligible` | `InvalidDataException` | T-03, T-04 |
| BR-07 | A sale cannot complete when payment is insufficient | `Transaction.pay` | `InsufficientPaymentException` | T-23, T-24 |
| BR-08 | A fixture cannot be completed before a valid result is recorded | `Fixture.complete` | `ResultNotRecordedException` | T-17 |
| BR-09 | A stadium cannot host two fixtures at the same date and time | `FootballLeague.createFixture`, `rescheduleFixture` | `ScheduleConflictException` | T-12 |
| BR-10 | A squad cannot exceed its maximum size | `FootballTeam.addPlayer` | `CapacityExceededException` | T-05, T-06 |
| BR-11 | A product cannot be sold beyond its stock | `Transaction.addItem`, `Transaction.pay` | `OutOfStockException` | T-22 |

Each check is marked `// SRS rule:` in the code.

Two more rules apply:
- A cancelled fixture no longer counts as a clash, so its slot can be reused.
- An action that makes no sense in the current state throws `IllegalStateException`. Examples are changing a completed fixture and paying for a sale twice.

`Transaction.pay` checks every item's stock before reducing any of it, so a failed payment changes nothing.

## 5. Persistence

### 5.1 Files

Each sequential file holds one record per line, with fields separated by commas. `-` marks an empty value.

| File | Fields |
|---|---|
| `stadiums.txt` | ID, name, location, capacity, field condition, base ticket price |
| `teams.txt` | registration no., name, parish, max squad size, status |
| `commissioner.txt` | ID, name, age, gender, office title |
| `players.txt` | team registration no., then `Player.toFileRecord()`: ID, name, age, gender, position, jersey, goals, status |
| `coaches.txt` | `Coach.toFileRecord()`: ID, name, age, gender, qualification, role, team registration no. |
| `officials.txt` | ID, name, age, gender, role, rank, available |
| `fixtures.txt` | ID, date, kick-off, status, home and away registration nos., stadium ID, official IDs (`;`-separated), home goals, away goals, attendance |
| `tickets.txt` | ticket no., fixture ID, category, base price, holder's ID, name, age and gender |
| `transactions.txt` | transaction ID, stadium ID, date-time, amount paid, customer's ID, name, age and gender, items as `CODE:QTY;CODE:QTY` |
| `products.dat` | random access: see 5.3 |

Records refer to each other by ID, never by position. Files load in an order where each record's references already exist: stadiums, teams, commissioner, players, coaches, officials, fixtures, tickets, products, then transactions.

Spectators have no file of their own. They are rebuilt from the holder and customer details on their tickets and sales.

### 5.2 Saving and loading safely

- **Saving through a temporary file.** `LeagueFileManager` writes each file as `name.tmp`, then moves it over the real file, atomically where the file system allows. An error part-way through a save leaves the previous data intact.
- **Bad lines.** Each line is parsed through a small `Line` helper. It throws `DataFileException` naming the file, line and field, for example `teams.txt line 4: maximum squad size "abc" is not a whole number`. A line that breaks a business rule is treated the same way. The loader reports the line, skips it and carries on, so one bad line never loses the rest of the file.
- **Missing files.** A missing file means "no records yet".
- **Restoring saved objects.** Saved objects are rebuilt without re-running the checks that only apply when something first happens. For example, a completed fixture whose team has since been suspended must still load. `Fixture.restore`, `Fixture.restoreTicket`, `StadiumShop.restoreTransaction` and `FootballLeague.restoreFixture` exist only for this, and the menus never call them.
- **Standings.** Team statistics are not stored. `Fixture.restore` replays each completed fixture's result into both teams' records, so the standings always match the saved results.

### 5.3 The random-access product file

`products.dat` holds one fixed-length record of `ProductFile.RECORD_SIZE` (243) bytes per product:

| Field | Size |
|---|---|
| stadium ID | 10 characters |
| product code | 10 characters |
| name | 30 characters |
| kind | `M` (merchandise) or `F` (food) |
| base price | `double` |
| quantity | `int` |
| merchandise type | 20 characters |
| size | 10 characters |
| team | 30 characters |
| beverage flag | `boolean` |
| expiry date | epoch day, as a `long` |

Strings are padded to their fixed length. Because every record is the same size, record *n* starts at byte *n* × 243.

After each paid sale, `ShopMenu` calls `ProductFile.updateStock`. It finds the product's record, seeks to the quantity field and overwrites those 4 bytes in place, without rewriting the file. The menus limit text input to the field sizes, and `ProductFile` refuses to truncate a longer value.

## 6. User interface

The console menu follows SRS section 8: ten areas plus Exit, with `0` going back.

`InputHelper` provides prompts that state their format (`Date (YYYY-MM-DD)`, `Kick-off time (HH:MM, 24-hour)`), numbered lists to pick from, Y/N confirmation before destructive actions, and a one-line result after every action. The user guide (`docs/User_Guide.md`) describes every option.

## 7. Testing

The tests are plain Java (no JUnit) in `oop_project.tests`. Run them with:

```bash
java -cp out oop_project.tests.TestRunner
```

Each check is named after its SRS test-plan ID (T-01 to T-28) and prints PASS or FAIL with the expected and actual result. Most IDs have several checks: the main case, plus checks that nothing else changed. Examples are that the squad size is unchanged after a refused player, and that stock is unchanged after a refused payment.

| Class | Tests | Covers |
|---|---|---|
| `TeamTests` | T-01 to T-07 | registration, duplicate numbers, minimum age (16 and 15), squad size limit, duplicate jersey |
| `FixtureTests` | T-08 to T-13 | pending and suspended teams, FX001 ID, team clash, one hour later, stadium clash, official clash, cancelling frees a slot |
| `TicketTests` | T-14 to T-16 | the last seat, the sold-out refusal, VIP price $2,875.00 |
| `ResultTests` | T-17 to T-20, T-28 | completing without a result, negative score, win and draw points, standings order with a name tie-break |
| `ShopTests` | T-21 to T-25 | zero or negative quantity, out of stock (including two sales competing for the last item), short payment, exact payment, change of $442.50 |
| `PersistenceTests` | T-26 | save and reload give byte-identical files and equal counts and standings; in-place stock update; missing folder; a bad line skipped and reported |

T-27 (letters typed where a number is expected) is manual, using the console. Its transcript is `docs/test-evidence/T-27_console_transcript.txt`; add a screenshot of the same run.

**Latest run:** 83 checks, 83 passed, 0 failed. The full output is in `docs/test-evidence/test-run-output.txt`.

`PersistenceTests` writes only to temporary folders, so it never touches the demo data.

## 8. Design decisions and deviations from the brief's model

| Decision | Reason |
|---|---|
| The ticket class is `Tickets`, not `Ticket` | Kept from the original code. The UML box is still labelled `Ticket`. |
| `Player`'s goals field is `gaols` (`getGaols`, `setGaols`) | Kept from the original code; `addGoals` is the method the menus use. |
| Minimum player age is 16 | The brief requires "the league's stated minimum-age rule" but gives no age. This is to be confirmed with the lecturer. |
| `FootballLeague` keeps a list of spectators | A spectator buys both tickets and shop items, so the ticket office and the shop need one shared record. |
| Team statistics are rebuilt from results instead of saved | The standings can never drift from the recorded results. |
| `DataFileException` is unchecked and not a `LeagueException` | It reports bad file contents, not a league rule. The loader handles it in one place. |
| `FootballLeague.saveData` and `loadData` take the data folder and hand off to `LeagueFileManager` | They stay in the UML as the league's own operations, while the file code stays in `persistence`. |
| Additional rules BR-09 to BR-11 | A stadium cannot host two fixtures at once, a squad has a size limit, and a product cannot be sold beyond its stock. |

## 9. Limitations and possible extensions

**Limitations**
- One league and one season at a time.
- No login or roles: whoever runs the program can do everything.
- Text fields cannot contain commas.
- Product names are limited to 30 characters.
- If the program exits without saving after a sale, `products.dat` already holds the reduced stock but `transactions.txt` does not hold the sale. The SRS asks for both the in-place update and saving on request.

**Possible extensions** (brief, section 15): a Swing GUI over the same entities, disciplinary cards and suspensions, player transfers, and CSV export of the reports.

## 10. Sources and acknowledgements

- Project brief: *Jamaican Football League and Match-Day Management System, Six-Week Group Project Specification*, Rorron A. Clarke.
- `docs/KPFL_SRS_AI_Reference.pdf`: an AI-generated reference SRS used as a guide for the group's own SRS.
- _Group to complete: other sources and any AI assistance used, as the course policy requires._
