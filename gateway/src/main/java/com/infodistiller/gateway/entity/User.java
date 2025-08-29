package com.infodistiller.gateway.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "time_zone")
    private String timeZone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "prefs_json", columnDefinition = "jsonb")
    private String prefsJson;

    @Column(name = "rss_private_token", unique = true, nullable = false)
    private String rssPrivateToken;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private OffsetDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getPrefsJson() {
        return prefsJson;
    }

    public void setPrefsJson(String prefsJson) {
        this.prefsJson = prefsJson;
    }

    public String getRssPrivateToken() {
        return rssPrivateToken;
    }

    public void setRssPrivateToken(String rssPrivateToken) {
        this.rssPrivateToken = rssPrivateToken;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}