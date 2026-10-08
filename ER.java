import java.util.Scanner;
public class ER {
    static Scanner sc = new Scanner(System.in);

    //  Input helpers 

    // keeps asking until the number is between min and max
    static int readInt(String prompt, int min, int max) {
        int value;
        do {
            System.out.print(prompt);
            value = sc.nextInt();
            if (value < min || value > max)
                System.out.println("Enter a value between " + min + " and " + max + ".");
        } while (value < min || value > max);
        return value;
    }

    // heart rate is 0 (no pulse) or between 30 and 250
    static int readHeartRate() {
        int hr;
        do {
            System.out.print("Heart rate (30-250, or 0 if no pulse): ");
            hr = sc.nextInt();
            if (hr != 0 && (hr < 30 || hr > 250))
                System.out.println("That's not a realistic heart rate, try again.");
        } while (hr != 0 && (hr < 30 || hr > 250));
        return hr;
    }

    // Triage 

    // returns 1 = RED, 2 = YELLOW, 3 = GREEN
    static int triage(int age, int conscious, int hr, int pain, int complaint) {
        if (conscious == 0 || hr < 40 || hr > 150 || pain >= 9) {
            return 1;
        } else if (hr < 50 || hr > 110 || pain >= 6
                || (complaint == 1 && pain >= 5)
                || ((age <= 5 || age >= 65) && pain >= 4)) {
            return 2;
        } else {
            return 3;
        }
    }

    static String categoryName(int category) {
        if (category == 1) return "RED";
        else if (category == 2) return "YELLOW";
        else return "GREEN";
    }

    //  Array work

    // counting: how many patients have this category
    static int countCategory(int[] categories, int count, int wanted) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            if (categories[i] == wanted)
                total++;
        }
        return total;
    }

    // average of the first "count" values
    static double average(int[] values, int count) {
        int sum = 0;
        for (int i = 0; i < count; i++)
            sum += values[i];
        return (double) sum / count;
    }

    // recursion: adds up the pain levels from index onwards
    static int totalPain(int[] pains, int index, int count) {
        if (index == count)                          // base case
            return 0;
        return pains[index] + totalPain(pains, index + 1, count);   // recursive case
    }

    // searching: returns the position of the name, or -1 if not found
    static int findPatient(String[] names, int count, String target) {
        for (int i = 0; i < count; i++) {
            if (names[i].equals(target))
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

    // treatment order: all RED first, then YELLOW, then GREEN
    static void showQueue(String[] names, int[] pains, int[] categories, int count) {
        printLine("TREATMENT QUEUE");
        if (count == 0) {
            System.out.println("No patients waiting.");
            return;
        }
        int position = 1;
        for (int cat = 1; cat <= 3; cat++) {
            for (int i = 0; i < count; i++) {
                if (categories[i] == cat) {
                    System.out.println(position + ". " + names[i] + " - " + categoryName(cat)
                            + " (pain " + pains[i] + "/10)");
                    position++;
                }
            }
        }
    }

    static void showStats(int[] ages, int[] pains, int[] categories, int count, int noPulse) {
        printLine("STATISTICS");
        System.out.println("Triaged patients : " + count);
        System.out.println("No pulse         : " + noPulse);

        if (count == 0)
            return;

        String[] labels = {"RED", "YELLOW", "GREEN"};
        int cat = 1;
        for (String label : labels) {                // enhanced for loop
            System.out.println(label + " : " + countCategory(categories, count, cat));
            cat++;
        }

        System.out.printf("Average age  : %.1f%n", average(ages, count));
        System.out.printf("Average pain : %.1f%n", average(pains, count));
        System.out.println("Total pain   : " + totalPain(pains, 0, count));
    }

    // Main 

    public static void main(String[] args) {
        int patients = readInt("How many patients? ", 1, 100);

        String[] names = new String[patients];
        int[] ages = new int[patients];
        int[] pains = new int[patients];
        int[] categories = new int[patients];

        int count = 0;      // how many patients were triaged and stored
        int noPulse = 0;    // patients with no pulse

        for (int i = 1; i <= patients; i++) {
            System.out.println("\n--- Patient " + i + " ---");

            System.out.print("Patient name: ");
            String name = sc.next();

            int age = readInt("Age: ", 0, 120);
            int conscious = readInt("Is the patient conscious? (1=yes, 0=no): ", 0, 1);
            int heartRate = readHeartRate();

            if (heartRate == 0) {
                System.out.println(name + " has no pulse - declared dead, no triage category.");
                noPulse++;
                continue;
            }

            int pain = readInt("Pain level (1-10): ", 1, 10);
            int complaint = readInt("Complaint type (1=Injury/Trauma, 2=Illness/Medical): ", 1, 2);

            int category = triage(age, conscious, heartRate, pain, complaint);
            System.out.println("Triage result: " + categoryName(category));

            names[count] = name;
            ages[count] = age;
            pains[count] = pain;
            categories[count] = category;
            count++;
        }

        //  Menu
        int choice;
        do {
            System.out.println();
            printLine("ER MENU");
            System.out.println("1. View treatment queue");
            System.out.println("2. Search patient by name");
            System.out.println("3. View statistics");
            System.out.println("4. Exit");
            choice = readInt("Choice: ", 1, 4);

            switch (choice) {
                case 1:
                    showQueue(names, pains, categories, count);
                    break;
                case 2:
                    System.out.print("Name to search: ");
                    String target = sc.next();
                    int found = findPatient(names, count, target);
                    if (found == -1)
                        System.out.println("No triaged patient named " + target + ".");
                    else
                        System.out.println(names[found] + " - age " + ages[found] + ", pain "
                                + pains[found] + "/10, " + categoryName(categories[found]));
                    break;
                case 3:
                    showStats(ages, pains, categories, count, noPulse);
                    break;
                default:
                    System.out.println("Goodbye!");
            }
        } while (choice != 4);

        sc.close();
    }
}