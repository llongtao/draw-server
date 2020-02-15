package com.llt.im.game.service;

public class RobotSocketServer extends WebSocketServer {
    @Override
    public void sendMessage(String message) {

    }

    @Override
    public String getUserId() {
        return "机器人";
    }
}
