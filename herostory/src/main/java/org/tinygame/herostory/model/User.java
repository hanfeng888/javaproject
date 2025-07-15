package org.tinygame.herostory.model;

import lombok.Data;

/**
 * User 用户
 *
 * @author hf
 * @date 2025/6/30 13:21:47
 */
@Data
public class User {
    /**
     * 用户Id
     */
    public int userId;
    /**
     * 英雄形象
     */
    public String heroAvatar;
}
