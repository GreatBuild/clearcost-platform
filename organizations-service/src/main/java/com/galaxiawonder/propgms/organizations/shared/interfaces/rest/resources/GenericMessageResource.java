// El paquete se ha actualizado a la estructura de nuestro microservicio
package com.galaxiawonder.propgms.organizations.shared.interfaces.rest.resources;

/**
 * Recurso para devolver mensajes genéricos en las respuestas de la API.
 * @param message El mensaje a devolver.
 */
public record GenericMessageResource(
        String message
) {
}