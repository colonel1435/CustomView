package com.zero.customview.view.danmaku;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/15 0015 9:21
 */

public class DanmakuMsg {
    private int userId;
    private int userType;
    private int msgType;
    private int iconId;
    private String iconPath;
    private String msg;

    public DanmakuMsg() {
    }

    public DanmakuMsg(int userId, int userType, int msgType, int iconId, String msg) {
        this.userId = userId;
        this.userType = userType;
        this.msgType = msgType;
        this.iconId = iconId;
        this.msg = msg;
    }

    public DanmakuMsg(int userId, int userType, int msgType, String iconPath, String msg) {
        this.userId = userId;
        this.userType = userType;
        this.msgType = msgType;
        this.iconPath = iconPath;
        this.msg = msg;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "DanmakuMsg{" +
                "userId=" + userId +
                ", userType=" + userType +
                ", msgType=" + msgType +
                ", iconId=" + iconId +
                ", iconPath='" + iconPath + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
