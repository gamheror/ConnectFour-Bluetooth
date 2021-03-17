package com.bluetooth.connectFour.tools;

import android.widget.TextView;

public class Player {

    private String colorPawn;
    private int remainingPawn = 21;
    private TextView showRemainingPawn;

    /** permit to associate a pawn color with the player
     * @param pawn, the color of the pawn
     */
    public Player(String pawn) {
        colorPawn = pawn;
    }

    /**
     * @return the pawn's color of the player
     */
    public String getColorPawn() {
        return colorPawn;
    }

    /**
     * @return the number of remaining pawn
     */
    public String getRemainingPawn(){
        return String.valueOf(remainingPawn);
    }

    /** set the text of remaining pawn that will be display
     * @param text
     */
    public void setViewText(TextView text){
        this.showRemainingPawn = text;
    }

    /** get the text of remaining pawn that will be display
     * @return the text
     */
    public TextView getViewText(){
        return this.showRemainingPawn;
    }

    /**
     * decrease the number of remaining pawn of the player
     */
    public void decreaseRemainingPawn(){
        remainingPawn -= 1;
    }

    /** check if the player win
     * @param game, the board game
     * @return true if the player win, else false
     */
    public boolean playerWin(final String[][] game) {

        // Check if player win by column
        for (int i = 0; i<=6; i++) {
            for (int j = 0; j<=2; j++) {
                if (this.colorPawn.equals(game[i][j])
                        && this.colorPawn.equals(game[i][j + 1])
                        && this.colorPawn.equals(game[i][j + 2])
                        && this.colorPawn.equals(game[i][j + 3])) {
                    return true;
                }
            }
        }

        // Check if player win by line
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j<=5; j++) {
                if (this.colorPawn.equals(game[i][j])
                        && this.colorPawn.equals(game[i + 1][j])
                        && this.colorPawn.equals(game[i + 2][j])
                        && this.colorPawn.equals(game[i + 3][j])) {
                    return true;
                }
            }
        }

        // Check if player win by right diagonal
        for (int i = 0; i<=3; i++) {
            for (int j = 3; j<=5; j++) {
                if (this.colorPawn.equals(game[i][j])
                        && this.colorPawn.equals(game[i + 1][j - 1])
                        && this.colorPawn.equals(game[i + 2][j - 2])
                        && this.colorPawn.equals(game[i + 3][j - 3])) {
                    return true;
                }
            }
        }

        // Check if player win by left diagonal
        for (int i = 3; i<=6; i++) {
            for (int j = 3; j<=5; j++) {
                if (this.colorPawn.equals(game[i][j])
                        && this.colorPawn.equals(game[i - 1][j - 1])
                        && this.colorPawn.equals(game[i - 2][j - 2])
                        && this.colorPawn.equals(game[i - 3][j - 3])) {
                    return true;
                }
            }
        }

        return false;
    }

}
