package com.llt.im.game.manager;

import com.llt.im.enums.GameStatus;
import com.llt.im.game.model.Group;
import com.llt.im.game.model.KeyWord;
import com.llt.im.game.service.WebSocketServer;
import com.llt.im.utils.MessageUtills;
import com.llt.im.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author llt11
 */
@Slf4j
public class GameManager {

    private static List<String> sysUserList = Arrays.asList("法官", "机器人");

    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Group> groupMap = new ConcurrentHashMap<>();

    private static List<Group> groupList = new ArrayList<>();

    public static boolean addUser(WebSocketServer webSocketServer) {
        if (sysUserList.contains(webSocketServer.getUserId())) {
            webSocketServer.sendMessage(MessageUtills.error("系统名称禁止使用"));
            return false;
        } else if (webSocketMap.containsKey(webSocketServer.getUserId())) {
            log.info("用户已存在:" + webSocketServer.getUserId() + ",当前在线人数为:" + webSocketMap.size());
            webSocketServer.sendMessage(MessageUtills.error("用户已存在"));
            return false;
        } else {
            webSocketMap.put(webSocketServer.getUserId(), webSocketServer);
            log.info("用户连接:" + webSocketServer.getUserId() + ",当前在线人数为:" + webSocketMap.size());
            webSocketServer.sendMessage(MessageUtills.status(GameStatus.LOGIN));
            return true;
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendAll(String message) {
        webSocketMap.forEach((s, webSocketServer) -> webSocketServer.sendMessage(message));
    }

    public static void sendGroup(String userId, String message) {
        Group group = GameManager.getGroupByUserId(userId);
        group.sendAll(message);
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendTo(String userId, String message) {
        WebSocketServer webSocketServer1 = webSocketMap.get(userId);
        if (webSocketServer1 != null) {
            webSocketServer1.sendMessage(message);
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static Group createGroup(String userId) {
        WebSocketServer webSocketServer1 = webSocketMap.get(userId);
        if (webSocketServer1 == null) {
            throw new RuntimeException("未连接");
        }
        Group group = Group.create(webSocketServer1);
        groupList.add(group);
        groupMap.put(userId, group);
        sendAll(MessageUtills.groupListChange());
        return group;
    }

    /**
     * 实现服务器主动推送
     */
    public static Group joinGroup(String userId, String groupId) {
        WebSocketServer webSocketServer1 = webSocketMap.get(userId);
        if (webSocketServer1 == null) {
            throw new RuntimeException("未连接");
        }
        List<Group> groupList = getGroupList(groupId);
        if (!groupList.isEmpty()) {
            Group group = groupList.get(0);
            group.add(webSocketServer1);
            groupMap.put(userId, group);
            return group;
        }
        return null;
    }

    public static List<Group> getGroupList(String groupId) {
        if (StringUtils.isNotBlank(groupId)) {
            return groupList.stream().filter((item) -> item.getId().equals(groupId)).collect(Collectors.toList());
        }
        return groupList;
    }

    public static Group getGroupByUserId(String userId) {
        return groupMap.get(userId);
    }


    public static void startGame(String groupId) {
        getGroupList(groupId).forEach(Group::start);
    }

    public static Group selectKeyWord(String groupId, String word) {
        KeyWord keyWord = KeywordManager.getKeyWord(word);
        getGroupList(groupId).forEach(item -> {
            item.setKeyWord(keyWord);
        });
        if (!groupList.isEmpty()) {
            return groupList.get(0);
        }
        return null;
    }

    public static void guess(String groupId, String userId, String message) {
        getGroupList(groupId).forEach(item -> {
            item.guess(message, userId);
        });
    }


    public static void remove(WebSocketServer webSocketServer) {
        exitGroup(webSocketServer.getUserId());
        webSocketMap.remove(webSocketServer.getUserId());
        log.info("用户退出:" + webSocketServer.getUserId() + ",当前在线人数为:" + webSocketMap.size());
    }

    public synchronized static void exitGroup(String userId) {
        Group group = getGroupByUserId(userId);
        WebSocketServer webSocketServer = webSocketMap.get(userId);
        if (group != null) {
            if (webSocketServer != null) {
                group.remove(webSocketServer);
            }
            if (group.isEmpty()) {
                groupList.remove(group);
                groupMap.remove(userId);
            }
        }
        sendAll(MessageUtills.groupListChange());
    }
}
