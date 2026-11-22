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

**To do**
- [ ] Persistence: `LeagueFileManager` (load and save the `data/*.txt` files, writing through a temp file)
- [ ] Persistence: `ProductFile` (random-access `products.dat`, with stock updated after each paid sale)
- [ ] Persistence: `FootballLeague.saveData()` and `loadData()`, handing off to `LeagueFileManager`
- [ ] Exception: `DataFileException`, thrown for a bad line in a data file
- [ ] Demo data in `data/`
- [ ] Menus: `MainMenu`, `InputHelper`, `StadiumMenu`, `FixtureMenu`, and `Main` to load the data and open `MainMenu`
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

**To do**
- [ ] Menus: `TeamMenu`, `PersonnelMenu`, `OfficialMenu`, `ResultsMenu`
- [ ] Tests: `TeamTests` (T-01 to T-07)
- [ ] Tests: `ResultTests` (T-17 to T-20, T-28)
- [ ] SRS document (in progress). `docs/KPFL_SRS_AI_Reference.pdf` is an AI-generated reference to guide it.

### Shenecia Williams (2501202)

**Completed**
- [x] Shop and ticket classes (orange in the UML diagram): `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Tickets`, `Transaction`

**To do**
- [ ] Enums: `TicketCategory`
- [ ] Shop rules: `Transaction.addItem` refuses a zero quantity and more than is in stock; abandoned sales can be cancelled
- [ ] Menus: `TicketMenu`, `ShopMenu`, `ReportMenu`
- [ ] Tests: `TestRunner` and `Check` (the runner and the PASS/FAIL printer)
- [ ] Tests: `TicketTests` (T-14 to T-16), proving `CapacityExceededException`
- [ ] Tests: `ShopTests` (T-21 to T-25), proving `InsufficientPaymentException` and `OutOfStockException`
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
