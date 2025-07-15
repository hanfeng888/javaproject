package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


/**
 * GameMsgDecoder TODO
 *
 * @author hf
 * @date 2025/6/30 11:13:32
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BinaryWebSocketFrame) {
            //WebSocket二进制消息会通过 HttpServerCodec解码成 BinaryWebSocketFrame 类对象
            BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = frame.content();
            byteBuf.readShort(); //读取消息的长度
            int msgCode = byteBuf.readShort(); //读取消息编号
            //拿到真实的字节数组 并打印
            byte[] byteArray = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(byteArray);
            Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            if (Objects.isNull(msgBuilder)) {
                LOGGER.error("无法识别的消息，msgCode={}",msgCode);
                return;
            }
            msgBuilder.clear();
            msgBuilder.mergeFrom(byteArray);
            //构建消息
            Message newMsg = msgBuilder.build();
            if (null != newMsg) {
                ctx.fireChannelRead(newMsg);
            }
        } else {
            return;
        }
    }
}
