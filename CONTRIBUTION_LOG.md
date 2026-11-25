# Contribution Log

Jamaican Football League and Match-Day Management System (KPFL): six-week OOP group project.

## Members

| Member | Student ID |
|---|---|
| Amar Campbell | 2501977 |
| Kemece Hinds | 2501990 |
| Shenecia Williams | 2501202 |

## Tasks by member

Each member is responsible for entity classes plus one or more parts of every other folder: enums, exceptions, menus and tests.

**AI assistance.** Much of the code was written or changed with Claude (an AI tool) in sessions run by Amar on 2026-11-21 and 2026-11-25. Items under "Done with AI" were written or finished that way. The member named is responsible for that part and still has to review it and be able to explain it. The Author line in a file header names the responsible member, and does not mean that member typed all of the code. Commit messages from those dates name the responsible member in the same way.

### Amar Campbell (2501977)

**Completed**
- [x] Initial repository, README and UML class diagram
- [x] League classes, first version: `FootballLeague`, `Stadium`, `MatchResult`; the enums and the business-rule exceptions

**Done with AI (member to review)**
- [x] League classes: `FootballTeam` and `Fixture` written, `FootballLeague` extended, and the eligibility, clash and result rules enforced
- [x] Persistence: `LeagueFileManager` (load and save the `data/*.txt` files, writing through a temp file)
- [x] Persistence: `ProductFile` (random-access `products.dat`, with stock updated after each paid sale)
- [x] Persistence: `FootballLeague.saveData()` and `loadData()`, handing off to `LeagueFileManager`
- [x] Exception: `DataFileException`, thrown for a bad line in a data file

** Done with AI **
- [x] Demo data in `data/`
- [x] Menus: `MainMenu`, `InputHelper`, `StadiumMenu`, `FixtureMenu`, and `Main` to load the data and open `MainMenu`
- [x] Tests: `FixtureTests` (T-08 to T-13)
- [x] Tests: `PersistenceTests` (T-26)
- [x] Test evidence: saved test-run output, and the T-27 console transcript in `docs/test-evidence/`
- [x] Drafts of the user guide and technical report (`docs/*.md`)

**To do**
- [ ] T-27 manual test: add a screenshot of the run to `docs/test-evidence/`

### Kemece Hinds (2501990)

**Completed**
- [x] Person hierarchy, first version: `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator`

- [x] Person hierarchy: validation and business rules added to the first version
- [x] Exception: `InvalidDataException` rejects players under `Player.MIN_AGE` (through `Player.isEligible`)
- [x] Exception: `ScheduleConflictException` stops a match official being assigned to clashing fixtures (`MatchOfficial.assignTo`)

**Done with AI **
- [x] Menus: `TeamMenu`, `PersonnelMenu`, `OfficialMenu`, `ResultsMenu`
- [x] Tests: `TeamTests` (T-01 to T-07)
- [x] Tests: `ResultTests` (T-17 to T-20, T-28)

**To do**
- [ ] Real SRS document (in progress). `docs/KPFL_SRS_AI_Reference.pdf` is an AI-generated reference to guide it.

### Shenecia Williams (2501202)

**Completed**
- [x] Shop and ticket classes (orange in the UML diagram): `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Tickets`, `Transaction`
- [x] Shop rules: `Transaction.addItem` refuses a zero quantity and more than is in stock; abandoned sales can be cancelled
- [ ] 
**Done with AI (member to review)**
- [x] Menus: `TicketMenu`, `ShopMenu`, `ReportMenu`
- [x] Tests: `TestRunner` and `Check` (the runner and the PASS/FAIL printer)
- [x] Tests: `TicketTests` (T-14 to T-16), proving `CapacityExceededException`
- [x] Tests: `ShopTests` (T-21 to T-25), proving `InsufficientPaymentException` and `OutOfStockException`

**To do**
- [ ] Presentation slides (in progress)

### Whole group

**To do**
- [ ] Confirm the minimum player age (16 is assumed) with the lecturer
- [ ] Review the drafts of `docs/User_Guide.md` and `docs/Technical_Report.md`, then export them to PDF
- [ ] Test plan and results document (`docs/Test_Plan_and_Results.pdf`), using `docs/test-evidence/`
- [ ] Check the AI acknowledgement in section 10 of the technical report against the course policy, and add any other sources
- [ ] Each member's reflection in `docs/reflections/`

## Activity log

| Date | Member | Work | Evidence |
|---|---|---|---|
| 2026-10-29 | Amar Campbell | Set up the repository and added the README and UML class diagram | commits `8b26978`, `c6e5800`, `d2a87f5` |
| 2026-10-29 | Kemece Hinds | Person hierarchy: `Person`, `Player`, `Coach`, `LeagueCommissioner`, `MatchOfficial`, `Spectator` (committed to the repo by Amar) | commit `f1caf37` |
| 2026-11-14 | Shenecia Williams | Wrote the shop and ticket classes: `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Ticket`, `Transaction` | `src/shenecia_src/` (removed after the merge below) |
| 2026-11-15 | Shenecia Williams | Merged the shop and ticket classes into `oop_project.entities`. `Ticket` became `Tickets` and now implements `Taxable`. `Transaction` was fixed to compile and gained payment, stock checks, GCT and receipts. Added `Spectator.addTicket` and `addPurchase`. | `Tickets.java`, `Transaction.java`, `Spectator.java` |
| 2026-11-15 | Amar Campbell | Assigned the classes by UML colour: blue and `Person` to Kemece, green to Amar, orange to Shenecia | `docs/KPFL_UML_Class_Diagram.drawio` |
| 2026-11-15 | Amar Campbell, with AI (Claude)| Wrote the README: project summary, team and responsibilities, features, build, run and test steps, and layout | `README.md` |
| 2026-11-15 | Amar Campbell | Assigned the enum, exception, menu and test work to each member, and took persistence | `CONTRIBUTION_LOG.md` |
| 2026-11-15 | Amar Campbell, with AI (Claude) | Added a reference SRS generated with Claude (AI) to guide Kemece's own SRS. It is not Kemece's work and not the group's final SRS. | `docs/KPFL_SRS_AI_Reference.pdf` |
| 2026-11-21 | Amar Campbell | league classes: wrote `FootballTeam` (squad and coaching staff, squad-size, minimum-age and jersey rules, match record and points) and `Fixture` (scheduled/completed/cancelled lifecycle, eligibility check, clashes, officials, ticket sales within capacity, results). Added lookups, spectators, reschedule and cancel, and separate standings, fixtures and shop-sales reports to `FootballLeague`. Finished `DataFileException`. | `FootballTeam.java`, `Fixture.java`, `FootballLeague.java`, `DataFileException.java` |
| 2026-11-21 | Kemece Hinds|  person hierarchy rules: validation in the `Person` setters; `Player.isEligible` (minimum age 16) and `addGoals`; `Coach.assignTeam` keeping the team's staff in step; `MatchOfficial.isAvailableFor` and `assignTo` with the clash check; the commissioner's `approveTeam`, `suspendTeam`, `inspectFixture` and `assignOfficial`. | `Person.java`, `Player.java`, `Coach.java`, `MatchOfficial.java`, `LeagueCommissioner.java` |
| 2026-11-21 | Shenecia Williams|  shop rules: `Transaction.addItem` throws `InvalidDataException` for a quantity below 1 and `OutOfStockException` beyond the stock; added `removeItem`, `StadiumShop.cancelTransaction`, and restoring paid sales when data is loaded. | `Transaction.java`, `StadiumShop.java` |
| 2026-11-21 | Amar Campbell |  persistence: `LeagueFileManager` saves the sequential `.txt` files through temporary files and loads them in ID order, skipping and reporting bad lines; `ProductFile` keeps every shop's products in fixed-length records in `products.dat` and rewrites one product's stock in place. `saveData` and `loadData` hand off to them. Added the demo league to `data/`. | `persistence/`, `data/` |
| 2026-11-21 | Amar Campbell| console: `Main` loads the data and opens `MainMenu` (ten areas, save, and save-on-exit); `InputHelper` checks every number, date, time and Y/N answer and shows numbered lists; `StadiumMenu` and `FixtureMenu`. | `Main.java`, `menu/MainMenu.java`, `menu/InputHelper.java`, `menu/StadiumMenu.java`, `menu/FixtureMenu.java` |
| 2026-11-21 | Kemece Hinds | menus: `TeamMenu` (register, update, search, display, approve or suspend), `PersonnelMenu` (players, goals and coaches), `OfficialMenu` (officials, assignment, inspection, commissioner) and `ResultsMenu` (results and standings). | `menu/TeamMenu.java`, `menu/PersonnelMenu.java`, `menu/OfficialMenu.java`, `menu/ResultsMenu.java` |
| 2026-11-21 | Shenecia Williams with AI (Claude)|  menus: `TicketMenu` (sales with GCT by category, seats remaining), `ShopMenu` (products, sales with payment, change and receipt, stock written to `products.dat`) and `ReportMenu` (six reports RPT-01 to RPT-06 and searches SR-01 to SR-04). | `menu/TicketMenu.java`, `menu/ShopMenu.java`, `menu/ReportMenu.java` |
| 2026-11-21 | Shenecia Williams with AI (Claude)| tests: `TestRunner` and `Check` (PASS or FAIL with expected and actual), `TicketTests` (T-14 to T-16) and `ShopTests` (T-21 to T-25). | `tests/TestRunner.java`, `tests/Check.java`, `tests/TicketTests.java`, `tests/ShopTests.java` |
| 2026-11-21 | Kemece Hinds with AI (Claude)| tests: `TeamTests` (T-01 to T-07) and `ResultTests` (T-17 to T-20, T-28). | `tests/TeamTests.java`, `tests/ResultTests.java` |
| 2026-11-21 | Amar Campbell with AI (Claude)| tests: `FixtureTests` (T-08 to T-13) and `PersistenceTests` (T-26). Saved the full test run (83 checks, all passing) and the T-27 console transcript as evidence. | `tests/FixtureTests.java`, `tests/PersistenceTests.java`, `docs/test-evidence/` |
| 2026-11-21 | Amar Campbell|  updated the UML diagram for the finished code (new members, the menu and persistence classes, "(has-a)" labels) and re-exported the PNG; drafted the user guide and technical report; updated the README and project guide. | `docs/KPFL_UML_Class_Diagram.drawio`, `docs/KPFL_UML_Class_Diagram.png`, `docs/User_Guide.md`, `docs/Technical_Report.md`, `README.md`, `CLAUDE.md` |
