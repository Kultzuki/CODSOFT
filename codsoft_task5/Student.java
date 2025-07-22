public class Student implements Comparable<Student> {
    private int rollNumber;
    private String name;
    private String grade;
    
    // Default constructor
    public Student() {}
    
    // Parameterized constructor
    public Student(int rollNumber, String name, String grade) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.grade = grade;
    }
    
    // Getters
    public int getRollNumber() {
        return rollNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGrade() {
        return grade;
    }
    
    // Setters
    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    // toString method
    @Override
    public String toString() {
        return "Student{" +
                "rollNumber=" + rollNumber +
                ", name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
    
    // equals method for comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return rollNumber == student.rollNumber;
    }
    
    // hashCode method
    @Override
    public int hashCode() {
        return Integer.hashCode(rollNumber);
    }
    
    // compareTo method for Comparable interface (sort by roll number)
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.rollNumber, other.rollNumber);
    }
} 