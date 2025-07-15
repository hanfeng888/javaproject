package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 *
 * @author hf
 * @date 2025/7/2 16:50:02
 */
public class GameMsgRecognizer {

    /**
     * 私有化类默认构造器
     */
    private GameMsgRecognizer() {
    }

    public static void init() {
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
    }

    /**
     * 消息代码和消息体字典
     */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        Message.Builder msgBuilder = null;
        switch (msgCode) {
            case GameMsgProtocol
                    .MsgCode.USER_ENTRY_CMD_VALUE:
//                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(byteArray);
                msgBuilder = GameMsgProtocol.UserEntryCmd.newBuilder();
                break;

            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
//                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(byteArray);
                msgBuilder = GameMsgProtocol.WhoElseIsHereCmd.newBuilder();
                break;

            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
//                    cmd = GameMsgProtocol.UserMoveToCmd.parseFrom(byteArray);
                msgBuilder = GameMsgProtocol.UserMoveToCmd.newBuilder();
                break;
        }
        return msgBuilder;
    }

    public static Integer getMsgCodeByMsgClazz(Object msg) {
        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            return GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
            return GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
            return GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
            return GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        } else {

            return -1;
        }
    }
}
