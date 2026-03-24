package net.antichess.ai;

public interface AiPlayer {
    String getMove(String opponentMove, long timeLeft, long opponentTimeLeft);
    byte[] getPersistentState();
}
