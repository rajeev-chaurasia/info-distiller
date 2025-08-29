package com.infodistiller.gateway.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "selections")
public class Selection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "article_url", length = 1024, nullable = false)
    private String articleUrl;

    @Column(name = "article_title", length = 1024)
    private String articleTitle;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "picked_for_date", nullable = false)
    private LocalDate pickedForDate;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public LocalDate getPickedForDate() { return pickedForDate; }
    public void setPickedForDate(LocalDate pickedForDate) { this.pickedForDate = pickedForDate; }
}