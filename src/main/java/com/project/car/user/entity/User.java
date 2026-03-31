package com.project.car.user.entity;

import com.project.car.common.entity.BaseEntity;
import com.project.car.common.enums.AuthProvider;
import com.project.car.common.enums.SystemRole;
import com.project.car.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 50)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", nullable = false, length = 50)
    private SystemRole systemRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status;

}
