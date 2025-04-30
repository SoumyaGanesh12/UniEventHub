package unieventhub.model;

public class Registration {
    private int id;
    private int eventId;
    private int studentId;
    //private String studentUsername;
    private String studentFirstName;
    private String studentLastName;
    private String studentEmail;

    public Registration() {}

    public Registration(int id, int eventId, int studentId, String studentUsername, String studentEmail) {
        this.id = id;
        this.eventId = eventId;
        this.studentId = studentId;
        //this.studentUsername = studentUsername;
        this.studentEmail = studentEmail;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

//    public String getStudentUsername() {
//        return studentUsername;
//    }
//
//    public void setStudentUsername(String studentUsername) {
//        this.studentUsername = studentUsername;
//    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }
    
}
