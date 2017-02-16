package com.zhy.graph.bean;

/**
 * Created by yuzhuo on 2017/2/9.
 */
public class PlayerInfo {

    //玩家头像
    private String avater;
    //玩家昵称
    private String nickName;
    //是否准备
    private boolean ready;
    //是否游客
    private boolean youke;
    //是否本人
    private boolean me;

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isYouke() {
        return youke;
    }

    public void setYouke(boolean youke) {
        this.youke = youke;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }
}
