package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * UserManager TODO
 *
 * @author hf
 * @date 2025/6/30 15:55:15
 */
public final class UserManager {
    private UserManager() {
    }

    /**
     * 用户字典
     */
    private static final Map<Integer, User> _userMap = new java.util.HashMap<>();


    public static void addUser(User user) {
        if (Objects.nonNull(user)) {
            _userMap.put(user.getUserId(), user);
        }
    }

    public static void removeByUserId(int userId) {
        _userMap.remove(userId);
    }

    public static Collection<User> listUser() {
        return _userMap.values();
    }
}
