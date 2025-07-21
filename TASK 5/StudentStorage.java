import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentStorage {
    private static final String FILE_NAME = "students.csv";
    private static final String DELIMITER = ",";
    
    // Save students to CSV file
    public static boolean saveToFile(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header
            writer.println("RollNumber,Name,Grade");
            
            // Write student data
            for (Student student : students) {
                writer.println(student.getRollNumber() + DELIMITER + 
                             escapeSpecialCharacters(student.getName()) + DELIMITER + 
                             escapeSpecialCharacters(student.getGrade()));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
            return false;
        }
    }
    
    // Load students from CSV file
    public static List<Student> loadFromFile() {
        List<Student> students = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        if (!file.exists()) {
            return students; // Return empty list if file doesn't exist
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                
                Student student = parseStudent(line);
                if (student != null) {
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
        
        return students;
    }
    
    // Parse a CSV line to create a Student object
    private static Student parseStudent(String line) {
        try {
            String[] parts = line.split(DELIMITER);
            if (parts.length >= 3) {
                int rollNumber = Integer.parseInt(parts[0].trim());
                String name = unescapeSpecialCharacters(parts[1].trim());
                String grade = unescapeSpecialCharacters(parts[2].trim());
                return new Student(rollNumber, name, grade);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing student data: " + line);
        }
        return null;
    }
    
    // Escape special characters for CSV
    private static String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    
    // Unescape special characters from CSV
    private static String unescapeSpecialCharacters(String data) {
        if (data == null) return "";
        
        if (data.startsWith("\"") && data.endsWith("\"")) {
            data = data.substring(1, data.length() - 1);
            data = data.replace("\"\"", "\"");
        }
        return data;
    }
    
    // Check if storage file exists
    public static boolean storageFileExists() {
        return new File(FILE_NAME).exists();
    }
    
    // Delete storage file
    public static boolean deleteStorageFile() {
        File file = new File(FILE_NAME);
        return file.exists() && file.delete();
    }
} 