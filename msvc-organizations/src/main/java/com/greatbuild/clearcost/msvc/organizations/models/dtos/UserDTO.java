package com.greatbuild.clearcost.msvc.organizations.models.dtos;

// Este DTO representa la respuesta que esperamos de msvc-users
// No tiene @Entity, no tiene @Id. Es solo un contenedor de datos.
public class UserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;

    // ¡Importante! Jackson (la librería de JSON) necesita
    // un constructor vacío y getters/setters para funcionar.
    public UserDTO() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
