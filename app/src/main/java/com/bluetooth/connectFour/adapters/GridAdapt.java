package com.bluetooth.connectFour.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bluetooth.connectFour.R;
import com.bluetooth.connectFour.tools.Constants;
import com.bluetooth.connectFour.tools.Player;

public class GridAdapt extends BaseAdapter {

    private int[] piecesTray = new int[42];
    private String[][] piecesPlayed = new String[7][6];
    private int[] piecesColumn = new int[7];
    private LayoutInflater inflter;

    public GridAdapt (Context applicationContext){
        inflter = (LayoutInflater.from(applicationContext));
        initGrid();
    }

    /**
     * BaseAdapter method
     * How many items are in the data set represented by this Adapter.
     * @return
     */
    @Override
    public int getCount() { return 0; }

     /**
     * BaseAdapter method
     * Get the data item associated with the specified position in the data set.
     * @return
     */
    @Override
    public Object getItem(int position) { return piecesTray[position]; }

    /**
     * BaseAdapter method
     * Get the row id associated with the specified position in the list.
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) { return position; }

    /**
     * BaseAdapter method
     * Get a View that displays the data at the specified position in the data set.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.row_pion,parent,false);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imagePion);
        icon.setImageResource(piecesTray[position]);

        return convertView;
    }

    /**
     * Initializes the empty grid
     */
    public void initGrid(){
        for(int i = 0; i<=41; i++)
            piecesTray[i] = R.drawable.ic_pvide;

        for(int col = 0; col < 7; col ++){
            for(int line = 0; line < 6; line ++)
                piecesPlayed[col][line] = null;
            piecesColumn[col] = 0;
        }
    }

    /**
     * Method that allows the placement of a piece by a player
     * @param position Position of the part to be placed
     * @param player Current player
     * @param colorPhone Player's room color
     * @return From the game after placing the part (IN_GAME, RES_EQUAL, RES_LOOSE, RES_WINS)
     */
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
                            return Constants.RES_EQUAL;
                        }
                    } else {
                        if(player.getColor_piece() == colorPhone) {
                            return Constants.RES_WINS;
                        }
                        else{
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

    /**
     * Method used to check if the grid is not full
     * @return Booleen
     */
    private boolean stillPlayable() {
        boolean playable = false;

        for(int i=0; i<=6; i++){
            if(this.piecesColumn[i] < 6)
                playable = true;
        }
        return playable;
    }
}
