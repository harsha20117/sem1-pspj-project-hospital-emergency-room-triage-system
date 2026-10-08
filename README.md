# ER Triage & Patient Management System

A console-based Java application that prioritises emergency-room patients by urgency instead of arrival time.

**Course:** Problem Solving Using Programming (Java) · 26SC1101E
**University:** KL Deemed to be University

---

## Abstract

In an emergency room, deciding who is treated first can save a life. This program reads each patient's age, consciousness, heart rate, pain level and complaint type, applies clear triage rules, and labels the patient **RED**, **YELLOW** or **GREEN**. Patients are stored in arrays, and a menu lets staff view the treatment queue, search by name and see statistics. The program is built from small reusable methods, with input validation and recursion.

## Features

- Input validation: invalid values are re-asked until correct
- Rule-based triage into three categories (RED / YELLOW / GREEN)
- No-pulse handling: the patient is recorded but given no category
- Treatment queue ordered RED, then YELLOW, then GREEN
- Search a patient by name
- Statistics: category counts, average age, average pain, total pain
- Menu-driven interface that runs until Exit

## Triage Rules

| Category | Meaning | Condition |
|----------|---------|-----------|
| **RED** | Immediate | Unconscious, **or** heart rate < 40 or > 150, **or** pain ≥ 9 |
| **YELLOW** | Urgent | Heart rate 40–49 or 111–150, **or** pain ≥ 6, **or** injury with pain ≥ 5, **or** age ≤ 5 / ≥ 65 with pain ≥ 4 |
| **GREEN** | Can wait | Everything else |

A heart rate of `0` means no pulse. The patient is recorded but not triaged.

## Input Format

| Field | Accepted values |
|-------|-----------------|
| Name | Any text |
| Age | Whole number |
| Conscious? | `1` = yes, `0` = no |
| Heart rate | `30–250`, or `0` if no pulse |
| Pain level | `1–10` |
| Complaint type | `1` = Injury, `2` = Illness |

## Program Flow

**Phase 1: Register patients**
1. Ask for the number of patients.
2. For each patient, read name, age, consciousness and heart rate.
3. If heart rate is `0`, mark as no pulse and skip to the next patient.
4. Otherwise read pain level and complaint type, call `triage()`, and store the result.

**Phase 2: Menu loop** (repeats until Exit)

```
1  View treatment queue
2  Search patient by name
3  View statistics
4  Exit
```

## How to Run

Requires JDK 8 or later.

```bash
javac ER.java
java ER
```

## Sample Output

```
--- Patient 2 ---
Patient name: Ravi
Age: 70
Is the patient conscious? (1=yes, 0=no): 1
Heart rate (30-250, or 0 if no pulse): 95
Pain level (1-10): 5
Complaint type (1=Injury, 2=Illness): 2
Triage result: YELLOW
```

```
TREATMENT QUEUE
1. Kiran - RED (pain 4/10)
2. Ravi - YELLOW (pain 5/10)
3. Sneha - YELLOW (pain 3/10)
4. Asha - GREEN (pain 5/10)

Name to search: Ravi
Ravi - age 70, pain 5/10, YELLOW

STATISTICS
Triaged patients : 4
No pulse         : 1
RED : 1   YELLOW : 2   GREEN : 1
Average age  : 39.3
Average pain : 4.3
Total pain   : 17
```

## Course Outcomes Covered

| CO | Module | Concepts used |
|----|--------|---------------|
| **CO1** | Module 1 | Primitive types, variables, operators, `Scanner` input, `printf` output, type casting `(double) sum / count` |
| **CO2** | Module 2 | `if / else-if` ladder in `triage()`, `switch` menu, `do-while` validation, `for` loops, `continue` |
| **CO3** | Module 3 | Methods with parameters and return values, overloading (`printLine()` / `printLine(title)`), recursion (`totalPain()`), 1D arrays for search, sum, average and count |

## Limitations

- Data is not saved. Everything is lost when the program exits.
- Patients are entered once at the start, so none can be added later.
- This is a learning project and not a replacement for real clinical triage systems (ESI, Manchester Triage).

## Author

Your Name (ID)
