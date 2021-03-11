package com.bluetooth.puissanceFour.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bluetooth.puissanceFour.R;
import com.bluetooth.puissanceFour.tools.Constants;
import com.bluetooth.puissanceFour.tools.Player;

public class GridAdapt extends BaseAdapter {

    private int[] piecesTray = new int[42];
    private String[][] piecesPlayed = new String[7][6];
    private int[] piecesColumn = new int[7];

    private LayoutInflater inflter;
    private Context context;

    private Player player_1;
    private Player player_2;

    public GridAdapt (Context applicationContext){
        this.context = applicationContext;
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

    private void initGrid(){
        for(int i = 0; i<=41; i++)
            piecesTray[i] = R.drawable.ic_vide;

        for(int col = 0; col < 7; col ++){
            for(int line = 0; line < 6; line ++)
                piecesPlayed[col][line] = null;
            piecesColumn[col] = 0;
        }
    }

    public void placePiece(int position, Player player){
        int column = position % 7;

        if(piecesColumn[column] < 6 ){
            int line = 5;
            boolean place = false;

            do {
                if(piecesPlayed[column][line] == null) {
                    place = true;

                    piecesColumn[column] = piecesColumn[column] + 1;
                    piecesPlayed[column][line] = player.getId_bluetooth();

                    int newPosition = column + (line * 7);

                    if (Constants.YELLOW_PIECE.equals(player.getColor_piece()))
                        piecesTray[newPosition] = R.drawable.ic_jaune;
                    else if (Constants.RED_PIECE.equals(player.getColor_piece()))
                        piecesTray[newPosition] = R.drawable.ic_rouge;

                    notifyDataSetChanged();

                   if (!player.playerWin(piecesPlayed)) {
                        if (!stillPlayable()) {
                            //AlertDialog -> EQUALITY
                        }
                    } else {
                        //alerteDialog -> WIN
                        //alerteDialog -> LOOSE
                    }
                } else {
                    line --;
                }
            } while(!place);
        } else {
            System.out.println("Essayez une autre colonne");
        }
    }

    private boolean stillPlayable() {
        boolean playable = true;

        for(int i=0; i<=6; i++){
            if(this.piecesColumn[i] == 6)
                playable = false;
        }
        return playable;
    }



}
