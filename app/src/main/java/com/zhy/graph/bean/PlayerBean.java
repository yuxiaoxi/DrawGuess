package com.zhy.graph.bean;

import java.io.Serializable;

/**
 * Created by yuzhuo on 2017/2/26.
 */
public class PlayerBean implements Serializable{
    private String id;
    private String username;
    private String nickname;
    private String image;
    private String status;
    private String enabled;
    private String currentScore;
    private String ansser;
    private boolean drawNow;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }

    public String getAnsser() {
        return ansser;
    }

    public void setAnsser(String ansser) {
        this.ansser = ansser;
    }

    public boolean isDrawNow() {
        return drawNow;
    }

    public void setDrawNow(boolean drawNow) {
        this.drawNow = drawNow;
    }
}
