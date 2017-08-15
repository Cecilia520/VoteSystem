package com.Cecilia.vote.bean;

import java.util.Date;

/**
 * 投票详情Bean
 * Created by Cecilia on 2017/8/1.
 */
public class VoteDetailBean {

    private String userId;//用户id

    private int changeNumber;//投票选项

    private Date voteTime;//投票时间

    public VoteDetailBean() {
    }

    public VoteDetailBean(String userId, String changeNumber, String voteTime) {
        this.userId = userId;
        this.changeNumber = Integer.valueOf(changeNumber);
        this.voteTime = new Date(Long.valueOf(voteTime));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getChangeNumber() {
        return changeNumber;
    }

    public void setChangeNumber(int changeNumber) {
        this.changeNumber = changeNumber;
    }

    public Date getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Date voteTime) {
        this.voteTime = voteTime;
    }
}
