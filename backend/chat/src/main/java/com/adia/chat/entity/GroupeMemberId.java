package com.adia.chat.entity;

import java.io.Serializable;
import java.util.Objects;

public class GroupeMemberId implements Serializable {
    private Long userId;
    private Long groupeId;

    public GroupeMemberId() {}

    public GroupeMemberId(Long userId, Long groupeId) {
        this.userId = userId;
        this.groupeId = groupeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
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