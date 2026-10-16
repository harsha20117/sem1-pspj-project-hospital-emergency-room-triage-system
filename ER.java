import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ER {
    static Scanner sc = new Scanner(System.in);

    //  Input helpers

    // throws our own exception if the value is outside min..max
    static void checkRange(int value, int min, int max) throws InvalidVitalsException {
        if (value < min || value > max)
            throw new InvalidVitalsException("Enter a value between " + min + " and " + max + ".");
    }

    // keeps asking until a valid number between min and max is typed
    static int readInt(String prompt, int min, int max) {
        int value = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            try {
                value = sc.nextInt();
                checkRange(value, min, max);
                valid = true;
            } catch (InputMismatchException e) {
                System.out.println("That's not a number, try again.");
                sc.next();              // throw away the bad word
            } catch (InvalidVitalsException e) {
                System.out.println(e.getMessage());
            } finally {
                sc.nextLine();          // clear the rest of the line
            }
        }
        return value;
    }

    // heart rate is 0 (no pulse) or between 30 and 250
    static int readHeartRate() {
        int hr = readInt("Heart rate (30-250, or 0 if no pulse): ", 0, 250);
        while (hr != 0 && hr < 30) {
            System.out.println("That's not a realistic heart rate, try again.");
            hr = readInt("Heart rate (30-250, or 0 if no pulse): ", 0, 250);
        }
        return hr;
    }

    // keeps asking until the name is not empty
    static String readName(String prompt) {
        String name;
        do {
            System.out.print(prompt);
            name = sc.nextLine().trim();
            if (name.isEmpty())
                System.out.println("Name can't be empty.");
        } while (name.isEmpty());
        return name;
    }

    //  List work

    // searching: returns the position of the name (ignoring case), or -1 if not found
    static int findPatient(ArrayList<Patient> patients, String target) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getName().equalsIgnoreCase(target))
                return i;
        }
        return -1;
    }

    //  Output

    static void printLine() {
        System.out.println("==============================");
    }

    // same name, different parameters (method overloading)
    static void printLine(String title) {
        printLine();
        System.out.println(" " + title);
        printLine();
    }

    // treatment order: sorted by category, then highest pain first (see Patient.compareTo)
    static void showQueue(ArrayList<Patient> patients) {
        printLine("TREATMENT QUEUE");
        if (patients.isEmpty()) {
            System.out.println("No patients waiting.");
            return;
        }
        Collections.sort(patients);
        int position = 1;
        for (Patient p : patients) {
            System.out.println(position + ". " + p);
            position++;
        }
    }

    // Main

    public static void main(String[] args) {
        int total = readInt("How many patients? ", 1, 100);

        ArrayList<Patient> patients = new ArrayList<>();

        for (int i = 1; i <= total; i++) {
            System.out.println("\n--- Patient " + i + " ---");

            String name = readName("Patient name: ");
            int age = readInt("Age: ", 0, 120);
            boolean conscious = readInt("Is the patient conscious? (1=yes, 0=no): ", 0, 1) == 1;
            int heartRate = readHeartRate();

            if (heartRate == 0) {
                System.out.println(name + " has no pulse - declared dead, no triage category.");
                continue;
            }

            int pain = readInt("Pain level (1-10): ", 1, 10);
            int complaint = readInt("Complaint type (1=Injury/Trauma, 2=Illness/Medical): ", 1, 2);

            Patient p;
            if (complaint == Patient.INJURY)
                p = new TraumaPatient(name, age, conscious, heartRate, pain);
            else
                p = new MedicalPatient(name, age, conscious, heartRate, pain);

            System.out.println("Triage result: " + Patient.categoryName(p.getCategory()));
            patients.add(p);
        }

        //  Menu
        int choice;
        do {
            System.out.println();
            printLine("ER MENU");
            System.out.println("1. View treatment queue");
            System.out.println("2. Search patient by name");
            System.out.println("3. Exit");
            choice = readInt("Choice: ", 1, 3);

            switch (choice) {
                case 1:
                    showQueue(patients);
                    break;
                case 2:
                    String target = readName("Name to search: ");
                    int found = findPatient(patients, target);
                    if (found == -1)
                        System.out.println("No triaged patient named " + target + ".");
                    else
                        System.out.println(patients.get(found));
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    break;
            }
        } while (choice != 3);

        sc.close();
    }
}

//  Our own exception (checked, because it extends Exception)
class InvalidVitalsException extends Exception {
    InvalidVitalsException(String message) {
        super(message);
    }
}

//  Classes

class Patient implements Comparable<Patient> {
    // constants instead of magic numbers
    static final int RED = 1;
    static final int YELLOW = 2;
    static final int GREEN = 3;
    static final int INJURY = 1;
    static final int ILLNESS = 2;

    private String name;
    private int age;
    private boolean conscious;
    private int heartRate;
    private int pain;

    // default constructor
    Patient() {
        this("Unknown", 0, true, 80, 1);
    }

    // parameterized constructor (constructor overloading)
    Patient(String name, int age, boolean conscious, int heartRate, int pain) {
        this.name = name;
        this.age = age;
        this.conscious = conscious;
        this.heartRate = heartRate;
        this.pain = pain;
    }

    String getName() { return name; }
    int getAge() { return age; }
    int getPain() { return pain; }

    // pain level from which this patient becomes YELLOW (subclasses can change it)
    int yellowPainLimit() {
        return 6;
    }

    String getType() {
        return "General";
    }

    // returns 1 = RED, 2 = YELLOW, 3 = GREEN
    int getCategory() {
        if (!conscious || heartRate < 40 || heartRate > 150 || pain >= 9) {
            return RED;
        } else if (heartRate < 50 || heartRate > 110 || pain >= yellowPainLimit()
                || ((age <= 5 || age >= 65) && pain >= 4)) {
            return YELLOW;
        } else {
            return GREEN;
        }
    }

    static String categoryName(int category) {
        if (category == RED) return "RED";
        else if (category == YELLOW) return "YELLOW";
        else return "GREEN";
    }

    // sorting order: lower category first, then higher pain first
    @Override
    public int compareTo(Patient other) {
        if (getCategory() != other.getCategory())
            return getCategory() - other.getCategory();
        return other.pain - pain;
    }

    @Override
    public String toString() {
        return name + " - " + categoryName(getCategory()) + " (" + getType() + ", age "
                + age + ", pain " + pain + "/10)";
    }
}

// is-a Patient: injuries become YELLOW at a lower pain level
class TraumaPatient extends Patient {
    TraumaPatient(String name, int age, boolean conscious, int heartRate, int pain) {
        super(name, age, conscious, heartRate, pain);
    }

    @Override
    int yellowPainLimit() {
        return 5;
    }

    @Override
    String getType() {
        return "Injury/Trauma";
    }
}

// is-a Patient: uses the normal pain limit from Patient
class MedicalPatient extends Patient {
    MedicalPatient(String name, int age, boolean conscious, int heartRate, int pain) {
        super(name, age, conscious, heartRate, pain);
    }

    @Override
    String getType() {
        return "Illness/Medical";
    }
}