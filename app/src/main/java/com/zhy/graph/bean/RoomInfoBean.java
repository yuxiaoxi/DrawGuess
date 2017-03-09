package com.zhy.graph.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuzhuo on 2017/2/26.
 */
public class RoomInfoBean implements Serializable{
    private String roomId;
    private String type;
    private String stage;
    private String roomOwnerName;
    private String nowUserNum;
    private String questionNum;
    private List<PlayerBean> addedUserList;
    private List<QuestionInfo> questions;
    private QuestionInfo currentQuestion;
    private String version;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getRoomOwnerName() {
        return roomOwnerName;
    }

    public void setRoomOwnerName(String roomOwnerName) {
        this.roomOwnerName = roomOwnerName;
    }

    public String getNowUserNum() {
        return nowUserNum;
    }

    public void setNowUserNum(String nowUserNum) {
        this.nowUserNum = nowUserNum;
    }

    public String getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public List<PlayerBean> getAddedUserList() {
        return addedUserList;
    }

    public void setAddedUserList(List<PlayerBean> addedUserList) {
        this.addedUserList = addedUserList;
    }

    public List<QuestionInfo> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionInfo> questions) {
        this.questions = questions;
    }

    public QuestionInfo getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(QuestionInfo currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
