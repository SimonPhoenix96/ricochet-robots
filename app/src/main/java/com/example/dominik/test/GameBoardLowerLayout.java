package com.example.dominik.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

//Code by Dominik Erbacher | 5016085
public class GameBoardLowerLayout extends BaseAdapter {
    private Context mContext;

    //16*16 Felder, daher muss ein Array der Länge 256 erstellt werden
    Integer[] mThumbIds = new Integer[256];
    Board board;

    public GameBoardLowerLayout(Context c, Board board) {
        mContext = c;
        this.board = board;
    }

    public int getCount() {
        return mThumbIds.length;
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

            //15-i, da Anfang "unten links" [0][0], oben rechts dementsprechend [15][15]
            int y = 15 - i / 16;
            int x = i % 16;

            if (board.getField(x, y).getWall().getOne() == null) {
                mThumbIds[i] = R.drawable.blank;
            }
            else {

                //checkt ob das Feld eine zweite Wand hat
                if (board.getField(x, y).getWall().getTwo() != null) {
                    Direction one = board.getField(x, y).getWall().getOne();
                    Direction two = board.getField(x, y).getWall().getTwo();

                    //setzt 2Wand-Feld als ImageResource
                    if (one.equals(Direction.N) && two.equals(Direction.W)) {
                        mThumbIds[i] = R.drawable.wand_nordwest;
                    }
                    if (one.equals(Direction.N) && two.equals(Direction.E)) {
                        mThumbIds[i] = R.drawable.wand_nordost;
                    }
                    if (one.equals(Direction.S) && two.equals(Direction.W)) {
                        mThumbIds[i] = R.drawable.wand_suedwest;
                    }
                    if (one.equals(Direction.S) && two.equals(Direction.E)) {
                        mThumbIds[i] = R.drawable.wand_suedost;
                    }

                    if (one.equals(Direction.W) && two.equals(Direction.S)) {
                        mThumbIds[i] = R.drawable.wand_suedwest;
                    }
                    if (one.equals(Direction.W) && two.equals(Direction.N)) {
                        mThumbIds[i] = R.drawable.wand_nordwest;
                    }
                    if (one.equals(Direction.E) && two.equals(Direction.S)) {
                        mThumbIds[i] = R.drawable.wand_suedost;
                    }
                    if (one.equals(Direction.E) && two.equals(Direction.N)) {
                        mThumbIds[i] = R.drawable.wand_nordost;
                    }

                }

                    //setzt 1Wand-Feld als ImageResource
                 else {
                    if (board.getField(x, y).getWall().getOne().equals(Direction.E)) {
                        mThumbIds[i] = R.drawable.wand_rechts;
                    }
                    if (board.getField(x, y).getWall().getOne().equals(Direction.S)) {
                        mThumbIds[i] = R.drawable.wand_unten;
                    }
                    if (board.getField(x, y).getWall().getOne().equals(Direction.N)) {
                        mThumbIds[i] = R.drawable.wand_oben;
                    }
                    if (board.getField(x, y).getWall().getOne().equals(Direction.W)) {
                        mThumbIds[i] = R.drawable.wand_links;
                    }
                }
            }

            //setzt Zentrum-Feld als ImageResource
            if ((x == 7 && y == 8) || (x == 7 && y == 7) || (x == 8 && y == 8) || (x == 8 && y == 7)) {
                mThumbIds[i] = R.drawable.blank_center;
            }
        }
        //erstellt & returned ImageView
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}
