package com.tfi.econexo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    private boolean isActive = true;

}
