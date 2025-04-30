package unieventhub.dao;

import unieventhub.model.Room;
import java.util.List;

/**
 * Interface: RoomAction
 *
 * This interface defines actions related to Room data.
 * It is implemented by RoomDAO and used in controllers to maintain abstraction
 */
public interface RoomAction {

    /**
     * Gets a room's full details by its ID.
     *
     * @param roomId The unique identifier of the room
     * @return Room object if found, otherwise null
     */
    Room getRoomById(int roomId);

    /**
     * Gets the room's internal ID using its room number and the building it's in.
     * This is useful when a user selects building and room number from UI dropdowns.
     *
     * @param roomNumber The number shown on the room (like Room 101)
     * @param buildingId The ID of the building the room belongs to
     * @return The database room ID if found, else -1 or error
     */
    int getRoomIdByNumberAndBuilding(int roomNumber, int buildingId);

    /**
     * Returns a list of all rooms in a given building.
     * 
     * @param buildingId ID of the building
     * @return List of Room objects belonging to that building
     */
    List<Room> getRoomsByBuildingId(int buildingId);
}
