package com.example.myexpensetracker.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", schema = "public")
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(length = 255, unique = true, nullable = false)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private ProfileEntity profile;

}
