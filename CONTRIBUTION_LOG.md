# Contribution Log

Jamaican Football League and Match-Day Management System (KPFL): six-week OOP group project.

## Members

| Member | Student ID |
|---|---|
| Amar Campbell | 2501977 |
| Kemece Hinds | 2501990 |
| Shenecia Williams | 2501202 |

## Tasks by member

### Amar Campbell (2501977)

**Completed**
- [x] Initial repository, README and UML class diagram

**To do**
- [ ] League classes (green in the UML diagram):
  - [ ] `FootballTeam` (empty stub so far)
  - [ ] `FootballLeague` (drafted, not committed)
  - [ ] `Fixture` (empty stub so far)
  - [ ] `Stadium` (drafted, not committed)
  - [ ] `MatchResult` (drafted, not committed)

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
- [ ] SRS document (in progress)

### Shenecia Williams (2501202)

**Completed**
- [x] Shop and ticket classes (orange in the UML diagram): `Product`, `FoodItem`, `Merchandise`, `StadiumShop`, `Taxable`, `Tickets`, `Transaction`

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
