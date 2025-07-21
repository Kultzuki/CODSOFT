import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {
    private StudentManager studentManager;
    private Stage primaryStage;
    private TableView<Student> studentTable;
    private ObservableList<Student> studentData;
    private TextField searchField;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.studentManager = new StudentManager();
        this.studentData = FXCollections.observableArrayList();
        
        // Load existing data
        loadDataFromFile();
        
        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(createMainMenuScene());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        
        // Save data on close
        primaryStage.setOnCloseRequest(e -> saveDataToFile());
    }
    
    // Create main menu scene
    private Scene createMainMenuScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        // Title
        Label title = new Label("Student Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // Buttons
        Button addBtn = createStyledButton("Add Student", "#27ae60");
        Button viewBtn = createStyledButton("View All Students", "#3498db");
        Button searchBtn = createStyledButton("Search Student", "#e74c3c");
        Button exitBtn = createStyledButton("Exit", "#95a5a6");
        
        addBtn.setOnAction(e -> primaryStage.setScene(createAddStudentScene()));
        viewBtn.setOnAction(e -> primaryStage.setScene(createViewStudentsScene()));
        searchBtn.setOnAction(e -> primaryStage.setScene(createSearchScene()));
        exitBtn.setOnAction(e -> {
            saveDataToFile();
            primaryStage.close();
        });
        
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addBtn, viewBtn, searchBtn, exitBtn);
        
        root.getChildren().addAll(title, buttonBox);
        
        return new Scene(root, 800, 600);
    }
    
    // Create add student scene
    private Scene createAddStudentScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        Label title = new Label("Add New Student");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // Form fields
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        
        Label rollLabel = new Label("Roll Number:");
        TextField rollField = new TextField();
        rollField.setPromptText("Enter roll number");
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter student name");
        
        Label gradeLabel = new Label("Grade:");
        TextField gradeField = new TextField();
        gradeField.setPromptText("Enter grade (e.g., A, B, C)");
        
        // Input validation
        rollField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                rollField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
        
        form.add(rollLabel, 0, 0);
        form.add(rollField, 1, 0);
        form.add(nameLabel, 0, 1);
        form.add(nameField, 1, 1);
        form.add(gradeLabel, 0, 2);
        form.add(gradeField, 1, 2);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveBtn = createStyledButton("Save Student", "#27ae60");
        Button backBtn = createStyledButton("Back to Menu", "#95a5a6");
        
        saveBtn.setOnAction(e -> {
            if (validateInput(rollField.getText(), nameField.getText(), gradeField.getText())) {
                int rollNumber = Integer.parseInt(rollField.getText());
                String name = nameField.getText().trim();
                String grade = gradeField.getText().trim();
                
                Student student = new Student(rollNumber, name, grade);
                if (studentManager.addStudent(student)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student added successfully!");
                    rollField.clear();
                    nameField.clear();
                    gradeField.clear();
                    saveDataToFile();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Student with this roll number already exists!");
                }
            }
        });
        
        backBtn.setOnAction(e -> primaryStage.setScene(createMainMenuScene()));
        
        buttonBox.getChildren().addAll(saveBtn, backBtn);
        
        root.getChildren().addAll(title, form, buttonBox);
        
        return new Scene(root, 800, 600);
    }
    
    // Create view students scene
    private Scene createViewStudentsScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        Label title = new Label("All Students");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        title.setAlignment(Pos.CENTER);
        
        // Search bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchField = new TextField();
        searchField.setPromptText("Search by name or roll number...");
        searchField.setPrefWidth(300);
        
        Button searchBtn = createStyledButton("Search", "#3498db");
        Button clearBtn = createStyledButton("Clear", "#f39c12");
        
        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchBtn, clearBtn);
        
        // Table
        createStudentTable();
        refreshTable();
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button editBtn = createStyledButton("Edit Selected", "#f39c12");
        Button deleteBtn = createStyledButton("Delete Selected", "#e74c3c");
        Button backBtn = createStyledButton("Back to Menu", "#95a5a6");
        
        // Event handlers
        searchBtn.setOnAction(e -> performSearch());
        clearBtn.setOnAction(e -> {
            searchField.clear();
            refreshTable();
        });
        
        editBtn.setOnAction(e -> {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                primaryStage.setScene(createEditStudentScene(selected));
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a student to edit.");
            }
        });
        
        deleteBtn.setOnAction(e -> {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText("Delete Student");
                confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?");
                
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    studentManager.removeStudent(selected.getRollNumber());
                    refreshTable();
                    saveDataToFile();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully!");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a student to delete.");
            }
        });
        
        backBtn.setOnAction(e -> primaryStage.setScene(createMainMenuScene()));
        
        buttonBox.getChildren().addAll(editBtn, deleteBtn, backBtn);
        
        root.getChildren().addAll(title, searchBox, studentTable, buttonBox);
        
        return new Scene(root, 800, 600);
    }
    
    // Create search scene
    private Scene createSearchScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        Label title = new Label("Search Student");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // Search form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        
        Label rollLabel = new Label("Roll Number:");
        TextField rollField = new TextField();
        rollField.setPromptText("Enter roll number to search");
        
        // Input validation
        rollField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                rollField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
        
        form.add(rollLabel, 0, 0);
        form.add(rollField, 1, 0);
        
        // Result area
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);
        resultArea.setStyle("-fx-font-family: monospace;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button searchBtn = createStyledButton("Search", "#3498db");
        Button backBtn = createStyledButton("Back to Menu", "#95a5a6");
        
        searchBtn.setOnAction(e -> {
            String rollText = rollField.getText().trim();
            if (rollText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a roll number to search.");
                return;
            }
            
            try {
                int rollNumber = Integer.parseInt(rollText);
                var student = studentManager.searchStudent(rollNumber);
                if (student.isPresent()) {
                    Student s = student.get();
                    resultArea.setText("Student Found:\n\n" +
                            "Roll Number: " + s.getRollNumber() + "\n" +
                            "Name: " + s.getName() + "\n" +
                            "Grade: " + s.getGrade());
                } else {
                    resultArea.setText("No student found with roll number: " + rollNumber);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid roll number format.");
            }
        });
        
        backBtn.setOnAction(e -> primaryStage.setScene(createMainMenuScene()));
        
        buttonBox.getChildren().addAll(searchBtn, backBtn);
        
        root.getChildren().addAll(title, form, resultArea, buttonBox);
        
        return new Scene(root, 800, 600);
    }
    
    // Create edit student scene
    private Scene createEditStudentScene(Student student) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        Label title = new Label("Edit Student");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // Form fields
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        
        Label rollLabel = new Label("Roll Number:");
        TextField rollField = new TextField(String.valueOf(student.getRollNumber()));
        rollField.setEditable(false); // Roll number shouldn't be editable
        rollField.setStyle("-fx-background-color: #e8e8e8;");
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(student.getName());
        nameField.setPromptText("Enter student name");
        
        Label gradeLabel = new Label("Grade:");
        TextField gradeField = new TextField(student.getGrade());
        gradeField.setPromptText("Enter grade (e.g., A, B, C)");
        
        form.add(rollLabel, 0, 0);
        form.add(rollField, 1, 0);
        form.add(nameLabel, 0, 1);
        form.add(nameField, 1, 1);
        form.add(gradeLabel, 0, 2);
        form.add(gradeField, 1, 2);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button updateBtn = createStyledButton("Update Student", "#f39c12");
        Button backBtn = createStyledButton("Back", "#95a5a6");
        
        updateBtn.setOnAction(e -> {
            if (validateEditInput(nameField.getText(), gradeField.getText())) {
                String name = nameField.getText().trim();
                String grade = gradeField.getText().trim();
                
                Student updatedStudent = new Student(student.getRollNumber(), name, grade);
                if (studentManager.updateStudent(updatedStudent)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
                    saveDataToFile();
                    primaryStage.setScene(createViewStudentsScene());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student!");
                }
            }
        });
        
        backBtn.setOnAction(e -> primaryStage.setScene(createViewStudentsScene()));
        
        buttonBox.getChildren().addAll(updateBtn, backBtn);
        
        root.getChildren().addAll(title, form, buttonBox);
        
        return new Scene(root, 800, 600);
    }
    
    // Create student table
    private void createStudentTable() {
        studentTable = new TableView<>();
        studentTable.setItems(studentData);
        
        TableColumn<Student, Integer> rollCol = new TableColumn<>("Roll Number");
        rollCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        rollCol.setPrefWidth(120);
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Student, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeCol.setPrefWidth(100);
        
        studentTable.getColumns().addAll(rollCol, nameCol, gradeCol);
        studentTable.setPrefHeight(300);
    }
    
    // Refresh table data
    private void refreshTable() {
        studentData.clear();
        studentData.addAll(studentManager.getAllStudents());
    }
    
    // Perform search
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }
        
        studentData.clear();
        
        // Search by roll number if it's numeric
        try {
            int rollNumber = Integer.parseInt(searchText);
            var student = studentManager.searchStudent(rollNumber);
            if (student.isPresent()) {
                studentData.add(student.get());
            }
        } catch (NumberFormatException e) {
            // Search by name
            studentData.addAll(studentManager.searchStudentsByName(searchText));
        }
    }
    
    // Create styled button
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-font-size: 14px; " +
                       "-fx-background-radius: 5;");
        
        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-opacity: 0.8;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-opacity: 0.8;", "")));
        
        return button;
    }
    
    // Validate input
    private boolean validateInput(String rollNumber, String name, String grade) {
        if (rollNumber.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Roll number is required!");
            return false;
        }
        
        if (name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Name is required!");
            return false;
        }
        
        if (grade.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Grade is required!");
            return false;
        }
        
        try {
            Integer.parseInt(rollNumber.trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Roll number must be a valid number!");
            return false;
        }
        
        return true;
    }
    
    // Validate edit input
    private boolean validateEditInput(String name, String grade) {
        if (name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Name is required!");
            return false;
        }
        
        if (grade.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Grade is required!");
            return false;
        }
        
        return true;
    }
    
    // Show alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Load data from file
    private void loadDataFromFile() {
        var students = StudentStorage.loadFromFile();
        studentManager.setStudents(students);
        System.out.println("Loaded " + students.size() + " students from file.");
    }
    
    // Save data to file
    private void saveDataToFile() {
        boolean success = StudentStorage.saveToFile(studentManager.getAllStudents());
        if (success) {
            System.out.println("Data saved successfully.");
        } else {
            System.err.println("Failed to save data.");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 