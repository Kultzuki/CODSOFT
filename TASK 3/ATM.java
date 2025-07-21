import java.util.Scanner;

public class ATM {
    private final BankAccount account;
    private final Scanner scanner;

    public ATM(BankAccount account) {
        this.account = account;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the ATM!");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    performWithdraw();
                    break;
                case 2:
                    performDeposit();
                    break;
                case 3:
                    performCheckBalance();
                    break;
                case 4:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Withdraw");
        System.out.println("2. Deposit");
        System.out.println("3. Check Balance");
        System.out.println("4. Exit");
    }

    private void performWithdraw() {
        double amount = readDouble("Enter amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        if (account.withdraw(amount)) {
            System.out.printf("Withdrawal successful. New balance: %.2f%n", account.getBalance());
        } else {
            System.out.println("Withdrawal failed. Check amount or balance (insufficient funds).");
        }
    }

    private void performDeposit() {
        double amount = readDouble("Enter amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        if (account.deposit(amount)) {
            System.out.printf("Deposit successful. New balance: %.2f%n", account.getBalance());
        } else {
            System.out.println("Deposit failed. Please try again.");
        }
    }

    private void performCheckBalance() {
        System.out.printf("Current balance: %.2f%n", account.getBalance());
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            System.out.print(prompt);
        }
        return scanner.nextInt();
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a numeric value.");
            scanner.next();
            System.out.print(prompt);
        }
        return scanner.nextDouble();
    }
} 