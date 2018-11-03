package com.example.dominik.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

//Code by Dominik Erbacher | 5016085
public class GameBoardUpperLayout_RT extends BaseAdapter {
    private Context mContext;
    Integer[] mThumbIds = new Integer[256];
    Board board;
    Game game;
    boolean robotSkin;
    boolean targetSkin;

    public GameBoardUpperLayout_RT(Context c, Board board, Game game) {
        mContext = c;
        this.board = board;
        this.game = game;
        getPreferences();
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public void setItem(int position){
        mThumbIds[position] = R.drawable.transparent;
    }

    public void setItem(int position, Color c){
    }
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return position;
    }

   public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView((mContext));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        for (int i = 0; i < 256; i++) {
            int y = 15- i / 16;
            int x = i % 16;

            int x1 = game.getCurrentTarget().getTargetPostion().getX();
            int y1 = game.getCurrentTarget().getTargetPostion().getY();

            //check ob Target(Ziel) auf aktuellem Feld liegt
            if( x1 == x && y1 == y) {
                Color targetColor = game.getCurrentTarget().getTargetColor();
                //falls user zuvor in den Einstellungen den DEFAULT Skin ausgewählt hat
                if (!targetSkin) {
                    switch (targetColor) {
                        case RED:
                            mThumbIds[i] = R.drawable.target_red_star;
                            break;
                        case BLUE:
                            mThumbIds[i] = R.drawable.target_blue_star;
                            break;
                        case GREEN:
                            mThumbIds[i] = R.drawable.target_green_star;
                            break;
                        case YELLOW:
                            mThumbIds[i] = R.drawable.target_yellow_star;
                            break;
                        case VORTEX:
                            mThumbIds[i] = R.drawable.target_vortex;
                            break;
                    }
                    //falls user zuvor in den Einstellungen den Saturn Skin ausgewählt hat
                } else {

                    switch (targetColor) {
                        case RED:
                            mThumbIds[i] = R.drawable.saturn_red;
                            break;
                        case BLUE:
                            mThumbIds[i] = R.drawable.saturn_blue;
                            break;
                        case GREEN:
                            mThumbIds[i] = R.drawable.saturn_green;
                            break;
                        case YELLOW:
                            mThumbIds[i] = R.drawable.saturn_yellow;
                            break;
                        case VORTEX:
                            mThumbIds[i] = R.drawable.target_vortex;
                            break;
                    }
                }
            }
            else{
                mThumbIds[i] = R.drawable.transparent;
            }
            if(board.getField(x,y).hasRobot()){
                for(int j = 0; j < game.getRobots().length; j++){

                    int xRobot = game.getRobots()[j].getPosition().getX();
                    int yRobot = game.getRobots()[j].getPosition().getY();

                    //check ob Roboter auf aktuellem Feld liegt
                    if(x == xRobot && y == yRobot){
                        //check settings value
                        if(!robotSkin) {
                            switch (game.getRobots()[j].getColor()) {
                                case RED:
                                    mThumbIds[i] = R.drawable.robot_red;
                                    break;
                                case BLUE:
                                    mThumbIds[i] = R.drawable.robot_blue;
                                    break;
                                case GREEN:
                                    mThumbIds[i] = R.drawable.robot_green;
                                    break;
                                case YELLOW:
                                    mThumbIds[i] = R.drawable.robot_yellow;
                                    break;
                                case SILVER:
                                    mThumbIds[i] = R.drawable.robot_black;
                                    break;
                            }
                        }else{
                            switch (game.getRobots()[j].getColor()) {
                                case RED:
                                    mThumbIds[i] = R.drawable.penguin_red;
                                    break;
                                case BLUE:
                                    mThumbIds[i] = R.drawable.penguin_blue;
                                    break;
                                case GREEN:
                                    mThumbIds[i] = R.drawable.penguin_green;
                                    break;
                                case YELLOW:
                                    mThumbIds[i] = R.drawable.penguin_yellow;
                                    break;
                                case SILVER:
                                    mThumbIds[i] = R.drawable.penguin_black;
                                    break;
                            }
                        }
                    }
                }
            }
        }
            imageView.setImageResource(mThumbIds[position]);
            return imageView;
    }
    private void getPreferences() {
        //checkt die Booleanwerte in den SharedPreferences (können vom User in den Einstellungen durch Buttonclick geändert werden)
        SharedPreferences pref = mContext.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        robotSkin = pref.getBoolean("robot",false);
        targetSkin = pref.getBoolean("target",false);
    }
}
