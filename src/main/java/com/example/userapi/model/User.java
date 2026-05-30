package com.example.userapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username", "email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String username;
    @Column(nullable = false)
    private String email;
    private String phone;
    private String website;

    @Embedded
    private Address address;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "company_name")),
        @AttributeOverride(name = "catchPhrase", column = @Column(name = "catch_phrase")),
        @AttributeOverride(name = "bs", column = @Column(name = "bs"))
    })
    private Company company;
}
