package unieventhub.util;

import unieventhub.dao.EventDAO;
import unieventhub.dao.RegistrationDAO;
import unieventhub.model.Event;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ReminderScheduler {
    private final Queue<Event> reminderQueue = new LinkedList<>();
    private final Set<Integer> sentReminders = new HashSet<>(); // to track already reminded events
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    public void start() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkForReminders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 1000); // check every minute
    }

    private void checkForReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> upcomingEvents = eventDAO.getAllEvents();

        for (Event event : upcomingEvents) {
            int eventId = event.getId();

            if (sentReminders.contains(eventId)) continue;

            LocalDateTime eventDateTime = LocalDateTime.of(event.getDate(), event.getStartTime());
            Duration timeUntilEvent = Duration.between(now, eventDateTime);

            // Check if it's approximately 24 hours before event (within 1 minute tolerance)
            if (timeUntilEvent.toMinutes() >= 1439 && timeUntilEvent.toMinutes() <= 1441) {
                reminderQueue.offer(event);
                System.out.println("Queued reminder for event: " + event.getName());
            }
        }

        // Send reminders from queue
        while (!reminderQueue.isEmpty()) {
            Event event = reminderQueue.poll();
            int eventId = event.getId();

            List<String> emails = registrationDAO.getRegisteredStudentEmailsForEvent(eventId);

            if (emails != null && !emails.isEmpty()) {
                String subject = "Reminder: " + event.getName() + " is tomorrow!";
                String content = """
                    Dear Student,

                    Just a reminder that you are registered for the following event:

                    Event: %s
                    Date: %s
                    Time: %s to %s

                    We look forward to seeing you there!

                    Regards,
                    UniEventHub Team
                """.formatted(
                        event.getName(),
                        event.getDate(),
                        event.getStartTime(),
                        event.getEndTime()
                );

                for (String email : emails) {
                    EmailSender.sendEmail(email, subject, content);
                    System.out.println("Reminder sent to " + email + " for event: " + event.getName());
                }

                // Mark as sent
                sentReminders.add(eventId);
            }
        }
    }
}
