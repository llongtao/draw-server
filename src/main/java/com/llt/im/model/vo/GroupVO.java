package com.llt.im.model.vo;

import com.llt.im.game.model.Group;
import com.llt.im.model.resp.BaseResult;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author llt11
 */
@Data
public class GroupVO extends BaseResult {

    private Group group;

    private List<Member> memberList;

    @Data
    public static class Member{
        private String name;
        private Boolean isMaster;
    }

    public GroupVO(Group group) {
        this.group = group;
        String masterId = group.getMasterId();
        List<GroupVO.Member> memberList = group.getUserIdList().stream().map(item -> {
            GroupVO.Member member = new GroupVO.Member();
            member.setName(item);
            member.setIsMaster(Objects.equals(masterId, item));
            return member;
        }).collect(Collectors.toList());
        this.setMemberList(memberList);
    }
}
