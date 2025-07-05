import java.util.Scanner;
import java.util.Random;

public class NumberGame {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        int totalRounds = 0;
        int totalScore = 0;
        boolean again = true;

        System.out.println("Welcome to the Number Guessing Game!");

        while (again) {
            int numberToGuess = rand.nextInt(100) + 1;
            int totalAttempts = 3;
            int attempts = 0;
            boolean guessedCorrectly = false;

            System.out.println("\nRound " + (totalRounds + 1));
            System.out.println("Guess the number between 1 and 100. You have " + totalAttempts + " attempts.");

            while (attempts < totalAttempts) {
                System.out.print("Enter your guess: ");
                int userGuess = sc.nextInt();
                attempts++;

                if (userGuess == numberToGuess) {
                    System.out.println("Correct! You guessed the number in " + attempts + " attempts.");
                    totalScore += (totalAttempts - attempts + 1);
                    guessedCorrectly = true;
                    break;
                } else if (userGuess < numberToGuess) {
                    System.out.println("Guess is too low!");
                } else {
                    System.out.println("Guess is too high!");
                }
            }

            if (!guessedCorrectly) {
                System.out.println("You've used all attempts. The correct number was: " + numberToGuess);
            }

            totalRounds++;

            System.out.print("\nDo you want to play another round? (yes/no): ");
            sc.nextLine();
            String response = sc.nextLine().toLowerCase();
            again = response.equals("yes") || response.equals("y");
        }

        System.out.println("\nGame Over!");
        System.out.println("Total rounds played by you: " + totalRounds);
        System.out.println("Your total Score: " + totalScore);

        sc.close();
    }
}