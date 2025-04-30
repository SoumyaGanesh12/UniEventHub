package unieventhub.model;

import java.time.LocalDate;
import java.time.LocalTime;
import unieventhub.util.EventCategory;

public class Event {
    public static final String Category = null;
	private int id;
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private EventCategory category;
    private int roomId;
    private double price;
    private String registrationType;
    private String foodAvailability;
    private int seatLimit;
    private String description;
    private int organizerId; 
    private String organizerClub;
    private String organizerUserName; // store username
    private String roomName;     // For display only
    private String buildingName; // For display only
    private int registrationCount;
    
    public Event() {
        // Required for TableView and FXMLLoader
    }

    public Event(String name, String description, LocalDate date, LocalTime startTime, LocalTime endTime,
                 EventCategory category, int organizerId, int roomId, double price,
                 String registrationType, String foodAvailability, int seatLimit) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.organizerId = organizerId;
        this.roomId = roomId;
        this.price = price;
        this.registrationType = registrationType;
        this.foodAvailability = foodAvailability;
        this.seatLimit = seatLimit;
    }
    
    // Overloaded constructor for reg.count
    public Event(String name, String description, LocalDate date, LocalTime startTime, LocalTime endTime,
            EventCategory category, int organizerId, int roomId, double price,
            String registrationType, String foodAvailability, int seatLimit,
            int registrationCount) {
		   this.name = name;
		   this.description = description;
		   this.date = date;
		   this.startTime = startTime;
		   this.endTime = endTime;
		   this.category = category;
		   this.organizerId = organizerId;
		   this.roomId = roomId;
		   this.price = price;
		   this.registrationType = registrationType;
		   this.foodAvailability = foodAvailability;
		   this.seatLimit = seatLimit;
		   this.registrationCount = registrationCount;
		}


    // Getters and Setters

    public Event(String name, String description,LocalDate date, LocalTime startTime, LocalTime endTime, EventCategory category,
            String organizerUserName, int roomId, double price, String registrationType,
            String foodAvailability, int seatLimit) {
   this.name = name;
   this.description = description;
   this.date = date;
   this.startTime = startTime;
   this.endTime = endTime;
   this.category = category;
   this.organizerUserName = organizerUserName;
   this.roomId = roomId;
   this.price = price;
   this.registrationType = registrationType;
   this.foodAvailability = foodAvailability;
   this.seatLimit = seatLimit;
}

	public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public EventCategory getCategory() { return category; }
    public void setCategory(EventCategory category) { this.category = category; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getRegistrationType() { return registrationType; }
    public void setRegistrationType(String registrationType) { this.registrationType = registrationType; }

    public String getFoodAvailability() { return foodAvailability; }
    public void setFoodAvailability(String foodAvailability) { this.foodAvailability = foodAvailability; }

    public int getSeatLimit() { return seatLimit; }
    public void setSeatLimit(int seatLimit) { this.seatLimit = seatLimit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
    
    public String getOrganizerClub() { return organizerClub; }
    public void setOrganizerClub(String organizerClub) { this.organizerClub = organizerClub; }
    
    public int getRegistrationCount() { return registrationCount; }
    public void setRegistrationCount(int regcount) { this.registrationCount = regcount; }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getOrganizer() { return organizerUserName; }
    public void setOrganizer(String organizerUserName) { this.organizerUserName = organizerUserName; }
    private boolean isRegistered;

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }

}
