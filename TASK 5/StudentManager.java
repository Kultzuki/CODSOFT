import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentManager {
    private List<Student> students;
    
    public StudentManager() {
        this.students = new ArrayList<>();
    }
    
    // Add a student
    public boolean addStudent(Student student) {
        // Check if student with same roll number already exists
        if (searchStudent(student.getRollNumber()).isPresent()) {
            return false; // Student already exists
        }
        return students.add(student);
    }
    
    // Remove a student by roll number
    public boolean removeStudent(int rollNumber) {
        return students.removeIf(student -> student.getRollNumber() == rollNumber);
    }
    
    // Search for a student by roll number
    public Optional<Student> searchStudent(int rollNumber) {
        return students.stream()
                .filter(student -> student.getRollNumber() == rollNumber)
                .findFirst();
    }
    
    // Search students by name (partial match)
    public List<Student> searchStudentsByName(String name) {
        return students.stream()
                .filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
    
    // Update a student
    public boolean updateStudent(Student updatedStudent) {
        Optional<Student> existingStudent = searchStudent(updatedStudent.getRollNumber());
        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            student.setName(updatedStudent.getName());
            student.setGrade(updatedStudent.getGrade());
            return true;
        }
        return false;
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }
    
    // Get total number of students
    public int getStudentCount() {
        return students.size();
    }
    
    // Clear all students
    public void clearAllStudents() {
        students.clear();
    }
    
    // Set the students list (used when loading from file)
    public void setStudents(List<Student> students) {
        this.students = new ArrayList<>(students);
    }
    
    // Check if roll number exists
    public boolean rollNumberExists(int rollNumber) {
        return searchStudent(rollNumber).isPresent();
    }
} 