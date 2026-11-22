# KPFL League and Match-Day Management System: User Guide

This guide explains how to build, start and use the Kingston Premier Football League (KPFL) system. It is a console program: you choose numbered options from menus and type answers at prompts.

> Draft for the group to review. Export it to `docs/User_Guide.pdf` for the submission.

## 1. What you need

- Java Development Kit (JDK) 21 or newer.
- The project folder, including the `data` folder with the sample league.
- No internet connection, database or extra library is needed.

## 2. Building and starting the program

From the project folder, in Git Bash (or any shell with `find`):

```bash
javac --release 21 -d out $(find src -name '*.java')
java -cp out oop_project.Main
```

In IntelliJ IDEA, open the project folder and run `oop_project.Main`. The working directory must be the project folder, so that the program finds `data/`.

When it starts, the program loads the league from `data/` and reports what it loaded:

```
KPFL League and Match-Day Management System
Loaded 8 teams, 4 stadiums, 9 officials and 19 fixtures from ...\data.
```

If a line in a data file cannot be read, it is listed and skipped, and everything else still loads. If there is no `data` folder, the program starts with an empty league.

To use another data folder, give its path as the first argument: `java -cp out oop_project.Main my-data`.

## 3. How input works

- **Menus.** Type the number of an option and press Enter. `0` always goes back one level, and on the main menu it exits.
- **Picking from a list.** Teams, fixtures, officials, stadiums and products are shown numbered. Type the number instead of retyping an ID.
- **Formats.** Each prompt shows the format and range it expects:
  - dates: `YYYY-MM-DD`, for example 2026-12-05
  - times: `HH:MM` on the 24-hour clock, for example 15:00
  - money: a plain amount, for example 1500 or 1500.50
- **Mistakes.** Anything that is not a valid number, date, time or Y/N answer is refused with a message, and the prompt is asked again. The program never stops because of bad input.
- **Commas.** Text cannot contain commas, because commas separate the fields in the data files.
- **Keeping a value.** When updating a record, press Enter at a prompt to keep the value shown in brackets.
- **Confirmations.** Removing a player or coach, cancelling a fixture and exiting without saving all ask "Are you sure? (Y/N)" first.
- **Rule errors.** If an action breaks a league rule, the program prints `Error:` and the reason, and you return to the same menu. For example: `Error: Dunbeholden is pending and cannot be assigned to a fixture.`

## 4. The main menu

```
=== Kingston Premier Football League - Season 2026 ===
 1. Teams
 2. Players & coaches
 3. Stadiums
 4. Fixtures
 5. Match officials
 6. Results & standings
 7. Ticket sales
 8. Stadium shop
 9. Reports & search
10. Save data
 0. Exit
```

### 4.1 Teams

| Option | What it does |
|---|---|
| Register team | Enter a registration number, name, parish and maximum squad size (1 to 50). The team starts as **Pending**. A registration number that is already used is refused, whatever its letter case. |
| Update team | Change the name, parish or maximum squad size. The squad size cannot go below the number of players already in the squad. |
| Search for a team | Type a name or registration number. Case and surrounding spaces are ignored. Shows the details, status, squad, coaches and match record. |
| Display all teams | A table of every team with its status, squad size, games played and points. |
| Approve or suspend a team | The league commissioner sets a team to **Registered** or **Suspended**. Only registered teams can be given fixtures. |

### 4.2 Players & coaches

| Option | What it does |
|---|---|
| Add player | Pick the team, then enter the ID, name, age, gender, position, jersey number (1 to 99) and status. The team refuses the player if the squad is full, the player is under 16, or the jersey number or ID is already used. |
| Update player | Change a player's position, jersey number or status. |
| Remove player | Removes a player from the squad, after confirmation. |
| Record goals for a player | Adds goals to a player's total, which feeds the top scorers report. The number must be at least 1. |
| Add, update, remove coach | Each coach has a qualification and a coaching role, and works for one team at a time. Updating a coach can move them to another team. |
| Display a team's squad and staff | Lists the players and coaches of one team. |

### 4.3 Stadiums

| Option | What it does |
|---|---|
| Register stadium | Enter an ID (up to 10 characters), name, location, seating capacity, base (General) ticket price and field condition. Each stadium gets its own shop automatically. |
| Update field condition, ticket price or capacity | The field condition can be Excellent, Good, Poor or Unplayable. Capacity cannot drop below the tickets already sold for a scheduled fixture there. |
| Display stadiums | Shows each stadium and the two teams of its current fixture. |

### 4.4 Fixtures

| Option | What it does |
|---|---|
| Create fixture | Pick the home team, away team and stadium, then enter the date and kick-off time. The fixture gets the next ID (FX001, FX002, ...). |
| Reschedule fixture | Change a scheduled fixture's date, kick-off time or stadium. The same clash rules apply. |
| Cancel fixture | Cancels a scheduled fixture after confirmation. Its date and time are then free for other fixtures. |
| Display fixtures | Every fixture in date order, with its result or status and the tickets sold. |

A fixture is refused when:
- a team is Pending or Suspended
- a team is picked twice
- either team already plays at that date and time
- the stadium already hosts a match at that date and time

### 4.5 Match officials

| Option | What it does |
|---|---|
| Register official | Enter the ID, name, age, gender, role (for example Referee), rank (for example FIFA) and whether they are available. |
| Assign official to fixture | Pick a scheduled fixture and an official. An unavailable official, or one already booked at the same date and time, is refused. |
| Inspect fixture readiness | The commissioner's checklist: the fixture is scheduled, both teams are registered, at least one official is assigned and the field is playable. It ends with READY or NOT READY. |
| Display officials | Every official with their availability and number of fixtures. |
| Change an official's availability | Marks an official available or unavailable for new fixtures. |
| Set league commissioner | Records the commissioner, who approves teams and inspects fixtures. |

### 4.6 Results & standings

| Option | What it does |
|---|---|
| Record result | Pick a scheduled fixture and enter both scores. Negative scores are refused. The fixture is completed and both teams' records are updated: win 3 points, draw 1, loss 0. Attendance is the number of tickets sold. The new standings are shown afterwards. |
| Display standings | The league table, ranked by points, then goal difference, then goals scored, then name. |

### 4.7 Ticket sales

| Option | What it does |
|---|---|
| Sell ticket | Pick a scheduled fixture. The program shows the seats remaining and each category's price including 15% GCT: General is 1.0 times the base price, Grandstand 1.5 times, and VIP 2.5 times. Enter the spectator's ID: an existing spectator is found, and a new one is asked for their name, age and gender. Then pick the category. A sold-out fixture refuses the sale. |
| Show seats remaining | Seats left and prices for a fixture. |
| Display tickets sold for a fixture | Ticket numbers (for example FX014-T0006), holders, categories, prices and total revenue. |

### 4.8 Stadium shop

Each option first asks which stadium's shop to use.

| Option | What it does |
|---|---|
| Add product | Either merchandise (type, size and team) or a food or beverage item (with an optional expiry date). Enter a product code (up to 10 characters), a name (up to 30), the base price before GCT and the quantity in stock. |
| Remove product | Removes a product from the inventory, after confirmation. |
| Restock product | Adds stock. The quantity must be at least 1. |
| New sale | Described below. |
| Display inventory | Every product with its base price, price including GCT, stock and details. |
| Shop sales report | Paid sales, the total including GCT, and the units sold of each product. |

**A new sale works like this:**
1. Pick the spectator (or enter a new one).
2. Pick products and enter quantities. A quantity of zero or less, or more than is in stock, is refused.
3. Pick `0` when the order is complete. The program shows the subtotal, the 15% GCT and the total.
4. Enter the amount paid. If it is less than the total, the program says so and asks whether to try another amount.
5. Once paid, the program shows the change and prints a receipt. The new stock level is written straight into `products.dat`.

If the sale is abandoned, nothing is taken from stock.

### 4.9 Reports & search

| Option | What it shows |
|---|---|
| League standings | The league table. |
| Fixtures and results | Every fixture with its date, time, teams, stadium, result and tickets sold. |
| Shop sales by stadium | Paid sales and totals per stadium, with a league total. |
| Top scorers | Every player with at least one goal, highest first. |
| Attendance and ticket revenue | For each fixture: capacity, tickets sold, percentage of capacity and revenue including GCT. |
| Squad list for a team | Players by jersey number, with age, status and whether they meet the minimum age. |
| Find a team | By name or registration number. |
| Find a player | By ID or part of the name, showing their team. |
| Find fixtures | On a date, or for a team. |
| Find a shop product | By product code, across every shop. |

A search that finds nothing says "No match found."

### 4.10 Save data and exit

- **Save data** (option 10) writes every data file and reports how many records it saved.
- **Exit** (option 0) asks whether to save first. Answering N asks you to confirm that you want to exit without saving.

Saving is safe: each file is written to a temporary file first, and only replaces the old file once the write has succeeded.

## 5. The sample data

The `data` folder holds a ready-made league for the demonstration:

| Records | Contents |
|---|---|
| Teams | 8 teams: 6 registered, Tivoli Gardens suspended, Dunbeholden pending |
| People | 65 players, 12 coaches, 9 match officials and the league commissioner |
| Stadiums | 4 stadiums; the Ferdi Neita Sports Complex field is Poor |
| Fixtures | 19 fixtures: 12 completed with results, 1 cancelled (FX013) and 6 scheduled (FX014 to FX019) |
| Officials | FX019 has no officials, so it fails inspection |
| Tickets | 122 tickets for 16 spectators |
| Shops | 20 products across the 4 shops, and 14 paid sales |

The people in the sample data are fictional.

## 6. The data files

| File | Holds | Access |
|---|---|---|
| `stadiums.txt`, `teams.txt`, `commissioner.txt`, `players.txt`, `coaches.txt`, `officials.txt`, `fixtures.txt`, `tickets.txt`, `transactions.txt` | One record per line, with fields separated by commas | Sequential |
| `products.dat` | Every shop's products, as fixed-length records | Random access |

Do not edit the files while the program is running, because it overwrites them on Save.

## 7. Troubleshooting

| Problem | What to do |
|---|---|
| `Could not find or load main class oop_project.Main` | Compile first (section 2), and run from the project folder. |
| "No data folder ... starting with an empty league" | Run the program from the project folder, or pass the data folder's path as the first argument. |
| "N line(s) could not be read and were skipped" | Each skipped line is listed with its file, line number and reason. Fix or delete the line, or ignore it: the rest of the data has loaded. |
| A team cannot be given a fixture | The commissioner must approve it first (Teams > Approve or suspend a team). |
| A fixture fails inspection | Assign at least one official, and make sure the field is not Unplayable. |
