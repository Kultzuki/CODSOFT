import java.util.Scanner;

public class GradeCalculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of subjects: ");
        int subjectCount = sc.nextInt();

        int[] marks = new int[subjectCount];
        int total = 0;

        for (int i = 0; i < subjectCount; i++) {
            System.out.print("Enter marks for Subject " + (i + 1) + " (out of 100): ");
            marks[i] = sc.nextInt();

            while (marks[i] < 0 || marks[i] > 100) {
                System.out.print("Invalid! Enter marks between 0 and 100: ");
                marks[i] = sc.nextInt();
            }

            total += marks[i];
        }

        double average = (double) total / subjectCount;

        String grade;
        if (average >= 90) {
            grade = "A+";
        } else if (average >= 80) {
            grade = "A";
        } else if (average >= 70) {
            grade = "B";
        } else if (average >= 60) {
            grade = "C";
        } else if (average >= 50) {
            grade = "D";
        } else {
            grade = "F (Fail)";
        }

        System.out.println("\nResult Summary:");
        System.out.println("Total Marks      : " + total + " / " + (subjectCount * 100));
        System.out.printf("Average Percentage: %.2f%%\n", average);
        System.out.println("Grade            : " + grade);

        sc.close();
    }
}
