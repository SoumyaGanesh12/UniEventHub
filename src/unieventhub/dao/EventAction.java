package unieventhub.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import unieventhub.model.Event;

/**
 * Interface: EventAction
 *
 * This interface defines all actions that can be performed on the Event model.
 * It is implemented by EventDAO, helping us apply Abstraction and Polymorphism
 * 
 */
public interface EventAction {

    /**
     * Creates a new event in the database.
     *
     * @param event The Event object containing all necessary fields
     * @return true if the event was created successfully, false otherwise
     */
    boolean createEvent(Event event);

    /**
     * Updates an existing event with new data.
     *
     * @param event The Event object containing updated details
     * @return true if update was successful, false otherwise
     */
    boolean updateEvent(Event event);

    /**
     * Deletes an event by its ID.
     *
     * @param eventId The unique ID of the event
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteEventById(int eventId);

    /**
     * Checks if the given room is available at the specified time slot.
     * Used during **Event Creation**.
     *
     * @param roomId ID of the room to check
     * @param date Date of the event
     * @param startTime Starting time of the event
     * @param endTime Ending time of the event
     * @return true if room is available, false otherwise
     */
    boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * Checks if the room is available for update operations,
     * excluding the event being updated (to avoid conflict with itself).
     *
     * @param roomId ID of the room
     * @param date Date of the event
     * @param startTime Starting time of the event
     * @param endTime Ending time of the event
     * @param excludeEventId ID of the event to exclude from conflict check
     * @return true if room is available, false otherwise
     */
    boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer excludeEventId);

    /**
     * Gets all events created by a specific organizer.
     *
     * @param organizerUsername The username of the organizer
     * @return List of Event objects
     */
    List<Event> getEventsByOrganizer(String organizerUsername);
}
