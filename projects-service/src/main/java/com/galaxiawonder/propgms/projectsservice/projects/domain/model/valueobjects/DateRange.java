package com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects;

import java.util.Date;
import jakarta.persistence.Embeddable;

@Embeddable
public record DateRange(
        Date startDate,
        Date endDate
) {

    public DateRange() {
        this(null, null);
    }

    public DateRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and endDate date cannot be null");
        }

        if(startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be set after endDate date");
        }
    }
}