package com.bluetooth.connectFour.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bluetooth.connectFour.R;
import com.bluetooth.connectFour.tools.Constants;
import com.bluetooth.connectFour.tools.Player;

public class GridAdapt extends BaseAdapter {

    public static String msg;
    private int[] piecesTray = new int[42];
    private String[][] piecesPlayed = new String[7][6];
    private int[] piecesColumn = new int[7];
    private LayoutInflater inflter;

    public GridAdapt (Context applicationContext){


        inflter = (LayoutInflater.from(applicationContext));
        initGrid();
    }

    @Override
    public int getCount() {
        return piecesTray.length;
    }

    @Override
    public Object getItem(int position) { return piecesTray[position]; }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflter.inflate(R.layout.row_pion,parent,false);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imagePion);
        icon.setImageResource(piecesTray[position]);

        return convertView;
    }

    public void initGrid(){
        for(int i = 0; i<=41; i++)
            piecesTray[i] = R.drawable.ic_pvide;

        for(int col = 0; col < 7; col ++){
            for(int line = 0; line < 6; line ++)
                piecesPlayed[col][line] = null;
            piecesColumn[col] = 0;
        }
    }

    public String placePiece(int position, Player player, String colorPhone){
        int column = position % 7;

        if(piecesColumn[column] < 6 ){
            int line = 5;
            boolean place = false;

            do {
                if(piecesPlayed[column][line] == null) {
                    place = true;

                    piecesColumn[column] = piecesColumn[column] + 1;
                    piecesPlayed[column][line] = player.getColor_piece();

                    int newPosition = column + (line * 7);

                    if (Constants.YELLOW_PIECE.equals(player.getColor_piece()))
                        piecesTray[newPosition] = R.drawable.ic_pjaune;
                    else if (Constants.RED_PIECE.equals(player.getColor_piece()))
                        piecesTray[newPosition] = R.drawable.ic_prouge;

                    notifyDataSetChanged();

                    if (!player.playerWin(piecesPlayed)) {
                        if (!stillPlayable()) {
                            Log.i("1","???");
                            this.msg = "Egalité !";
                            return Constants.RES_EQUAL;
                        }
                    } else {
                        if(player.getColor_piece() == colorPhone) {
                            Log.i("1","win");
                            this.msg = "Vous avez gagné !";
                            return Constants.RES_WINS;
                        }
                        else{
                            System.out.println("non");
                            Log.i("1","perdu");
                            this.msg = "Vous avez perdu !";
                            return Constants.RES_LOOSE;
                        }
                    }
                } else {
                    line --;
                }
            } while(!place);
        } else {
            System.out.println("Essayez une autre colonne");
        }

        return Constants.IN_GAME;
    }

    private boolean stillPlayable() {
        boolean playable = false;

        for(int i=0; i<=6; i++){
            if(this.piecesColumn[i] < 6)
                playable = true;
        }
        return playable;
    }
}
