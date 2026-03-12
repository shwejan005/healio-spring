package com.healio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer maxUsers;

    @Column(nullable = false)
    private Integer currentUsers = 0;

    @Column(columnDefinition = "TEXT")
    private String activeUsers; // stored as JSON array string

    public Room() {
        this.activeUsers = "[]";
    }

    public Room(String name, Integer maxUsers) {
        this.name = name;
        this.maxUsers = maxUsers;
        this.currentUsers = 0;
        this.activeUsers = "[]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(Integer currentUsers) {
        this.currentUsers = currentUsers;
    }

    public String getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(String activeUsers) {
        this.activeUsers = activeUsers;
    }
}
