package org.pine.blockparty.model;

public enum GameState {

    IDLE,
    FIRST_ROUND_START,
    PLATFORM_CHANGE,
    XBLOCK_DISPLAY,
    XBLOCK_REMOVAL,
    ROUND_EVALUATION,
    DIFFICULTY_UPDATE,
    WIN_CONDITION_SINGLE_WINNER,
    WIN_CONDITION_TIE,
    WIN_CONDITION_MAX_ROUNDS_EXCEEDED,
    GAME_OVER
}
