package com.example.dominik.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Code by Mike Hein | Justin Diaz | [Code aus SingleplayerActivity (Dominik Erbacher) kopiert]

public class MultiplayerActivity extends AppCompatActivity implements Chronometer.OnChronometerTickListener {

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

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    String hostusername;
    String username;
    String boardcreated;
    String gamecreated;
    int numberofMoves;
    boolean timerStarted = false;
    boolean newMessage = true;
    int turn;
    boolean alreadyMyTurn;
    ArrayList<String> bidListSorted;
    HashMap < String, String > bids;



    private static final String TAG = "MultiplayerActivity";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            hostusername = extras.getString("hostusername");
            boardcreated = extras.getString("boardcreated");
            gamecreated = extras.getString("gamecreated");

        }



        getPreferences();

        final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
        final FirebaseInterface fbInterface = new FirebaseInterface();


        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        final Button leftButton = findViewById(R.id.leftButtonMP);
        final Button rightButton = findViewById(R.id.rightButtonMP);
        final Button middleButton = findViewById(R.id.middleButtonMP);
        robotSelected = findViewById(R.id.robotSelected);

        moves = findViewById(R.id.moves);
        moves.setText(String.valueOf(moveCounter));

        Gson gson = new Gson();
        game = gson.fromJson(gamecreated, Game.class);
        board = gson.fromJson(boardcreated, Board.class);

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

        if(!timerStarted) {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                    if (lobby.isTimerSet() && !timerStarted) {
                        startTimer();
                        timerStarted = true;
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //always check for new toast message
        if(newMessage){
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                  if(lobby.isNewMessage()) {
                      Toast.makeText(MultiplayerActivity.this, lobby.getToastMessages(), Toast.LENGTH_SHORT).show();
                  lobby.setNewMessage(false);
                  fbInterface.createLobby(lobby);
                  }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //make turn
        if(newMessage){

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                    if(lobby.isNextTurn()) {
                        makeTurn();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }




                // BUTTONS
                // Player leaves lobby
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(buttonClick);

                     /*   if (hostusername.equalsIgnoreCase(username)) {
                            fbInterface.removeLobby(lobby);
                            Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                            startIntent.putExtra("username", username);
                            //switchingActivity = true;
                            startActivity(startIntent);
                        } else {
                            fbInterface.removePlayerFromLobby(lobby, username);
                            Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                            startIntent.putExtra("username", username);
                            //switchingActivity = true;
                            startActivity(startIntent);
                        }*/

                    }
                });

                // Player has solution
                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(buttonClick);

                     /*   if (timePlayed != null)
                            if (((SystemClock.elapsedRealtime() - timePlayed.getBase()) / 1000) > 60){
                                lobby.setBidPhaseOver(true);
                                fbInterface.createLobby(lobby);
                        }*/


                        // Enter amount of moves needed to solve board
                        final AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerActivity.this);
                        final LayoutInflater inflater = MultiplayerActivity.this.getLayoutInflater();
                        View promptView = inflater.inflate(R.layout.dialog_moves, null);
                        a_builder.setView(promptView);

                        final EditText edittext = promptView.findViewById(R.id.bid);

                        a_builder.setPositiveButton("Confirm Bid!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Attach a listener to read the data at our lobby' reference
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                                        if (!lobby.isBidPhaseOver()) {
                                            //bid map
                                            Gson gson = new Gson();
                                            bids = gson.fromJson(lobby.getBidMap(), HashMap.class);



                                            numberofMoves = Integer.parseInt(edittext.getText().toString());
                                            if (edittext.getText().toString().equalsIgnoreCase("")) {
                                                Toast.makeText(MultiplayerActivity.this, "Please enter valid number of moves!", Toast.LENGTH_SHORT).show();

                                            } else if (numberofMoves <= 0) {
                                                Toast.makeText(MultiplayerActivity.this, "Please enter a value larger than 0!", Toast.LENGTH_SHORT).show();

                                            } else if (!lobby.isBidPhaseOver()) {


                                                // set bid after first bid
                                                if (lobby.isFirstBidExists()) {
                                                    if (bids.containsKey(username)) {
                                                        if (numberofMoves < Integer.parseInt(bids.get(username))) {
                                                            for (int i = 0; i < lobby.getPlayers().size(); i++) {
                                                                if (lobby.getPlayers().get(i).USERNAME.equalsIgnoreCase(username)) {
                                                                    lobby.getPlayers().get(i).HAS_SOLUTION = true;
                                                                    lobby.getPlayers().get(i).NR_MOVES = numberofMoves;
                                                                    bids.put(username, Integer.toString(numberofMoves));
                                                                    String bidMapData = gson.toJson(bids);

                                                                    try {
                                                                        JSONObject request3 = new JSONObject(bidMapData);

                                                                    } catch (JSONException e) {
                                                                        // TODO Auto-generated catch block
                                                                        e.printStackTrace();
                                                                    }
                                                                    if(numberofMoves < lobby.getLowestBidderMoves()) {
                                                                        lobby.setLowestBidderMoves(numberofMoves);
                                                                        lobby.setLowestBidder(username);

                                                                    }
                                                                    lobby.setBidMap(bidMapData);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        for (int i = 0; i < lobby.getPlayers().size(); i++) {
                                                            if (lobby.getPlayers().get(i).USERNAME.equalsIgnoreCase(username)) {
                                                                lobby.getPlayers().get(i).HAS_SOLUTION = true;
                                                                lobby.getPlayers().get(i).NR_MOVES = numberofMoves;
                                                                bids.put(username, Integer.toString(numberofMoves));

                                                                String bidMapData = gson.toJson(bids);

                                                                try {
                                                                    JSONObject request3 = new JSONObject(bidMapData);

                                                                } catch (JSONException e) {
                                                                    // TODO Auto-generated catch block
                                                                    e.printStackTrace();
                                                                }
                                                                lobby.setBidMap(bidMapData);
                                                                if(numberofMoves < lobby.getLowestBidderMoves()) {
                                                                    lobby.setLowestBidderMoves(numberofMoves);
                                                                    lobby.setLowestBidder(username);

                                                                }
                                                                break;
                                                            }
                                                        }

                                                    }


                                                }


                                                // set first bid
                                                if (!lobby.isFirstBidExists()) {

                                                    for (int i = 0; i < lobby.getPlayers().size(); i++) {
                                                        if (lobby.getPlayers().get(i).USERNAME.equalsIgnoreCase(username)) {
                                                            lobby.getPlayers().get(i).HAS_SOLUTION = true;
                                                            lobby.getPlayers().get(i).NR_MOVES = numberofMoves;
                                                            lobby.setLowestBidder(username);
                                                            lobby.setLowestBidderMoves(numberofMoves);
                                                            lobby.setFirstBidExists(true);
                                                            bids.put(username, Integer.toString(numberofMoves));

                                                            String bidMapData = gson.toJson(bids);

                                                            try {
                                                                JSONObject request3 = new JSONObject(bidMapData);

                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }
                                                            lobby.setTimerSet(true);
                                                            lobby.setBidMap(bidMapData);

                                                            break;
                                                        }
                                                    }
                                                }


                                                fbInterface.createLobby(lobby);


                                            }


                                        } else  Toast.makeText(MultiplayerActivity.this, "Bidding phase is over!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("The read failed: " + databaseError.getCode());
                                        Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });


                        a_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog = a_builder.create();
                        dialog.show();

                    }});

                        //zu Einstellungen wechseln
                        middleButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(buttonClick);
                                Intent startIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(startIntent);
                            }
                        });

          /*    }

          @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });*/




    }

    public void onChronometerTick(Chronometer timePlayed) {
        if (((SystemClock.elapsedRealtime() - timePlayed.getBase()) / 1000) >= 60) {
            timePlayed.stop();
            Toast.makeText(MultiplayerActivity.this, "Bidding phase is now over!", Toast.LENGTH_SHORT).show();
            final FirebaseInterface fbInterface = new FirebaseInterface();
            final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);

                    Toast.makeText(MultiplayerActivity.this, "Lowest Bid: " +lobby.getLowestBidderMoves() +" by " +lobby.getLowestBidder(), Toast.LENGTH_SHORT).show();

                    lobby.setBidPhaseOver(true);
                    fbInterface.createLobby(lobby);

                    //bid map
                    Gson gson = new Gson();
                    bids = gson.fromJson(lobby.getBidMap(), HashMap.class);

                   bidListSorted = new ArrayList<String>(bids.values());
                    // Sorting
                    Collections.sort(bidListSorted, new Comparator<String>() {
                        @Override
                        public int compare(String obj1, String obj2)
                        {

                            if (obj1 == obj2) {
                                return 0;
                            }
                            if (obj1 == null) {
                                return -1;
                            }
                            if (obj2 == null) {
                                return 1;
                            }

                            return obj1.compareTo(obj2);

                        }
                    });

                  makeTurn();

                 /*   for(int i = 0; i < bids.size(); i++){

                        Log.v("cuck", getAllKeysForValue(bids, bidListSorted.get(i)).get(i));

                    }*/


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });





        }
    }


    // GAME METHODS

    public void resetGame() {
        robotGoalView = findViewById(R.id.robotGoalView);
        game = SerializationUtils.clone(backupGame);
        board = SerializationUtils.clone(backupBoard);
        upperLayout = new GameBoardUpperLayout_RT(MultiplayerActivity.this, board, game);
        robotGoalView.setAdapter(upperLayout);
        startTimer();
        moveCounter = 0;
        moves.setText(String.valueOf(moveCounter));
    }

    @SuppressLint("ClickableViewAccessibility")
    public void listenForGesture() {
        final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
        final FirebaseInterface fbInterface = new FirebaseInterface();
        // if already had turn, then turn off listenforgesture

        //Conditional: Welche Gesture-Methode wurde in den Einstellungen ausgewählt (Click = true | Swipe = false)
           if (moveGesture) {
               robotGoalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View v,final int position, long id) {
                       if(!alreadyMyTurn) {
                           x = position % 16;
                       y = 15 - position / 16;
                       Field currentField = MultiplayerActivity.this.board.getField(x, y);
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
                       } else

                       {
                           boolean check = checkDirection(x, y);
                           //zu erst roboter ausgewählt -> stage 1 -> feld angeklickt, das vertikal / horizontal passt
                           if (check && !alreadyMyTurn) {
                               game.getRobots()[currentRobot].moveRobot(board, direction);
                               moveCounter++;

                               // if player goes over his bid its the next players turn
                               if (moveCounter >= Integer.parseInt(bids.get(username))) {

                                   myRef.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                                           lobby.setNextTurn(true);
                                           fbInterface.createLobby(lobby);
                                       }


                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {
                                           System.out.println("The read failed:dw " + databaseError.getCode());
                                           Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                                       }
                                   });
                                   alreadyMyTurn = true;
                                   makeTurn();

                               }


                               moves.setText(String.valueOf(moveCounter));
                               stage = 0;
                               upperLayout.notifyDataSetChanged();
                               robotSelected.setText("");
                               if (currentRobot == 4) ;
                               else if (game.getRobots()[currentRobot].getPosition().toString().equals(currentTargetPosition.toString()) && ((game.getRobots()[currentRobot].getColor().equals(currentTarget.getTargetColor())) || currentTarget.getTargetColor().equals(Color.VORTEX))) {
                                   alreadyMyTurn = true;
                                  showMessage(getAllKeysForValue(bids, bidListSorted.get(turn - 1)).get(turn - 1) + "won the game!");
                                   win();
                               }
                           } else {
                               //zu erst roboter ausgewählt -> stage 1 -> feld angeklickt, das nicht vertikal / horizontal zu roboter passt
                               stage = 0;
                               robotSelected.setText("");
                           }
                       }


                   } else Toast.makeText(MultiplayerActivity.this, "Sorry, but you've exceeded your bid!", Toast.LENGTH_SHORT).show();

                   }

               });
           } else {
               //Swipe/Wisch Gesten
               robotGoalView.setOnTouchListener(new OnSwipeTouchListener(MultiplayerActivity.this) {
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
            Field currentField = MultiplayerActivity.this.board.getField(x, y);
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
                if (currentRobot == 4);
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
        AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerActivity.this);
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
        final AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerActivity.this);
        LayoutInflater inflater = MultiplayerActivity.this.getLayoutInflater();

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
            timePlayed.setOnChronometerTickListener(this);
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

    static <K, V> List<K> getAllKeysForValue(Map<K, V> mapOfWords, V value)
    {
        List<K> listOfKeys = null;

        //Check if Map contains the given value
        if(mapOfWords.containsValue(value))
        {
            // Create an Empty List
            listOfKeys = new ArrayList<>();

            // Iterate over each entry of map using entrySet
            for (Map.Entry<K, V> entry : mapOfWords.entrySet())
            {
                // Check if value matches with given value
                if (entry.getValue().equals(value))
                {
                    // Store the key from entry to the list
                    listOfKeys.add(entry.getKey());
                }
            }
        }
        // Return the list of keys whose value matches with given value.
        return listOfKeys;
    }

    public void makeTurn(){
if(!alreadyMyTurn) {
    final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
    final FirebaseInterface fbInterface = new FirebaseInterface();
    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final Lobby lobby = dataSnapshot.getValue(Lobby.class);
            lobby.setTurn(lobby.getTurn() + 1);
            Gson gson = new Gson();
            bids = gson.fromJson(lobby.getBidMap(), HashMap.class);
            lobby.setNextTurn(false);
            if (lobby.getTurn()-1 < bidListSorted.size()) {


                showMessage("It is now " + getAllKeysForValue(bids, bidListSorted.get(lobby.getTurn() - 1)).get(lobby.getTurn() - 1) + "'s turn!");
                fbInterface.createLobby(lobby);
                if (username.equalsIgnoreCase(getAllKeysForValue(bids, bidListSorted.get(lobby.getTurn() - 1)).get(lobby.getTurn() - 1))) {
                    listenForGesture();

                }
            } else
                showMessage("NOBODY, COULD SOLVE THE BOARD");
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
            Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
        }
    });
}

        }



    public void showMessage(final String msg){
        Toast.makeText(MultiplayerActivity.this, msg, Toast.LENGTH_SHORT).show();

        final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
        final FirebaseInterface fbInterface = new FirebaseInterface();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                lobby.setToastMessages(msg);
                lobby.setNewMessage(true);
                fbInterface.createLobby(lobby);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(MultiplayerActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
