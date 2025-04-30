package unieventhub.util;

import java.util.List;
import java.util.Map;

public class CollegeCourseMap {
    public static final Map<String, List<String>> collegeCourses = Map.of(
        "COE", List.of("Bioengineering", "Chemical Engineering", "Information Systems", "Software Engineering Systems", "Data Architecture and Management"),
        "CAMD", List.of("Extended Realities", "Experience Design", "Game Science and Design", "Information Design and Data Visualization", "Media Advocacy"),
        "CPS", List.of("Human Resources Management", "Nonprofit Management", "Project Management", "Regulatory Affairs", "Organizational Leadership")
    );
}
