package com.bluetooth.connectFour.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Message;
import com.bluetooth.communicator.Peer;
import com.bluetooth.connectFour.BluetoothActivity;
import com.bluetooth.connectFour.Global;
import com.bluetooth.connectFour.R;
import com.bluetooth.connectFour.MainActivity;
import com.bluetooth.connectFour.adapters.GridAdapt;
import com.bluetooth.connectFour.tools.Constants;
import com.bluetooth.connectFour.tools.Player;

public class GameFragment extends Fragment {

    private GridAdapt grid;
    private GridView simpleGrid;
    private Player yellowPlayer;
    private Player redPlayer;
    private Player actualPlayer;
    private String colorPiece;
    private Global global;
    private BluetoothActivity activity;
    private TextView actualPlayerTxt;
    private BluetoothCommunicator.Callback communicatorCallback;
    private Chronometer simpleChronometer;
    private View globalView;

    public GameFragment() {
        //an empty constructor is always needed for fragments
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        communicatorCallback = new BluetoothCommunicator.Callback() {
            @Override
            public void onConnectionLost(Peer peer) {
                super.onConnectionLost(peer);
            }

            @Override
            public void onConnectionResumed(Peer peer) {
                super.onConnectionResumed(peer);
            }

            /** means that we have received a message containing TEXT, for know the sender we can call message.getSender() that return
            the peer that have sent the message, we can ignore source, it indicate only if we have received the message as clients or as servers
             * @param message the message we received
             * @param source the sender
             */
            @Override
            public void onMessageReceived(Message message, int source) {
                super.onMessageReceived(message, source);

                String resGame = grid.placePiece(Integer.parseInt(message.getText()), actualPlayer, actualPlayer.getColorPawn());
                actualPlayerTxt.setText("VOUS JOUEZ");
                if (resGame == Constants.IN_GAME) {
                    actualPlayer.decreaseRemainingPawn();
                    actualPlayer.getViewText().setText("Remaining Pawn :" + actualPlayer.getRemainingPawn());
                    actualPlayer = nextPlayer(actualPlayer);
                } else {
                    if(resGame == Constants.RES_LOOSE){
                        showPopup(globalView,"Vous avez gagné");
                    } else if (resGame == Constants.RES_WINS){
                        showPopup(globalView,"Vous avez perdu");
                    } else {
                        showPopup(globalView,"Egalité");
                    }
                }
            }

            /** means that the peer is disconnected
             * @param peer
             * @param peersLeft number of connected peers remained
             */
            @Override
            public void onDisconnected(Peer peer, int peersLeft) {
                super.onDisconnected(peer, peersLeft);

                if (peersLeft == 0) {
                    activity.setFragment(BluetoothActivity.DEFAULT_FRAGMENT);
                }
            }
        };

        //create the 2 players
        yellowPlayer = new Player("Y_P");
        redPlayer = new Player("R_P");

        //the red player always begin the game
        actualPlayer = redPlayer;

    }

    /** show a popup when the game finished, ask the player if he want to play against the same player or return to the main menu
     * also permit the player to inspect the grid when he press the cross
     * inform the player if he wins, lose or equality, show the chronometer
     * @param v
     * @param res
     */
    public void showPopup(View v, String res) {
        TextView txtclose;
        TextView txtRes;
        TextView txtChrono;

        Button butHome;
        Button butReplay;

        simpleChronometer.stop();

        final View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.result_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        txtclose = (TextView) popupView.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        txtRes = (TextView) popupView.findViewById(R.id.result);
        txtRes.setText(res);

        //get the time of the chrono
        txtChrono = (TextView) popupView.findViewById(R.id.chrono);
        txtChrono.setText(simpleChronometer.getText());

        //return to the main page
        butHome = (Button) popupView.findViewById(R.id.Accueil);
        butHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.exitFromConversation();
                popupWindow.dismiss();
                Intent intent_home = new Intent(getContext(), MainActivity.class);
                startActivityForResult(intent_home, 48);

            }
        });

        //restart the game
        butReplay = (Button) popupView.findViewById(R.id.Reset);
        butReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setFragment(activity.GAME_FRAGMENT);
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(popupView, 0, 0);
    }

    /** pass to the next player
     * @param player the actual player playing
     * @return the new player who is start to play
     */
    public Player nextPlayer(Player player){
        if(player == this.redPlayer)
            return this.yellowPlayer;
        else
            return this.redPlayer;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_jeu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actualPlayerTxt = view.findViewById(R.id.actualPlayer);
        globalView = view;
        // get and start the chronometer
        simpleChronometer = view.findViewById(R.id.simpleChronometer);
        simpleChronometer.start();

        simpleGrid = view.findViewById(R.id.gridView);

        //get and display the remaining pawns of each players
        redPlayer.setViewText((TextView) view.findViewById(R.id.RedPawn));
        redPlayer.getViewText().setText("Remaining Pawn :" + redPlayer.getRemainingPawn());

        yellowPlayer.setViewText((TextView) view.findViewById(R.id.YellowPawn));
        yellowPlayer.getViewText().setText("Remaining Pawn :" + yellowPlayer.getRemainingPawn());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (BluetoothActivity) requireActivity();
        global = (Global) activity.getApplication();

        //get to color of the pawn of the App's player
        this.colorPiece = activity.getColorPawn();
        if(this.colorPiece == null){
            this.onDestroy();
        }
        //display the player who start the game
        actualPlayerTxt.setText(activity.getTxtActualPlayer());
        grid = new GridAdapt(getContext());
        simpleGrid.setAdapter(grid);
        //set a click listener on each box if the grid
        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(actualPlayer.getColorPawn() == colorPiece) {

                    String resGame = grid.placePiece(position, actualPlayer, colorPiece);

                    if (global.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
                        //sending a message in which bow the player played
                        Message message = new Message(global, "m", String.valueOf(position), global.getBluetoothCommunicator().getConnectedPeersList().get(0));
                        global.getBluetoothCommunicator().sendMessage(message);

                        actualPlayer.decreaseRemainingPawn(); //decrease the number of remaining pawns
                        actualPlayer.getViewText().setText("Remaining Pawn :" + actualPlayer.getRemainingPawn()); //display the new number of pawns
                        actualPlayer = nextPlayer(actualPlayer);
                    }
                    actualPlayerTxt.setText("VOTRE ADVERSAIRE JOUE");
                    //check if someone wins or if the players can't play anymore
                    if(resGame == Constants.RES_LOOSE){
                        showPopup(view,"Vous avez perdu");
                    } else if (resGame == Constants.RES_WINS){
                        showPopup(view,"Vous avez gagné");
                    } else if (resGame == Constants.RES_EQUAL) {
                        showPopup(view,"Egalité");
                    }
                } else { //if the player try to play when it's not his turn, display that it's the opponent's turn to play
                   Toast.makeText(activity, "C'est au tour de l'adversaire", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        global.getBluetoothCommunicator().addCallback(communicatorCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        global.getBluetoothCommunicator().removeCallback(communicatorCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
