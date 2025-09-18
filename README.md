# HotelHousekeeping

A Java console program that simulates a hotel’s weekly housekeeping and occupancy workflow. It tracks room occupation, housekeeping status, worker availability/assignments, and generates daily and weekly text reports.

---

## Features

- 3 floors × 10 rooms (01–10) per floor
- Randomized initial **Occupation** and **Housekeeping** states
- 7-day simulation loop with daily actions
- Clean rooms by room number and assign available workers
- Reasonable hotel rules enforced (see **Rules & Logic**)
- Daily report: `StatusofHotel.txt`
- Weekly summary: `HousekeepingStatistics.txt`

---

## Requirements

- Java 8+ (JDK)
- A terminal/command prompt

---

## Build & Run

```bash
# compile
javac Main.java

# run
java Main
