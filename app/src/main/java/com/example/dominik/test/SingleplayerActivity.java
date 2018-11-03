package com.example.dominik.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.commons.lang3.SerializationUtils;

import java.util.concurrent.TimeUnit;

//Code by Dominik Erbacher | 5016085
public class SingleplayerActivity extends AppCompatActivity {
    Board backupBoard; //für reset!
    Board board;
    Game game;
    Game backupGame;
    Chronometer timePlayed;
    GridView robotGoalView;
    int lastY;
    int lastX;
    Direction direction;
    Position currentTargetPosition;
    Target currentTarget;
    int moveCounter = 0;
    int x;
    int y;
    int stage = 0;
    int currentRobot;
    TextView robotSelected;
    TextView moves;
    TextView noTimePlayed;
    boolean moveGesture;
    boolean amountRobots;
    boolean timer;
    boolean oldTimer;
    GameBoardUpperLayout_RT upperLayout;
    int positionOldRobot;
    Color robotColor;
    long timeWhenStopped;
    String finishTime;
    private static final String TAG = "SingleplayerActivity";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        getPreferences();

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        Button resetGame = findViewById(R.id.resetbutton_left);
        Button newGame = findViewById(R.id.newgamebutton_right);
        Button settings = findViewById(R.id.settingsButton);
        robotSelected = findViewById(R.id.robotSelected);

        moves = findViewById(R.id.moves);
        moves.setText(String.valueOf(moveCounter));
        board = new Board(16);

        final Board[] boards = Board.createBoards();
        //
        game = new Game(boards, 1, !amountRobots);
        board = game.getBoard();

        //Apache Librabry zum Klonen des Board & GameObjekts
        backupGame = SerializationUtils.clone(game);
        backupBoard = SerializationUtils.clone(board);

        //Brett + Felder generieren
        GridView boardView = findViewById(R.id.gameView);
        boardView.setAdapter(new GameBoardLowerLayout(this, this.board));

        // obere Layer, auf der die Roboter & Ziele platziert werden!! hier muss mit onClickListener gearbeitet werden!!
        robotGoalView = findViewById(R.id.robotGoalView);
        upperLayout = new GameBoardUpperLayout_RT(this, this.board, game);
        robotGoalView.setAdapter(upperLayout);

        currentTargetPosition = game.getCurrentTarget().getTargetPostion();
        currentTarget = game.getCurrentTarget();
        if (currentTarget.getTargetColor().equals(Color.VORTEX)) {
            showVortexWarning();
        }

        startTimer();
        listenForGesture();

        //Game auf Anfang zurücksetzen, Timer neustarten
        resetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                robotGoalView = findViewById(R.id.robotGoalView);
                game = SerializationUtils.clone(backupGame);
                board = SerializationUtils.clone(backupBoard);
                upperLayout = new GameBoardUpperLayout_RT(SingleplayerActivity.this, board, game);
                robotGoalView.setAdapter(upperLayout);
                startTimer();
                moveCounter = 0;
                moves.setText(String.valueOf(moveCounter));

            }
        });
        //neues Board, Timer auf 0
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        //zu Einstellungen wechseln
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent startIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(startIntent);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void listenForGesture() {
        //Conditional: Welche Gesture-Methode wurde in den Einstellungen ausgewählt (Click = true | Swipe = false)
        if (moveGesture) {
            robotGoalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    x = position % 16;
                    y = 15 - position / 16;
                    Field currentField = SingleplayerActivity.this.board.getField(x, y);
                    if (stage == 0 || currentField.hasRobot()) {

                        //check for Robot
                        if (currentField.hasRobot()) {
                            stage = 1;
                            lastX = x;
                            lastY = y;
                            for (int i = 0; i < game.getRobots().length; i++) {
                                if (x == game.getRobots()[i].getPosition().getX() && y == game.getRobots()[i].getPosition().getY()) {
                                    currentRobot = i;
                                    robotSelected.setText(game.getRobots()[i].getColor().toString() + " " + getString(R.string.robotAusgewaehlt));
                                    positionOldRobot = position;
                                    robotColor = game.getRobots()[i].getColor();
                                }
                            }
                        }
                        if (!currentField.hasRobot() && !currentField.hasGoal()) {
                            robotSelected.setText("");
                        }
                    } else {
                        boolean check = checkDirection(x, y);
                        //zu erst roboter ausgewählt -> stage 1 -> feld angeklickt, das vertikal / horizontal passt
                        if (check) {
                            game.getRobots()[currentRobot].moveRobot(board, direction);
                            moveCounter++;
                            moves.setText(String.valueOf(moveCounter));
                            stage = 0;
                            upperLayout.notifyDataSetChanged();
                            robotSelected.setText("");
                            if (currentRobot == 4) ;
                            else if (game.getRobots()[currentRobot].getPosition().toString().equals(currentTargetPosition.toString()) && ((game.getRobots()[currentRobot].getColor().equals(currentTarget.getTargetColor())) || currentTarget.getTargetColor().equals(Color.VORTEX))) {
                                win();
                            }
                        } else {
                            //zu erst roboter ausgewählt -> stage 1 -> feld angeklickt, das nicht vertikal / horizontal zu roboter passt
                            stage = 0;
                            robotSelected.setText("");
                        }
                    }
                }
            });
        } else {
            //Swipe/Wisch Gesten
            robotGoalView.setOnTouchListener(new OnSwipeTouchListener(SingleplayerActivity.this) {
                public void onSwipeTop() {
                    position = robotGoalView.pointToPosition(eventX, eventY);
                    swipeRobotMove(position, Direction.N);
                }

                public void onSwipeRight() {
                    position = robotGoalView.pointToPosition(eventX, eventY);
                    swipeRobotMove(position, Direction.E);
                }

                public void onSwipeLeft() {
                    position = robotGoalView.pointToPosition(eventX, eventY);
                    swipeRobotMove(position, Direction.W);
                }

                public void onSwipeBottom() {
                    position = robotGoalView.pointToPosition(eventX, eventY);
                    swipeRobotMove(position, Direction.S);
                }

            });
        }
    }

    public boolean checkDirection(int x, int y) {
        if (x == lastX && y > lastY) {
            direction = Direction.N;
            return true;
        } else if (x == lastX && y < lastY) {
            direction = Direction.S;
            return true;
        } else if (x < lastX && y == lastY) {
            direction = Direction.W;
            return true;
        } else if (x > lastX && y == lastY) {
            direction = Direction.E;
            return true;
        } else {
            direction = null;
            return false;
        }
    }


    public void swipeRobotMove(int position, Direction swipeDirection) {
        x = position % 16;
        y = 15 - position / 16;
        //Log.v(TAG,x + " | " +y);

        if ((x >= 0 && x < 16) && (y >= 0 && y < 16)) {
            Field currentField = SingleplayerActivity.this.board.getField(x, y);
            if (currentField.hasRobot()) {
                lastX = x;
                lastY = y;
                for (int i = 0; i < game.getRobots().length; i++) {
                    if (x == game.getRobots()[i].getPosition().getX() && y == game.getRobots()[i].getPosition().getY()) {
                        currentRobot = i;
                        positionOldRobot = position;
                        robotColor = game.getRobots()[i].getColor();
                    }
                }
                game.getRobots()[currentRobot].moveRobot(board, swipeDirection);
                moveCounter++;
                moves.setText(String.valueOf(moveCounter));
                upperLayout.notifyDataSetChanged();
                if (currentRobot == 4) ;
                else if (game.getRobots()[currentRobot].getPosition().toString().equals(currentTargetPosition.toString()) && ((game.getRobots()[currentRobot].getColor().equals(currentTarget.getTargetColor())) || currentTarget.getTargetColor().equals(Color.VORTEX))) {
                    win();
                }
            }
        }
    }


    public void win() {
        if (timer) {
            timePlayed.stop();

            long millis = SystemClock.elapsedRealtime() - timePlayed.getBase();
            finishTime = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            finishTime = "-";
        }
        AlertDialog.Builder a_builder = new AlertDialog.Builder(SingleplayerActivity.this);
        if (moveCounter == 1) {
            a_builder.setMessage("Sie haben das Spiel mit genau einem Zug beendet. Zeit: " + finishTime);
        } else {
            a_builder.setMessage("Sie haben das Spiel in " + moveCounter + " Zügen beendet. Zeit: " + finishTime);
        }
        a_builder.setTitle("Herzlichen Glückwunsch!");
        a_builder.setCancelable(false);
        a_builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        a_builder.setNeutralButton("Main Menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        AlertDialog dialog = a_builder.create();
        dialog.show();
    }

    public void showVortexWarning() {
        final AlertDialog.Builder a_builder = new AlertDialog.Builder(SingleplayerActivity.this);
        LayoutInflater inflater = SingleplayerActivity.this.getLayoutInflater();

        a_builder.setView(inflater.inflate(R.layout.dialog_vortexwarning, null));
        AlertDialog dialog = a_builder.create();
        dialog.show();
    }

    private void getPreferences() {
        SharedPreferences pref = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        timer = pref.getBoolean("timer", true);
        amountRobots = pref.getBoolean("amountRobots", true);
        moveGesture = pref.getBoolean("moveGesture", true);
    }

    public void startTimer() {
        oldTimer = timer;
        if (timer) {
            timePlayed = findViewById(R.id.timePlayed);
            timePlayed.setBase(SystemClock.elapsedRealtime());
            timePlayed.start();
        } else {
            noTimePlayed = findViewById(R.id.timePlayed);
            noTimePlayed.setText(getString(R.string.off));
        }
    }

    public void stopTimer() {
        if (timer) {
            timeWhenStopped = timePlayed.getBase() - SystemClock.elapsedRealtime();
            timePlayed.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer) {
            stopTimer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPreferences();
        if ((oldTimer != timer) && timer) {
            startTimer();
        } else if ((oldTimer != timer) && !timer) {
            stopTimer();
            startTimer();
        } else if ((oldTimer == timer) && timer) {
            timePlayed.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            timePlayed.start();
        }
        if (moveGesture) {
            robotGoalView.setOnTouchListener(null);
        } else {
            robotGoalView.setOnItemClickListener(null);
        }
        listenForGesture();
    }
}
