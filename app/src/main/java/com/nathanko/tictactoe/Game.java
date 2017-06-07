package com.nathanko.tictactoe;

import java.util.ArrayList;
import java.util.Collections;

public class Game {
    public int playersJoined;
    //public enum gameState {IN_GAME, X_WIN, O_WIN, TIE, WAITING};
    public int gameState; // -1 waiting, 0 ingame, 1 o-win, 2 x-lose, 3 tie
    //public int[] board;
    public ArrayList<Integer> board = new ArrayList<Integer>();

    public Game() {
        playersJoined = 0;
        gameState = -1;
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
        board.add(0);
    }

    @Override
    public String toString() {
        return "playersJoined: " + playersJoined + "\ngameState: " + gameState;
    }

    public int nextTurn() {
        int o = Collections.frequency(board, 1);
        int x = Collections.frequency(board, 2);
        if (x > o) {
            return 1;
        } else if (o > x) {
            return 2;
        } else return 0;
    }


}
