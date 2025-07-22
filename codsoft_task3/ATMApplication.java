public class ATMApplication {
    public static void main(String[] args) {
        BankAccount account = new BankAccount(0.0);
        ATM atm = new ATM(account);
        atm.start();
    }
} 