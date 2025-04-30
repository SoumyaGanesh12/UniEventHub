package unieventhub.dao;

import java.util.List;
import unieventhub.model.Building;

/**
 * Interface: BuildingAction
 *
 * This interface defines actions related to the Building entity.
 * It is implemented by the BuildingDAO class, promoting abstraction
 * and separation of concerns.
 */
public interface BuildingAction {

    /**
     * Fetches all buildings from the database.
     *
     * @return List of Building objects
     */
    List<Building> getAllBuildings();

    /**
     * Returns the name of the building given its ID.
     *
     * @param id The building's unique identifier
     * @return Building name as String
     */
    String getBuildingNameById(int id);
}
