package com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects;

import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.PersonName;
import com.galaxiawonder.propgms.projectsservice.shared.domain.model.valueobjects.EmailAddress;

/**
 * ProfileDetails
 *
 * @summary
 * Read model representing basic personal profile information.
 * This record is commonly used to expose user identity data such as name and email
 * across bounded contexts without exposing internal domain structures.
 *
 * @param name the person's full name, including first and last name.
 * @param email the person's email address
 *
 * @author
 * Galaxia Wonder Development Team
 * @since 1.0
 */
public record ProfileDetails(
        PersonName name,
        EmailAddress email
) {}