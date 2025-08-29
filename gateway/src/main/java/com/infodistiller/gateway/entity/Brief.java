package com.infodistiller.gateway.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "briefs")
public class Brief {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "brief_date", nullable = false)
    private LocalDate briefDate;

    @Column(name = "html_path", length = 1024)
    private String htmlPath;

    @Column(name = "mp3_path", length = 1024)
    private String mp3Path;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDate getBriefDate() { return briefDate; }
    public void setBriefDate(LocalDate briefDate) { this.briefDate = briefDate; }
    public String getHtmlPath() { return htmlPath; }
    public void setHtmlPath(String htmlPath) { this.htmlPath = htmlPath; }
    public String getMp3Path() { return mp3Path; }
    public void setMp3Path(String mp3Path) { this.mp3Path = mp3Path; }
}