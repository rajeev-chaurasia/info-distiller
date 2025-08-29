package com.infodistiller.gateway.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "query_template", columnDefinition = "TEXT")
    private String queryTemplate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allowlist_json", columnDefinition = "jsonb")
    private String allowlistJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "denylist_json", columnDefinition = "jsonb")
    private String denylistJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueryTemplate() {
        return queryTemplate;
    }

    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

    public String getAllowlistJson() {
        return allowlistJson;
    }

    public void setAllowlistJson(String allowlistJson) {
        this.allowlistJson = allowlistJson;
    }

    public String getDenylistJson() {
        return denylistJson;
    }

    public void setDenylistJson(String denylistJson) {
        this.denylistJson = denylistJson;
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