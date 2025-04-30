package unieventhub.model;

public class Room {
    private int id;
    private int roomNumber;
    private int capacity;
    private int buildingId;

    public Room(int roomNumber, int capacity, int buildingId) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.buildingId = buildingId;
    }

    public Room(int id, int roomNumber, int capacity, int buildingId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.buildingId = buildingId;
    }

    public Room() {
		// TODO Auto-generated constructor stub
	}

	public int getId() { return id; }
    
    public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getCapacity() { return capacity; }
    public int getBuildingId() { return buildingId; }

    public void setId(int id) { this.id = id; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }
}
