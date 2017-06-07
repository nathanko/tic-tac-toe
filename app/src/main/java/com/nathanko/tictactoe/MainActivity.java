package com.nathanko.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;
    ValueEventListener changeListener;
    Game currGame = null;
    int myNum = 1;
    String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference("ttt");

        changeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currGame = dataSnapshot.getValue(Game.class);
                Log.v("DB", "got snapshot of " + dataSnapshot.getKey() + " from pullData");
                if (currGame != null) {
                    updateBoard();
                    checkEndCases();
                    if (currGame.playersJoined == 0) {
                        String msg = "DISCONNECTED";
                        ((TextView) findViewById(R.id.gameName)).setText(msg);
                    }
                } else {

                    String msg = "DISCONNECTED";
                    ((TextView) findViewById(R.id.gameName)).setText(msg);
                }
                Log.v("DB", "updated board");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("DB", "The read failed: " + databaseError.getCode());
            }
        };
        database.addValueEventListener(changeListener);

        String msg = "DISCONNECTED";
        ((TextView) findViewById(R.id.gameName)).setText(msg);
    }

    public void joinGame(View v) {

        //set new game if no game
        if (currGame == null) {
            database.setValue(new Game());
        }

        if (currGame == null) {
            database.addValueEventListener(changeListener);
        }

        if (currGame.playersJoined == 0) {
            myNum = 1;
            symbol = "O";
            database.child("playersJoined").setValue(++currGame.playersJoined);

            String msg = "JOINED P1";
            ((TextView) findViewById(R.id.gameName)).setText(msg);
            Log.v("DB", "joined game as 1st player 0");
        } else if (currGame.playersJoined == 1) {
            myNum = 2;
            symbol = "X";
            database.child("playersJoined").setValue(++currGame.playersJoined);
            database.child("gameState").setValue(0);

            String msg = "JOINED P2";
            ((TextView) findViewById(R.id.gameName)).setText(msg);
            Log.v("DB", "joined game as 2nd player 1");
        } else {
            //game full
            Toast.makeText(this, "Game room full. Try another time.", Toast.LENGTH_LONG).show();
        }
    }


    public void pressButton(View v) {
        Log.v("DB", ((Button) v).getText().toString() + "/" + currGame.gameState + "/" + currGame.nextTurn());
        if (((Button) v).getText().toString().equals("") && currGame.gameState == 0 && (currGame.nextTurn() == myNum || currGame.nextTurn() == 0)) {
            ((Button) v).setText(symbol);
            currGame.board.set(Integer.parseInt(getResources().getResourceEntryName(v.getId()).substring(1, 2)), myNum);
            database.setValue(currGame);
            checkEndCases();


            //is_myTurn = false;
        } else {
            Toast.makeText(this, "YOU CAN'T PRESS THAT!", Toast.LENGTH_SHORT).show();
        }

    }


    public void updateBoard() { //(string[] value){
        for (int index = 0; index < 9; index++) {
            String text = "";
            if (currGame.board.get(index) == 1) text = "O";
            else if (currGame.board.get(index) == 2) text = "X";

            if (index == 0) {
                ((Button) findViewById(R.id.b0)).setText(text);
            } else if (index == 1) {
                ((Button) findViewById(R.id.b1)).setText(text);
            } else if (index == 2) {
                ((Button) findViewById(R.id.b2)).setText(text);
            } else if (index == 3) {
                ((Button) findViewById(R.id.b3)).setText(text);
            } else if (index == 4) {
                ((Button) findViewById(R.id.b4)).setText(text);
            } else if (index == 5) {
                ((Button) findViewById(R.id.b5)).setText(text);
            } else if (index == 6) {
                ((Button) findViewById(R.id.b6)).setText(text);
            } else if (index == 7) {
                ((Button) findViewById(R.id.b7)).setText(text);
            } else if (index == 8) {
                ((Button) findViewById(R.id.b8)).setText(text);
            }
        }
    }

    public void checkEndCases() {
        //Toast.makeText(this, "Checking end cases", Toast.LENGTH_SHORT).show();
        if (is_win()) {
            currGame.gameState = myNum;
            Toast.makeText(this, "CONGRATULATIONS! Throws Confetti *<|:) (/^o^)/\\(^~^)/\\(^o^\\)", Toast.LENGTH_LONG).show();
        } else if (is_lose()) {
            currGame.gameState = myNum % 2 + 1;
            Toast.makeText(this, "You're a loser.", Toast.LENGTH_LONG).show();
        } else if (is_tie()) {
            currGame.gameState = 3;
            Toast.makeText(this, "Tie", Toast.LENGTH_LONG).show();
        }
    }


    public boolean is_tie() {
        for (int i = 0; i < 9; i++) {
            if (currGame.board.get(i) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean is_win() {
        return (
                currGame.board.get(0) == myNum && currGame.board.get(1) == myNum && currGame.board.get(2) == myNum ||
                        currGame.board.get(3) == myNum && currGame.board.get(4) == myNum && currGame.board.get(5) == myNum ||
                        currGame.board.get(6) == myNum && currGame.board.get(7) == myNum && currGame.board.get(8) == myNum ||
                        currGame.board.get(0) == myNum && currGame.board.get(3) == myNum && currGame.board.get(6) == myNum ||
                        currGame.board.get(1) == myNum && currGame.board.get(4) == myNum && currGame.board.get(7) == myNum ||
                        currGame.board.get(2) == myNum && currGame.board.get(5) == myNum && currGame.board.get(8) == myNum ||
                        currGame.board.get(0) == myNum && currGame.board.get(4) == myNum && currGame.board.get(8) == myNum ||
                        currGame.board.get(2) == myNum && currGame.board.get(4) == myNum && currGame.board.get(6) == myNum
        );
    }

    public boolean is_lose() {
        int otherNum = myNum % 2 + 1;

        return (
                currGame.board.get(0) == otherNum && currGame.board.get(1) == otherNum && currGame.board.get(2) == otherNum ||
                        currGame.board.get(3) == otherNum && currGame.board.get(4) == otherNum && currGame.board.get(5) == otherNum ||
                        currGame.board.get(6) == otherNum && currGame.board.get(7) == otherNum && currGame.board.get(8) == otherNum ||
                        currGame.board.get(0) == otherNum && currGame.board.get(3) == otherNum && currGame.board.get(6) == otherNum ||
                        currGame.board.get(1) == otherNum && currGame.board.get(4) == otherNum && currGame.board.get(7) == otherNum ||
                        currGame.board.get(2) == otherNum && currGame.board.get(5) == otherNum && currGame.board.get(8) == otherNum ||
                        currGame.board.get(0) == otherNum && currGame.board.get(4) == otherNum && currGame.board.get(8) == otherNum ||
                        currGame.board.get(2) == otherNum && currGame.board.get(4) == otherNum && currGame.board.get(6) == otherNum
        );
    }

    public void reset(View v) {
        database.setValue(new Game());
        String msg = "DISCONNECTED";
        ((TextView) findViewById(R.id.gameName)).setText(msg);
        currGame = new Game();
    }

}