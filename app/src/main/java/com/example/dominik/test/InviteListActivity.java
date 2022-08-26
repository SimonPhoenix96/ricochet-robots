package com.example.dominik.test;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.GenericTypeIndicator;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;

//Code by Justin Diaz | 5115108
public class InviteListActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    String username;

    boolean switchingActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_list);
        final ListView lv = (ListView) findViewById(R.id.listViewInvites);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");

        }
        final GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {};

        myRef = database.getInstance().getReference("currentlyOnlinePlayers");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Player> currentlyOnlinePlayers = dataSnapshot.getValue(t);
                ArrayList<String> data = new ArrayList<String>();

                for (int i = 0; i < currentlyOnlinePlayers.size(); i++) {
                    if(currentlyOnlinePlayers.get(i).USERNAME.equalsIgnoreCase(username))
                        data.addAll(currentlyOnlinePlayers.get(i).INVITES);
                }

                lv.setAdapter(new MyListAdaper(InviteListActivity.super.getApplicationContext(), R.layout.invite_list, data));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(InviteListActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(InviteListActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();


            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        if(switchingActivity == false) {
            /*Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(startIntent);*/
            finish();
        }
    }
    @Override
    public void onBackPressed() {


            /*Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(startIntent);*/
            finish();


    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.inviteListItemThumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.inviteListItemText);
                viewHolder.button = (Button) convertView.findViewById(R.id.inviteListItemButton);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        username = extras.getString("username");

                    }
                    final GenericTypeIndicator<List<Player>> t = new GenericTypeIndicator<List<Player>>() {};

                    myRef = database.getInstance().getReference("currentlyOnlinePlayers");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Player> currentlyOnlinePlayers = dataSnapshot.getValue(t);

                            for (int i = 0; i < currentlyOnlinePlayers.size(); i++) {
                                if (currentlyOnlinePlayers.get(i).USERNAME.equalsIgnoreCase(username)) {

                                    // / Join selected user at index position
                                    myRef = database.getInstance().getReference("lobbies/");
                                    final String gameid = currentlyOnlinePlayers.get(i).INVITES.get(position);
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (!dataSnapshot.hasChild(gameid))
                                                Toast.makeText(InviteListActivity.this, "Sorry " + gameid + "'s lobby doesn't exist!", Toast.LENGTH_SHORT).show();

                                            for (DataSnapshot lobbySnapshot : dataSnapshot.getChildren()) {
                                                Lobby returnedlobby = lobbySnapshot.getValue(Lobby.class);
                                                FirebaseInterface fbInterface = new FirebaseInterface();
                                                if (returnedlobby.getLOBBY_OWNER().equals(gameid)) {
                                                    Player player = new Player(username);
                                                    if (returnedlobby.isLOBBY_FULL()) {
                                                        Toast.makeText(InviteListActivity.this, "Sorry, that lobby is full!", Toast.LENGTH_SHORT).show();
                                                    } else if (returnedlobby.isInLobby(username)) {
                                                        Toast.makeText(InviteListActivity.this, "Sorry, you're already in that lobby!", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    fbInterface.addPlayerToLobby(returnedlobby, player);
                                                    String localgame = "no";
                                                    Toast.makeText(InviteListActivity.this, username + " joined " + gameid + "'s lobby!", Toast.LENGTH_SHORT).show();
                                                    // Switch to lobby view
                                                    Intent startIntent = new Intent(getApplicationContext(), CreateGameActivity.class);
                                                    startIntent.putExtra("username", username);
                                                    startIntent.putExtra("hostusername", returnedlobby.getLOBBY_OWNER());
                                                    startIntent.putExtra("maxplayers", String.valueOf(returnedlobby.getMAX_NR_PLAYERS()));
                                                    startIntent.putExtra("localgame", localgame);
                                                    startIntent.putExtra("gamecreated", returnedlobby.getGameData());
                                                    startIntent.putExtra("boardcreated", returnedlobby.getBoardData());
                                                    switchingActivity = true;
                                                    startActivity(startIntent);
                                                    break;
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // ...
                                        }
                                    });

                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                            Toast.makeText(InviteListActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }
}

