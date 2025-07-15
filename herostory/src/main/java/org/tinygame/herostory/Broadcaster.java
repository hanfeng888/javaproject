package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Broadcaster TODO
 *
 * @author hf
 * @date 2025/6/30 15:00:30
 */
public final class Broadcaster {

    /**
     * 客户端信道数组，一定要使用static，否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有默认构造器
     */
    private Broadcaster() {
    }

    /**
     * 增加信道
     *
     * @param channel
     * @return
     */
    public static boolean addChannel(Channel channel) {
        return _channelGroup.add(channel);
    }

    /**
     * 移除信道
     *
     * @param channel
     * @return
     */
    public static boolean removeChannel(Channel channel) {
        return _channelGroup.remove(channel);
    }

    /**
     * 广播消息
     *
     * @param msg
     * @return
     */
    public static void broadcast(Object msg) {
        if (null == msg) {
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
}
