package com.bluetooth.puissanceFour.tools;

import android.widget.TextView;

public class Player {

    private String id_bluetooth;
    private String color_piece;
    private int remainingPawn = 21;
    private TextView showRemainingPawn;

    public Player(String id, String jeton) {
        id_bluetooth = id;
        color_piece = jeton;
    }

    public String getColor_piece() {
        return color_piece;
    }

    public String getId_bluetooth() {
        return id_bluetooth;
    }

    public String getRemainingPawn(){
        return String.valueOf(remainingPawn);
    }
    public void setViewText(TextView textview){
        this.showRemainingPawn = textview;
    }

    public TextView getViewText(){
        return this.showRemainingPawn;
    }

    public void decreaseRemainingPawn(){
        remainingPawn -= 1;
    }

    public boolean playerWin(final String[][] game) {

        // Check if player win by column
        for (int i = 0; i<=6; i++) {
            for (int j = 0; j<=2; j++) {
                if (this.equals(game[i][j])
                        && this.toString().equals(game[i][j + 1])
                        && this.equals(game[i][j + 2])
                        && this.equals(game[i][j + 3])) {
                    return true;
                }
            }
        }

        // Check if player win by line
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j<=5; j++) {
                if (this.equals(game[i][j])
                        && this.equals(game[i + 1][j])
                        && this.equals(game[i + 2][j])
                        && this.equals(game[i + 3][j])) {
                    return true;
                }
            }
        }

        // Check if player win by right diagonal
        for (int i = 0; i<=3; i++) {
            for (int j = 3; j<=5; j++) {
                if (this.equals(game[i][j])
                        && this.equals(game[i + 1][j - 1])
                        && this.equals(game[i + 2][j - 2])
                        && this.equals(game[i + 3][j - 3])) {
                    return true;
                }
            }
        }

        // Check if player win by left diagonal
        for (int i = 3; i<=6; i++) {
            for (int j = 3; j<=5; j++) {
                if (this.equals(game[i][j])
                        && this.equals(game[i - 1][j - 1])
                        && this.equals(game[i - 2][j - 2])
                        && this.equals(game[i - 3][j - 3])) {
                    return true;
                }
            }
        }

        return false;
    }

}
