package net.antichess.ai;

import java.util.Collection;
import java.util.Properties;

public interface AiPlayerFactory {
    String PROPERTY_TIMED_GAME = "antichess.timedGame";
    String PROPERTY_INIT_TIME_WHITE = "antichess.initTime.white";
    String PROPERTY_INIT_TIME_BLACK = "antichess.initTime.black";
    String PROPERTY_RULE_SET = "antichess.ruleSet";
    String PROPERTY_WHITE_NAME = "antichess.whitePlayerName";
    String PROPERTY_BLACK_NAME = "antichess.blackPlayerName";

    AiPlayer createPlayer(boolean isWhite, Properties gameProperties,
            ChatProxy chatProxy, ChatHistoryViewer chatHistoryViewer,
            byte[] persistentState, int persistentStateSizeLimit);
    Collection<String> getSupportedRulesets();
}
