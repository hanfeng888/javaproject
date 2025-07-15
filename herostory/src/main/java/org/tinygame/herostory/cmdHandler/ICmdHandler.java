package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * ICmdHandler TODO
 *
 * @author hf
 * @date 2025/7/1 11:30:07
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    void handler(ChannelHandlerContext ctx, TCmd cmd);
}
