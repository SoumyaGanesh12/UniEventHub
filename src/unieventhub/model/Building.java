package unieventhub.model;

import java.util.List;

public class Building {
    private int id;
    private String name;
    private int maxRoomsAllowed;
    private List<Room> rooms; // optional: only if you fetch and hold rooms inside a Building object

    public Building() {} // Default constructor

    public Building(int id, String name, int maxRoomsAllowed) {
        this.id = id;
        this.name = name;
        this.maxRoomsAllowed = maxRoomsAllowed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxRoomsAllowed() { return maxRoomsAllowed; }
    public void setMaxRoomsAllowed(int maxRoomsAllowed) { this.maxRoomsAllowed = maxRoomsAllowed; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}
