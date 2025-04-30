package unieventhub.model;

public class Organizer extends User {
    private int userId; // FK to users table
    private String club;
    private String contactNumber;

    // No-argument constructor (required for TableView, FXMLLoader, etc.)
    public Organizer() {
        super(); // calls User's no-arg constructor
    }
    
    // Constructor with all fields
    public Organizer(String firstName, String lastName, String email, String username, String password, String role,
                     String club, String contactNumber) {
        super(firstName, lastName, email, username, password, role); // call User constructor
        this.club = club;
        this.contactNumber = contactNumber;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
