package com.zhy.graph.bean;

import java.util.List;

/**
 * Created by yuzhuo on 2017/2/13.
 */
public class ReceiveService {

    //画板颜色
    private String color;

    //粗细程度
    private int lw;

    //画板数据
    private List<CoordinateBean> pts;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLw() {
        return lw;
    }

    public void setLw(int lw) {
        this.lw = lw;
    }

    public List<CoordinateBean> getPts() {
        return pts;
    }

    public void setPts(List<CoordinateBean> pts) {
        this.pts = pts;
    }
}
