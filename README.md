# 📅 UniEventHub

**UniEventHub** is a full-stack campus event management platform built using **JavaFX** and **MySQL**, designed to centralize student-led events, streamline administrator control, and enhance campus engagement through a responsive, analytics-driven system.

---

## 🚀 Features

- 🔑 **Role-Based Access**

  ### 1. Admin
  - Manages building and room details
  - Views charts and visualizations
  - Oversees all events and event details

  ### 2. Organizers
  - Register and manage their events
  - Track student registrations
  - Increase seat limits (with room conflict checks)

  ### 3. Students
  - Search and register for events using filters:
    - Date and Time  
    - Event Name  
    - Registration Type (on-spot / pre-registration through UI)  
    - Price (paid / unpaid)  
    - Duration  
    - Category (technical / cultural)  
  - Unique registration enforced per email ID

- 📊 **Dashboards**
  - **Admin Dashboard**: Top 5 registered events, number of events per organizer, event category trends
  - **Organizer Dashboard**: Registrations for each event, categories of events hosted
  - **Student Dashboard**: Categories of events registered by the student

- 🧠 **Algorithms Implemented**
  - **Luhn’s Algorithm** – Credit card validation for paid events
  - **Interval Overlap Check** – Time conflict detection
  - **Seat Limit Check** – Validation before confirming registrations
  - **Event Sorting** – Based on date, popularity, and category

- 🎨 **UI/UX**
  - Built with JavaFX using **SceneBuilder** for FXML layout design
  - Styled using CSS and dynamic elements with observable lists and listeners

- 📧 **Email Notifications**
  - Event reminders and confirmations sent via **Jakarta Mail API**

---

## 🛠️ Tech Stack

| Layer        | Technology                             |
|--------------|------------------------------------------|
| **Frontend** | JavaFX (FXML, CSS, SceneBuilder)         |
| **Backend**  | Java 17+, JDBC, DAO Pattern, MVC         |
| **Database** | MySQL                                    |
| **Mailing**  | Jakarta Mail API                         |

---

## 🗃️ Project Structure

```
UniEventHub/
├── src/                      # Java source code
├── lib/                      # External JARs (JavaFX, Mail, MySQL)
├── instruction/              # Setup instructions (text file)
├── .classpath, .project      # Eclipse config files
├── build.fxbuild             # JavaFX build file
├── .gitignore
└── README.md
```

---

## 🧩 MySQL Database Setup

1. Install and run MySQL Server.
2. Create a database (e.g., `unieventhub`).
3. Add required tables and relationships according to your schema design.
4. Update the database URL, username, and password in the `db.properties` configuration file.

---

## 📦 Setup Instructions

> Prerequisites: Java 17+, JavaFX SDK, MySQL, SceneBuilder (optional), IntelliJ or Eclipse

1. Clone the repository.

2. Open the project in your IDE.

3. Add external libraries (from `lib/`) to the classpath:
   - JavaFX SDK JARs
   - MySQL Connector JAR
   - Jakarta Mail JAR

4. Set VM options if needed for JavaFX:
   ```bash
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

5. Run the application via the main launcher class.

---

## ✅ Conclusion

UniEventHub demonstrates the integration of frontend, backend, and database technologies to solve a real-world problem within a university setting. This project showcases strong full-stack development skills and practical implementation of data validation, scheduling logic, and user role management.
