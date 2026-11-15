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

**To do**
- [ ] League classes (green in the UML diagram):
  - [ ] `FootballTeam` (empty stub so far)
  - [ ] `FootballLeague` (drafted)
  - [ ] `Fixture` (empty stub so far)
  - [ ] `Stadium` (drafted)
  - [ ] `MatchResult` (drafted)
- [ ] Persistence (`persistence/`):
  - [ ] `LeagueFileManager`: load and save the `data/*.txt` files, writing through a temp file
  - [ ] `ProductFile`: random-access `products.dat`, with stock updated after each paid sale
  - [ ] `FootballLeague.saveData()` and `loadData()`, handing off to `LeagueFileManager`
  - [ ] Demo data in `data/`
- [ ] Enums: `TeamStatus`, `FixtureStatus`, `FieldCondition`
- [ ] Exceptions:
  - [ ] `DataFileException` (empty so far): thrown for a bad line in a data file
  - [ ] `TeamNotEligibleException`, `ScheduleConflictException` (teams and stadiums), `ResultNotRecordedException`
- [ ] Menus: `MainMenu`, `InputHelper`, `StadiumMenu`, `FixtureMenu`, and `Main` to load the data and open `MainMenu`
- [ ] Tests:
  - [ ] `FixtureTests` (T-08 to T-13)
  - [ ] `PersistenceTests` (T-26)
  - [ ] T-27 manual test (letters typed where a number is expected): screenshot in `docs/test-evidence/`

### Kemece Hinds (2501990)

**Completed**
- _None recorded yet_

**To do**
- [ ] Person hierarchy (blue in the UML diagram, plus `Person`). In progress:
  - [ ] `Person`
  - [ ] `Player`
  - [ ] `Coach`
  - [ ] `LeagueCommissioner`
  - [ ] `MatchOfficial`
  - [ ] `Spectator`
- [ ] SRS document (in progress). `docs/KPFL_SRS_AI_Reference.pdf` is an AI-generated reference to guide it.
- [ ] Enums: `PlayerStatus`
- [ ] Exceptions:
  - [ ] `InvalidDataException`: reject players under `Player.MIN_AGE` (the minimum-age rule; not enforced yet)
  - [ ] `ScheduleConflictException`: stop a match official being assigned to clashing fixtures (`MatchOfficial.assignTo`)
- [ ] Menus: `TeamMenu`, `PersonnelMenu`, `OfficialMenu`, `ResultsMenu`
- [ ] Tests:
  - [ ] `TeamTests` (T-01 to T-07)
  - [ ] `ResultTests` (T-17 to T-20, T-28)

### Shenecia Williams (2501202)

**Completed**
- [x] Shop and ticket classes (orange in the UML diagram): `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Tickets`, `Transaction`

**To do**
- [ ] Presentation slides (in progress)
- [ ] Enums: `TicketCategory`
- [ ] Exceptions:
  - [ ] `CapacityExceededException`: stop ticket sales going over the stadium's capacity
  - [ ] `InsufficientPaymentException`, `OutOfStockException`: already thrown by `Transaction.pay`; still need tests
- [ ] Menus: `TicketMenu`, `ShopMenu`, `ReportMenu`
- [ ] Tests:
  - [ ] `TestRunner` and `Check` (the runner and the PASS/FAIL printer). Do these first, because everyone's tests need them.
  - [ ] `TicketTests` (T-14 to T-16)
  - [ ] `ShopTests` (T-21 to T-25)

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
