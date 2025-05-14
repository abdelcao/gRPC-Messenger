package com.adia.chat.entity;

import java.io.Serializable;
import java.util.Objects;

public class GroupeMemberId implements Serializable {
    private Integer userId;
    private Integer groupeId;

    public GroupeMemberId() {}

    public GroupeMemberId(Integer userId, Integer groupeId) {
        this.userId = userId;
        this.groupeId = groupeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Integer groupeId) {
        this.groupeId = groupeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupeMemberId that = (GroupeMemberId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(groupeId, that.groupeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupeId);
    }
} 