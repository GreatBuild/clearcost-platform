package com.galaxiawonder.propgms.projectsservice.shared.interfaces.rest.resources;

/**
 * GenericMessageResource
 *
 * @summary
 * Represents a generic message response.
 * Used for simple API responses like "OK", "Deleted", "Updated".
 *
 * @param message The content of the message.
 */
public record GenericMessageResource(
        String message
) {
}