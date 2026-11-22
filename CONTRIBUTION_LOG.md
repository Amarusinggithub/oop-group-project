# Contribution Log

Jamaican Football League and Match-Day Management System (KPFL): six-week OOP group project.

## Members

| Member | Student ID |
|---|---|
| Amar Campbell | 2501977 |
| Kemece Hinds | 2501990 |
| Shenecia Williams | 2501202 |

## Tasks by member

Each member owns entity classes plus one or more parts of every other folder: enums, exceptions, menus and tests. All the enums and exceptions already exist. The task for each one is to use it wherever its rule applies, and to prove it with a test.

### Amar Campbell (2501977)

**Completed**
- [x] Initial repository, README and UML class diagram
- [x] League classes (green in the UML diagram): `FootballTeam`, `FootballLeague`, `Fixture`, `Stadium`, `MatchResult`
- [x] Enums: `TeamStatus`, `FixtureStatus`, `FieldCondition`
- [x] Exceptions: `TeamNotEligibleException`, `ScheduleConflictException` (teams and stadiums), `ResultNotRecordedException`
- [x] Persistence: `LeagueFileManager` (load and save the `data/*.txt` files, writing through a temp file)
- [x] Persistence: `ProductFile` (random-access `products.dat`, with stock updated after each paid sale)
- [x] Persistence: `FootballLeague.saveData()` and `loadData()`, handing off to `LeagueFileManager`
- [x] Exception: `DataFileException`, thrown for a bad line in a data file
- [x] Demo data in `data/`
- [x] Menus: `MainMenu`, `InputHelper`, `StadiumMenu`, `FixtureMenu`, and `Main` to load the data and open `MainMenu`

**To do**
- [ ] Tests: `FixtureTests` (T-08 to T-13)
- [ ] Tests: `PersistenceTests` (T-26)
- [ ] Test evidence: saved test-run output, and the T-27 console transcript in `docs/test-evidence/`
- [ ] UML diagram updated for the finished code, and the PNG export refreshed
- [ ] Drafts of the user guide and technical report (`docs/*.md`)
- [ ] T-27 manual test: add a screenshot of the run to `docs/test-evidence/`

### Kemece Hinds (2501990)

**Completed**
- [x] Person hierarchy (blue in the UML diagram, plus `Person`): `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator`
- [x] Enums: `PlayerStatus`
- [x] Exception: `InvalidDataException` rejects players under `Player.MIN_AGE` (through `Player.isEligible`)
- [x] Exception: `ScheduleConflictException` stops a match official being assigned to clashing fixtures (`MatchOfficial.assignTo`)
- [x] Menus: `TeamMenu`, `PersonnelMenu`, `OfficialMenu`, `ResultsMenu`

**To do**
- [ ] Tests: `TeamTests` (T-01 to T-07)
- [ ] Tests: `ResultTests` (T-17 to T-20, T-28)
- [ ] SRS document (in progress). `docs/KPFL_SRS_AI_Reference.pdf` is an AI-generated reference to guide it.

### Shenecia Williams (2501202)

**Completed**
- [x] Shop and ticket classes (orange in the UML diagram): `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Tickets`, `Transaction`
- [x] Enums: `TicketCategory`
- [x] Shop rules: `Transaction.addItem` refuses a zero quantity and more than is in stock; abandoned sales can be cancelled
- [x] Menus: `TicketMenu`, `ShopMenu`, `ReportMenu`
- [x] Tests: `TestRunner` and `Check` (the runner and the PASS/FAIL printer)
- [x] Tests: `TicketTests` (T-14 to T-16), proving `CapacityExceededException`
- [x] Tests: `ShopTests` (T-21 to T-25), proving `InsufficientPaymentException` and `OutOfStockException`

**To do**
- [ ] Presentation slides (in progress)

## Activity log

| Date | Member | Work | Evidence |
|---|---|---|---|
| 2026-10-29 | Amar Campbell | Set up the repository and added the README and UML class diagram | commits `8b26978`, `c6e5800`, `d2a87f5` |
| 2026-10-29 | Kemece Hinds | Person hierarchy: `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator` (committed to the repo by Amar) | commit `f1caf37` |
| 2026-11-14 | Shenecia Williams | Wrote the shop and ticket classes: `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Ticket`, `Transaction` | `src/shenecia_src/` (removed after the merge below) |
| 2026-11-15 | Shenecia Williams | Merged the shop and ticket classes into `oop_project.entities`. `Ticket` became `Tickets` and now implements `Taxable`. `Transaction` was fixed to compile and gained payment, stock checks, GCT and receipts. Added `Spectator.addTicket` and `addPurchase`. | `Tickets.java`, `Transaction.java`, `Spectator.java` |
| 2026-11-15 | Amar Campbell | Assigned the classes by UML colour: blue and `Person` to Kemece, green to Amar, orange to Shenecia | `docs/KPFL_UML_Class_Diagram.drawio` |
| 2026-11-15 | Amar Campbell | Wrote the README: project summary, team and responsibilities, features, build, run and test steps, and layout | `README.md` |
| 2026-11-15 | Amar Campbell | Assigned the enum, exception, menu and test work to each member, and took persistence | `CONTRIBUTION_LOG.md` |
| 2026-11-15 | Amar Campbell | Added a reference SRS generated with Claude (AI) to guide Kemece's own SRS. It is not Kemece's work and not the group's final SRS. | `docs/KPFL_SRS_AI_Reference.pdf` |
| 2026-11-21 | Amar Campbell | League classes: wrote `FootballTeam` (squad and coaching staff, squad-size, minimum-age and jersey rules, match record and points) and `Fixture` (scheduled/completed/cancelled lifecycle, eligibility check, clashes, officials, ticket sales within capacity, results). Added lookups, spectators, reschedule and cancel, and separate standings, fixtures and shop-sales reports to `FootballLeague`. Finished `DataFileException`. | `FootballTeam.java`, `Fixture.java`, `FootballLeague.java`, `DataFileException.java` |
| 2026-11-21 | Kemece Hinds | Person hierarchy rules: validation in the `Person` setters; `Player.isEligible` (minimum age 16) and `addGoals`; `Coach.assignTeam` keeping the team's staff in step; `MatchOfficial.isAvailableFor` and `assignTo` with the clash check; the commissioner's `approveTeam`, `suspendTeam`, `inspectFixture` and `assignOfficial`. | `Person.java`, `Player.java`, `Coach.java`, `MatchOfficial.java`, `LeagueCommissioner.java` |
| 2026-11-21 | Shenecia Williams | Shop rules: `Transaction.addItem` throws `InvalidDataException` for a quantity below 1 and `OutOfStockException` beyond the stock; added `removeItem`, `StadiumShop.cancelTransaction`, and restoring paid sales when data is loaded. | `Transaction.java`, `StadiumShop.java` |
| 2026-11-21 | Amar Campbell | Persistence: `LeagueFileManager` saves the sequential `.txt` files through temporary files and loads them in ID order, skipping and reporting bad lines; `ProductFile` keeps every shop's products in fixed-length records in `products.dat` and rewrites one product's stock in place. `saveData` and `loadData` hand off to them. Added the demo league to `data/`. | `persistence/`, `data/` |
| 2026-11-21 | Amar Campbell | Console: `Main` loads the data and opens `MainMenu` (ten areas, save, and save-on-exit); `InputHelper` checks every number, date, time and Y/N answer and shows numbered lists; `StadiumMenu` and `FixtureMenu`. | `Main.java`, `menu/MainMenu.java`, `menu/InputHelper.java`, `menu/StadiumMenu.java`, `menu/FixtureMenu.java` |
| 2026-11-21 | Kemece Hinds | Menus: `TeamMenu` (register, update, search, display, approve or suspend), `PersonnelMenu` (players, goals and coaches), `OfficialMenu` (officials, assignment, inspection, commissioner) and `ResultsMenu` (results and standings). | `menu/TeamMenu.java`, `menu/PersonnelMenu.java`, `menu/OfficialMenu.java`, `menu/ResultsMenu.java` |
| 2026-11-21 | Shenecia Williams | Menus: `TicketMenu` (sales with GCT by category, seats remaining), `ShopMenu` (products, sales with payment, change and receipt, stock written to `products.dat`) and `ReportMenu` (six reports RPT-01 to RPT-06 and searches SR-01 to SR-04). | `menu/TicketMenu.java`, `menu/ShopMenu.java`, `menu/ReportMenu.java` |
| 2026-11-21 | Shenecia Williams | Tests: `TestRunner` and `Check` (PASS or FAIL with expected and actual), `TicketTests` (T-14 to T-16) and `ShopTests` (T-21 to T-25). | `tests/TestRunner.java`, `tests/Check.java`, `tests/TicketTests.java`, `tests/ShopTests.java` |
