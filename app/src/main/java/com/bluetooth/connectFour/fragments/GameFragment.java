package com.bluetooth.connectFour.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "gamefragment";
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
                //Toast.makeText(this,"Connection lost, reconnecting...",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionResumed(Peer peer) {
                super.onConnectionResumed(peer);
                //Toast.makeText(this,"Connection resumed",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMessageReceived(Message message, int source) {
                super.onMessageReceived(message, source);
                /* means that we have received a message containing TEXT, for know the sender we can call message.getSender() that return
                the peer that have sent the message, we can ignore source, it indicate only if we have received the message
                as clients or as servers
                 */
                Log.i(TAG, message.getText());

                String resGame = grid.placePiece(Integer.parseInt(message.getText()), actualPlayer, actualPlayer.getColor_piece());
                actualPlayerTxt.setText("VOUS JOUEZ");
                if (resGame == Constants.IN_GAME) {
                    actualPlayer.decreaseRemainingPawn();
                    actualPlayer.getViewText().setText("Remaining Pawn :" + actualPlayer.getRemainingPawn());
                    actualPlayer = nextPlayer(actualPlayer);
                } else {
                    if(resGame == Constants.RES_LOOSE){
                        ShowPopup(globalView,"Vous avez gagné");
                    } else if (resGame == Constants.RES_WINS){
                        ShowPopup(globalView,"Vous avez perdu");
                    } else {
                        ShowPopup(globalView,"Egalité");
                    }
                }
            }

            @Override
            public void onDisconnected(Peer peer, int peersLeft) {
                super.onDisconnected(peer, peersLeft);
                /*means that the peer is disconnected, peersLeft indicate the number of connected peers remained
                 */
                if (peersLeft == 0) {
                    activity.setFragment(BluetoothActivity.DEFAULT_FRAGMENT);
                }
            }
        };

        yellowPlayer = new Player("Y_P");
        redPlayer = new Player("R_P");

        actualPlayer = redPlayer;

    }

    public void ShowPopup(View v, String res) {
        TextView txtclose;
        TextView txtRes;
        TextView txtChrono;

        Button butHome;
        Button butReplay;

        simpleChronometer.stop();

        final View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.result_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        txtclose = (TextView) popupView.findViewById(R.id.txtclose);

        txtRes = (TextView) popupView.findViewById(R.id.result);
        txtRes.setText(res);

        txtChrono = (TextView) popupView.findViewById(R.id.chrono);
        txtChrono.setText(simpleChronometer.getText());

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

        butReplay = (Button) popupView.findViewById(R.id.Reset);
        butReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setFragment(activity.GAME_FRAGMENT);
                popupWindow.dismiss();
            }
        });

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(popupView, 0, 0);
    }

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
        simpleChronometer = view.findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleGrid = view.findViewById(R.id.gridView);

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

        this.colorPiece = activity.getColorPiece();
        if(this.colorPiece == null){
            this.onDestroy();
        }
        actualPlayerTxt.setText(activity.getTxtActualPlayer());
        grid = new GridAdapt(getContext());
        simpleGrid.setAdapter(grid);

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(actualPlayer.getColor_piece() == colorPiece) {

                    String resGame = grid.placePiece(position, actualPlayer, colorPiece);

                    if (global.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
                        //sending message
                        Message message = new Message(global, "m", String.valueOf(position), global.getBluetoothCommunicator().getConnectedPeersList().get(0));
                        actualPlayer.decreaseRemainingPawn();
                        actualPlayer.getViewText().setText("Remaining Pawn :" + actualPlayer.getRemainingPawn());
                        global.getBluetoothCommunicator().sendMessage(message);

                        actualPlayer = nextPlayer(actualPlayer);
                    }
                    actualPlayerTxt.setText("VOTRE ADVERSAIRE JOUE");
                    if(resGame == Constants.RES_LOOSE){
                        ShowPopup(view,"Vous avez perdu");
                    } else if (resGame == Constants.RES_WINS){
                        ShowPopup(view,"Vous avez gagné");
                    } else if (resGame == Constants.RES_EQUAL) {
                        ShowPopup(view,"Egalité");
                    }

                } else {
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
