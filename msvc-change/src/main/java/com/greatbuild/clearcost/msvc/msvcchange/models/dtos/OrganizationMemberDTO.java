package com.greatbuild.clearcost.msvc.msvcchange.models.dtos;

import java.time.LocalDate;

/**
 * DTO para recibir datos de miembro de organización desde msvc-organizations
 */
public class OrganizationMemberDTO {

    private Long memberId;
    private Long userId;
    private String role;  // CONTRACTOR o MEMBER
    private String email;
    private String firstName;
    private String lastName;

    // Constructors
    public OrganizationMemberDTO() {
    }

    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
