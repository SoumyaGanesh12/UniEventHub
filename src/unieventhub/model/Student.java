package unieventhub.model;

public class Student extends User {
    private int userId;  // FK to users table
    private String neuId;
    private String college;
    private String course;
    private String contactNumber;

    public Student(String firstName, String lastName, String email, String username, String password, String role,
            String neuId, String college, String course, String contactNumber) {
		 super(firstName, lastName, email, username, password, role); // this calls User constructor
		 this.neuId = neuId;
		 this.college = college;
		 this.course = course;
		 this.contactNumber = contactNumber;
	}
    
    public Student() {
        super(); // calls User() — so this requires that User has a no-arg constructor!
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNeuid() { return neuId; }
    public void setNeuid(String neuid) { this.neuId = neuid; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
