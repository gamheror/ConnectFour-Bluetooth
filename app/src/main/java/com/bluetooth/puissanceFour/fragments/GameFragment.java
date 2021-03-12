package com.bluetooth.puissanceFour.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Message;
import com.bluetooth.communicator.Peer;
import com.bluetooth.puissanceFour.BluetoothActivity;
import com.bluetooth.puissanceFour.Global;
import com.bluetooth.puissanceFour.R;
import com.bluetooth.puissanceFour.adapters.GridAdapt;
import com.bluetooth.puissanceFour.tools.Player;



public class GameFragment extends Fragment {

    private ImageView selectedImage;
    private GridAdapt grid;
    private GridView simpleGrid;
    private Chronometer chronoPlay;
    private RecyclerView mRecyclerView;
    private ConstraintLayout constraintLayout;

    private Player yellowPlayer;
    private Player redPlayer;
    private Player actualPlayer;
    private String colorPiece;
    private Global global;
    private BluetoothActivity activity;
    private static final String TAG = "gamefragment";

    private BluetoothCommunicator.Callback communicatorCallback;

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
                Toast.makeText(activity, message.getText(), Toast.LENGTH_SHORT).show();
                grid.placePiece(Integer.parseInt(message.getText()), actualPlayer);
                actualPlayer.decreaseRemainingPawn();
                actualPlayer = nextPlayer(actualPlayer);
                Log.i(TAG, actualPlayer.getColor_piece());
                //smooth scroll
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

        yellowPlayer = new Player("12" , "Y_P");
        redPlayer = new Player("2" ,"R_P");

        actualPlayer = redPlayer;



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
        Chronometer simpleChronometer = activity.findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleGrid = view.findViewById(R.id.gridView);
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

        grid = new GridAdapt(getContext());
        simpleGrid.setAdapter(grid);
        // we give the constraint layout the information on the system measures (status bar etc.), which has the fragmentContainer,
        // because they are not passed to it if started with a Transaction and therefore it overlaps the status bar because it fitsSystemWindows does not work
        /*WindowInsets windowInsets = activity.getFragmentContainer().getRootWindowInsets();
        if (windowInsets != null) {
            constraintLayout.dispatchApplyWindowInsets(windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), 0));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);*/

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(actualPlayer.getColor_piece() == colorPiece) {
                    grid.placePiece(position, actualPlayer);
                    if (global.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
                        //sending message
                        Message message = new Message(global, "m", String.valueOf(position), global.getBluetoothCommunicator().getConnectedPeersList().get(0));
                        actualPlayer.decreaseRemainingPawn();
                        global.getBluetoothCommunicator().sendMessage(message);
                    }
                    actualPlayer = nextPlayer(actualPlayer);
                }
                else{
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
