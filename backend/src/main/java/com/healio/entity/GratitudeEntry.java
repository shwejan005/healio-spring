package com.healio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gratitude_entries")
public class GratitudeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String gratitude;

    public GratitudeEntry() {
    }

    public GratitudeEntry(String userId, String date, String gratitude) {
        this.userId = userId;
        this.date = date;
        this.gratitude = gratitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGratitude() {
        return gratitude;
    }

    public void setGratitude(String gratitude) {
        this.gratitude = gratitude;
    }
}
