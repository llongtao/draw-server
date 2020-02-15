package com.llt.im.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.llt.im.dao.ImageDataDao;
import com.llt.im.enums.GameStatus;
import com.llt.im.game.manager.DelayTaskManager;
import com.llt.im.game.manager.GameManager;
import com.llt.im.game.manager.KeywordManager;
import com.llt.im.game.service.RobotSocketServer;
import com.llt.im.model.entity.ImageData;
import com.llt.im.model.vo.GroupVO;
import com.llt.im.game.service.WebSocketServer;
import com.llt.im.utils.ApplicationContextUtils;
import com.llt.im.utils.IdGenerator;
import com.llt.im.utils.MessageUtills;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class Group {
    private static final Random random = new Random();
    private static final String ROBOT_NAME = "机器人";
    private static final ImageDataDao imageDataDao = ApplicationContextUtils.getBean(ImageDataDao.class);


    private static final String LOCATION_TYPE = "location";
    private static final String GO_TYPE = "go";
    private static final String BACK_TYPE = "back";

    private static final int MAX_NUM = 8;

    private String id;

    private final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>(8);

    private final List<String> userIdList = new ArrayList<>(8);

    private GameStatus gameStatus;

    private String statusCode;

    private String drawId;

    private String masterId;

    private KeyWord keyWord;

    private int peopleNum;

    private int round = 1;

    private int count = 0;

    private long startTime = 0;

    private long roundStartTime = 0;

    private int drawIndex = 0;

    private boolean showDesc;

    private boolean hasRobot;

    @JsonIgnore
    private Iterator<KeyWord> robotGuess;


    public static Group create(WebSocketServer webSocketServer) {
        Group group = new Group();
        group.id = IdGenerator.getId();
        group.gameStatus = GameStatus.CREATED;
        group.statusCode = GameStatus.CREATED.getCode();
        group.masterId = webSocketServer.getUserId();
        group.add(webSocketServer);
        return group;
    }

    public synchronized void add(WebSocketServer webSocketServer) {
        if (peopleNum == MAX_NUM) {
            throw new RuntimeException("房间已满");
        }
        if (hasRobot) {
            webSocketMap.remove(ROBOT_NAME);
            userIdList.remove(ROBOT_NAME);
            hasRobot = false;
            sendAll(MessageUtills.message(new Message("法官", "机器人退出了游戏")));
        }
        webSocketMap.put(webSocketServer.getUserId(), webSocketServer);
        userIdList.add(webSocketServer.getUserId());
        peopleNum = userIdList.size();
        sendAll(MessageUtills.changeGroup(new GroupVO(this)));
        sendAll(MessageUtills.message(new Message("法官", webSocketServer.getUserId() + "加入了游戏")));
    }

    public synchronized void remove(WebSocketServer webSocketServer) {

        String userId = webSocketServer.getUserId();
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(webSocketServer.getUserId());
            userIdList.remove(webSocketServer.getUserId());
            peopleNum = userIdList.size();
            if (!isEmpty()) {
                sendAll(MessageUtills.message(new Message("法官", "玩家:" + userId + " 断开连接")));
                //作画者退出，进行下一轮
                if (Objects.equals(drawId, userId)) {
                    incrDrawer();
                    sendAll(MessageUtills.message(new Message("法官", "作画者:" + userId + " 断开连接,接下来由:" + drawId + "作画")));
                    setStatus(GameStatus.WAIT_SELECT);
                }
                //房主退出，更换房主
                if (Objects.equals(masterId, userId)) {
                    reSetMaster();
                }
                if (peopleNum == 1) {
                    setStatus(GameStatus.CREATED);
                }

                sendAll(MessageUtills.changeGroup(new GroupVO(this)));
            }
        }

    }

    private void reSetMaster() {
        if (!isEmpty()) {
            String userId = masterId;
            masterId = userIdList.get(0);
            sendAll(MessageUtills.message(new Message("法官", "房主:" + userId + " 断开连接,接下来由:" + masterId + " 担任房主")));
        }
    }

    private String incrDrawer() {
        drawIndex++;
        if (drawIndex > userIdList.size() - 1) {
            drawIndex = 0;
            round++;
            sendAll(MessageUtills.message(new Message("法官", "第 " + round + " 回合")));
        }
        this.drawId = getDrawId();
        return drawId;
    }

    private void setStatus(GameStatus status) {
        if (status != this.gameStatus) {
            gameStatus = status;
            statusCode = status.getCode();
            sendAll(MessageUtills.status(status));
        }
    }

    public void setKeyWord(KeyWord keyWord) {
        this.roundStartTime = System.currentTimeMillis();
        this.keyWord = keyWord;
        this.setStatus(GameStatus.PLAYING);
        DelayTaskManager.execute(DelayTaskManager.Type.stop, count, this);
        DelayTaskManager.execute(DelayTaskManager.Type.remind, count, this);
        //有机器人并且没在作画
        if (hasRobot && !Objects.equals(getDrawId(), ROBOT_NAME)) {
            List<KeyWord> keyWords = KeywordManager.getRobotGuessKeyWords(keyWord);
            this.robotGuess = keyWords.iterator();
            DelayTaskManager.execute(DelayTaskManager.Type.robotAnswer, count, this,15000);
        }
        sendAll(MessageUtills.changeGroup(new GroupVO(this)));
        sendAll(MessageUtills.clear());
        sendAll(MessageUtills.message(new Message("法官", "第 " + count + " 回合")));
        sendAll(MessageUtills.message(new Message("法官", "提示！！ " + keyWord.getLen() + "个字")));
    }


    public void sendAll(String msg) {
        String type = MessageUtills.getType(msg);
        if ((LOCATION_TYPE.equals(type) || GO_TYPE.equals(type) || BACK_TYPE.equals(type)) && this.gameStatus == GameStatus.PLAYING) {
            webSocketMap.forEach((k, v) -> v.sendMessage(msg));
        } else {
            webSocketMap.forEach((k, v) -> v.sendMessage(msg));
        }

    }

    public void start() {
        hasRobot = peopleNum <= 1;
        if (hasRobot) {
            userIdList.add(ROBOT_NAME);
            webSocketMap.put(ROBOT_NAME, new RobotSocketServer());
            hasRobot = true;
            drawIndex = 1;
            DelayTaskManager.execute(DelayTaskManager.Type.robotSelect, count, this);
        } else {
            drawIndex = 0;
        }
        setStatus(GameStatus.WAIT_SELECT);
        peopleNum = userIdList.size();
        drawId = userIdList.get(drawIndex);
        sendAll(MessageUtills.changeGroup(new GroupVO(this)));
        sendAll(MessageUtills.message(new Message("法官", "游戏开始")));
        if (hasRobot) {
            sendAll(MessageUtills.message(new Message("法官", "机器人加入游戏，单人模式下会有机器人陪玩")));
        }

    }

    public boolean guess(String key, String userId) {
        sendAll(MessageUtills.message(new Message(userId, key)));
        if (this.keyWord == null) {
            return false;
        }
        if (Objects.equals(this.keyWord.getName(), key)) {
            sendAll(MessageUtills.show(userId));
            next(MessageUtills.message(new Message("法官", "答案是:" + keyWord.getName() + ",恭喜 " + userId + " 猜对了")));

            return true;
        }
        return false;
    }

    public void next(String message) {
        this.roundStartTime = System.currentTimeMillis();
        String drawer = incrDrawer();
        count++;
        setStatus(GameStatus.WAIT_SELECT);
        sendAll(message);
        sendAll(MessageUtills.changeGroup(new GroupVO(this)));
        if (ROBOT_NAME.equals(drawer)) {
            DelayTaskManager.execute(DelayTaskManager.Type.robotSelect, count, this);
        }
        this.keyWord = null;

    }

    public void robotDraw() {
        ImageData imageData = imageDataDao.queryRandom();
        setKeyWord(new KeyWord(imageData.getKeyword(), imageData.getKeywordDesc()));
        sendAll(MessageUtills.img(imageData.getImg()));
    }

    public void robotAnswer() {
        if (hasRobot && !Objects.equals(getDrawId(), ROBOT_NAME)) {
            KeyWord key= keyWord;
            if (robotGuess.hasNext()) {
                key = robotGuess.next();
            }
            if (key == null) {
                key = keyWord;
            }
            GameManager.guess(id, ROBOT_NAME, key.getName());
        }
    }

    private boolean randomWin() {
        return random.nextInt(100) > 94;
    }

    public boolean isEmpty() {
        if (hasRobot && peopleNum == 1) {
            return true;
        } else {
            return peopleNum == 0;
        }

    }

    public String getDrawId() {
        if (userIdList.size() < drawIndex + 1) {
            if (userIdList.isEmpty()) {
                return null;
            } else {
                return userIdList.get(0);
            }
        } else {
            return userIdList.get(drawIndex);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        Group group = (Group) o;
        return getId().equals(group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
