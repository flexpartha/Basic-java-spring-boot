package com.example.userapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
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

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
}
