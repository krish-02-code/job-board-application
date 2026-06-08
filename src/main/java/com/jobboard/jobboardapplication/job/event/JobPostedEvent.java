package com.jobboard.jobboardapplication.job.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostedEvent {
    // needed by notification consumer to build a link
    // "New job posted → click here → /api/jobs/42"
    // without jobId, candidate can't navigate to the job
    private Long id;

    //  needed for the notification message itself New job posted: Java Developer at Google
    // without title, notification is just "A job was posted" — useless
    private String title;

    private String requiredSkills;  // required for the comparison with the resume after the event is published

    //  needed for notification message
    // "New Java job in Pune" vs "New Java job in Bangalore"
    // candidate decides if relevant based on location
    private String location;
}
