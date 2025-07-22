import java.io.*;
import java.net.*;
import java.util.*;

public class CurrencyConverter {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    private static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<>();
    
    private static final String[] AVAILABLE_CURRENCIES = {
        "USD - US Dollar",
        "EUR - Euro", 
        "GBP - British Pound",
        "JPY - Japanese Yen",
        "INR - Indian Rupee",
        "CAD - Canadian Dollar",
        "AUD - Australian Dollar",
        "CHF - Swiss Franc",
        "CNY - Chinese Yuan",
        "KRW - South Korean Won",
        "SGD - Singapore Dollar",
        "HKD - Hong Kong Dollar",
        "SEK - Swedish Krona",
        "NOK - Norwegian Krone",
        "DKK - Danish Krone",
        "PLN - Polish Zloty",
        "CZK - Czech Koruna",
        "HUF - Hungarian Forint",
        "RUB - Russian Ruble",
        "BRL - Brazilian Real"
    };
    
    static {
        CURRENCY_SYMBOLS.put("USD", "$");
        CURRENCY_SYMBOLS.put("EUR", "€");
        CURRENCY_SYMBOLS.put("GBP", "£");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        CURRENCY_SYMBOLS.put("INR", "₹");
        CURRENCY_SYMBOLS.put("CAD", "CAD");
        CURRENCY_SYMBOLS.put("AUD", "AUD");
        CURRENCY_SYMBOLS.put("CHF", "CHF");
        CURRENCY_SYMBOLS.put("CNY", "CNY");
        CURRENCY_SYMBOLS.put("KRW", "KRW");
        CURRENCY_SYMBOLS.put("SGD", "SGD");
        CURRENCY_SYMBOLS.put("HKD", "HKD");
        CURRENCY_SYMBOLS.put("SEK", "SEK");
        CURRENCY_SYMBOLS.put("NOK", "NOK");
        CURRENCY_SYMBOLS.put("DKK", "DKK");
        CURRENCY_SYMBOLS.put("PLN", "PLN");
        CURRENCY_SYMBOLS.put("CZK", "CZK");
        CURRENCY_SYMBOLS.put("HUF", "HUF");
        CURRENCY_SYMBOLS.put("RUB", "RUB");
        CURRENCY_SYMBOLS.put("BRL", "BRL");
    }
    
    private Scanner scanner;
    
    public CurrencyConverter() {
        this.scanner = new Scanner(System.in);
        System.out.println("        CURRENCY CONVERTER");
        System.out.println("===========================================");
        System.out.println("Welcome to the Currency Converter!");
        System.out.println("Convert between multiple currencies with real-time rates.\n");
    }
    
    public void start() {
        try {
            while (true) {
                String baseCurrency = selectCurrency("base");
                if (baseCurrency == null) break;
                
                String targetCurrency = selectCurrency("target");
                if (targetCurrency == null) break;
                
                if (baseCurrency.equals(targetCurrency)) {
                    System.out.println("ERROR: Base and target currencies cannot be the same!");
                    continue;
                }
                
                double amount = getAmountInput(baseCurrency);
                if (amount < 0) break;
                
                performConversion(baseCurrency, targetCurrency, amount);
                
                if (!askToContinue()) break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please try again.\n");
        }
        
        System.out.println("\n===========================================");
        System.out.println("Thank you for using Currency Converter!");
        System.out.println("===========================================");
        scanner.close();
    }
    
    private String selectCurrency(String type) {
        System.out.println("\nAvailable Currencies:");
        System.out.println("---------------------------------------------");
        
        for (int i = 0; i < AVAILABLE_CURRENCIES.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, AVAILABLE_CURRENCIES[i]);
        }
        System.out.println(" 0. Exit");
        System.out.println("---------------------------------------------");
        
        System.out.printf("Select %s currency (1-%d) or 0 to exit: ", type, AVAILABLE_CURRENCIES.length);
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return null;
            
            if (choice >= 1 && choice <= AVAILABLE_CURRENCIES.length) {
                String currencyCode = AVAILABLE_CURRENCIES[choice - 1].substring(0, 3);
                System.out.printf("SUCCESS: Selected %s currency: %s%n", type, AVAILABLE_CURRENCIES[choice - 1]);
                return currencyCode;
            } else {
                System.out.printf("ERROR: Invalid choice! Please enter a number between 1 and %d.%n", AVAILABLE_CURRENCIES.length);
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Please enter a valid number!");
        }
        
        return selectCurrency(type);
    }
    
    private double getAmountInput(String currency) {
        String symbol = CURRENCY_SYMBOLS.getOrDefault(currency, currency);
        
        while (true) {
            System.out.printf("%nEnter amount in %s (%s) or 0 to exit: ", currency, symbol);
            
            try {
                String input = scanner.nextLine().trim();
                input = input.replace(symbol, "").replace(",", "").trim();
                
                double amount = Double.parseDouble(input);
                
                if (amount == 0.0) return -1.0;
                
                if (amount > 0) return amount;
                
                System.out.println("ERROR: Please enter a positive amount!");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid number!");
            }
        }
    }
    
    private void performConversion(String from, String to, double amount) {
        try {
            System.out.println("\nFetching real-time exchange rates...");
            
            double exchangeRate = fetchExchangeRate(from, to);
            double convertedAmount = amount * exchangeRate;
            
            displayResult(from, to, amount, convertedAmount, exchangeRate);
            
        } catch (Exception e) {
            System.out.println("Error fetching exchange rate: " + e.getMessage());
            System.out.println("Please check your internet connection and try again.");
        }
    }
    
    private double fetchExchangeRate(String from, String to) throws Exception {
        String urlString = API_URL + from;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP error code: " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        
        reader.close();
        connection.disconnect();
        
        String jsonResponse = response.toString();
        double rate = parseExchangeRate(jsonResponse, to);
        
        return rate;
    }
    
    private double parseExchangeRate(String jsonResponse, String targetCurrency) throws Exception {
        String ratesSection = "\"rates\":{";
        int ratesIndex = jsonResponse.indexOf(ratesSection);
        
        if (ratesIndex == -1) {
            throw new Exception("Invalid JSON response: rates section not found");
        }
        
        String currencyPattern = "\"" + targetCurrency + "\":";
        int currencyIndex = jsonResponse.indexOf(currencyPattern, ratesIndex);
        
        if (currencyIndex == -1) {
            throw new Exception("Currency not found: " + targetCurrency);
        }
        
        int valueStart = currencyIndex + currencyPattern.length();
        int valueEnd = jsonResponse.indexOf(",", valueStart);
        
        if (valueEnd == -1) {
            valueEnd = jsonResponse.indexOf("}", valueStart);
        }
        
        if (valueEnd == -1) {
            throw new Exception("Invalid JSON format: could not find end of rate value");
        }
        
        String rateString = jsonResponse.substring(valueStart, valueEnd).trim();
        
        try {
            return Double.parseDouble(rateString);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid rate format: " + rateString);
        }
    }
    
    private void displayResult(String from, String to, double originalAmount, double convertedAmount, double exchangeRate) {
        String fromSymbol = CURRENCY_SYMBOLS.getOrDefault(from, from);
        String toSymbol = CURRENCY_SYMBOLS.getOrDefault(to, to);
        
        System.out.println("\nCONVERSION SUCCESSFUL!");
        System.out.println("===============================================");
        System.out.printf("Exchange Rate: 1 %s = %.6f %s%n", from, exchangeRate, to);
        System.out.println("-----------------------------------------------");
        System.out.printf("Original Amount: %.2f %s%n", originalAmount, from);
        System.out.printf("Converted Amount: %.2f %s%n", convertedAmount, to);
        System.out.println("===============================================");
        System.out.printf("Info: You are %s %.2f %s for every 1 %s%n", 
            (exchangeRate > 1.0) ? "getting" : "paying", 
            Math.abs(exchangeRate), to, from);
    }
    
    private boolean askToContinue() {
        while (true) {
            System.out.print("\nWould you like to perform another conversion? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("y") || response.equals("yes")) {
                return true;
            } else if (response.equals("n") || response.equals("no")) {
                return false;
            } else {
                System.out.println("ERROR: Please enter 'y' for yes or 'n' for no.");
            }
        }
    }
    
    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.start();
    }
} 