package com.llt.im.controller;

import com.llt.im.dao.ImageDataDao;
import com.llt.im.game.manager.GameManager;
import com.llt.im.game.manager.KeywordManager;
import com.llt.im.game.model.Group;
import com.llt.im.game.model.KeyWord;
import com.llt.im.model.entity.ImageData;
import com.llt.im.model.req.GameSaveDataReq;
import com.llt.im.model.req.GameSendReq;
import com.llt.im.model.resp.BaseResult;
import com.llt.im.model.resp.CreateGroupResp;
import com.llt.im.model.resp.GameJoinResp;
import com.llt.im.model.vo.GroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author llt11
 */
@Slf4j
@RestController
public class GameController {

    @Resource
    private ImageDataDao imageDataDao;

    @GetMapping("/group/list")
    public List<Group> groupList(String id) {
        return GameManager.getGroupList(id);
    }

    @GetMapping("/group/{id}")
    public GroupVO group(@PathVariable String id) {
        List<Group> groupList = GameManager.getGroupList(id);
        if (groupList.isEmpty()) {
            throw new RuntimeException("房间不存在");
        }
        return new GroupVO(groupList.get(0));
    }

    @PostMapping("/group/create")
    public CreateGroupResp createGroup(String id) {
        Group group = GameManager.createGroup(id);
        CreateGroupResp createGroupResp = new CreateGroupResp();
        createGroupResp.setId(group.getId());
        return createGroupResp;
    }

    @PostMapping("/group/join")
    public GameJoinResp createGroup(String userId, String groupId) {
        Group group = GameManager.joinGroup(userId, groupId);
        if (group == null) {
            throw new RuntimeException("房间不存在");
        }
        GameJoinResp gameJoinResp = new GameJoinResp();
        gameJoinResp.setGroup(group);
        return gameJoinResp;
    }

    @GetMapping("/group/exit/{userId}")
    public BaseResult exitGroup(@PathVariable String userId) {
        GameManager.exitGroup(userId);
        return BaseResult.SUCCESS;
    }

    @GetMapping("/game/start/{groupId}")
    public BaseResult startGame(@PathVariable String groupId) {
        GameManager.startGame(groupId);
        return BaseResult.SUCCESS;
    }

    @GetMapping("/game/wordList")
    public List<KeyWord> wordList() {
        return KeywordManager.getKeyWords(6);
    }

    @GetMapping("/game/selectWord/{groupId}/{word}")
    public GameJoinResp selectWord(@PathVariable String groupId,@PathVariable String word) {
        Group group = GameManager.selectKeyWord(groupId, word);
        GameJoinResp gameJoinResp = new GameJoinResp();
        gameJoinResp.setGroup(group);
        return gameJoinResp;
    }

    @PostMapping("/game/send")
    public BaseResult send(@RequestBody GameSendReq gameSendReq) {
        GameManager.guess(gameSendReq.getGroupId(),gameSendReq.getUserId(),gameSendReq.getMessage());
        return BaseResult.SUCCESS;
    }

    @PostMapping("/game/saveData")
    public BaseResult saveData(@RequestBody GameSaveDataReq gameSaveDataReq) {
        ImageData imageData = new ImageData();
        BeanUtils.copyProperties(gameSaveDataReq,imageData);
        imageDataDao.saveAndFlush(imageData);

        return BaseResult.SUCCESS;
    }

//    @PostMapping("/game/getWeChatAuth")
//    public WeChatAuth getWeChatAuth(@RequestBody JSONObject jsonObject) {
//        log.info("getWeChatAuth:"+jsonObject.getString("url"));
//        return WeChatUtils.getWeChatAuth(jsonObject.getString("url"));
//    }






}
