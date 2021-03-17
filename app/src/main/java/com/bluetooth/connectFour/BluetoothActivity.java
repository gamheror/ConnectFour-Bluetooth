/*
 * Copyright 2016 Luca Martino.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copyFile of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluetooth.connectFour;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bluetooth.connectFour.fragments.GameFragment;
import com.bluetooth.connectFour.fragments.PairingFragment;
import com.bluetooth.connectFour.tools.Tools;
import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Peer;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    public static final int PAIRING_FRAGMENT = 0;
    public static final int GAME_FRAGMENT = 1;
    public static final int DEFAULT_FRAGMENT = PAIRING_FRAGMENT;
    public static final int NO_PERMISSIONS = -10;
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 2;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private Global global;
    private int currentFragment = -1;
    private ArrayList<Callback> clientsCallbacks = new ArrayList<>();
    private CoordinatorLayout fragmentContainer;
    private String colorPiece;
    private String txtActualBeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        global = (Global) getApplication();

        // Clean fragments (only if the app is recreated (When user disable permission))
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Remove previous fragments (case of the app was restarted after changed permission on android 6 and higher)
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        fragmentContainer = findViewById(R.id.fragment_container);

        global.getBluetoothCommunicator().addCallback(new BluetoothCommunicator.Callback() {
            @Override
            public void onAdvertiseStarted() {
                super.onAdvertiseStarted();
                if (global.getBluetoothCommunicator().isDiscovering()) {
                    notifySearchStarted();
                }
            }

            @Override
            public void onDiscoveryStarted() {
                super.onDiscoveryStarted();
                if (global.getBluetoothCommunicator().isAdvertising()) {
                    notifySearchStarted();
                }
            }

            @Override
            public void onAdvertiseStopped() {
                super.onAdvertiseStopped();
                if (!global.getBluetoothCommunicator().isDiscovering()) {
                    notifySearchStopped();
                }
            }

            @Override
            public void onDiscoveryStopped() {
                super.onDiscoveryStopped();
                if (!global.getBluetoothCommunicator().isAdvertising()) {
                    notifySearchStopped();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // when we return to the app's gui we choose which fragment to start based on connection status
        if (global.getBluetoothCommunicator().getConnectedPeersList().size() == 0) {
            setFragment(DEFAULT_FRAGMENT);
        } else {
            setFragment(GAME_FRAGMENT);
        }
    }

    /**
     * @param fragmentName, the fragment we want to set
     */
    public void setFragment(int fragmentName) {
        switch (fragmentName) {
            case PAIRING_FRAGMENT: {
                // possible setting of the fragment
                if (getCurrentFragment() != PAIRING_FRAGMENT) {
                    PairingFragment paringFragment = new PairingFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.replace(R.id.fragment_container, paringFragment);
                    transaction.commit();
                    currentFragment = PAIRING_FRAGMENT;
                }
                break;
            }
            case GAME_FRAGMENT: {
                GameFragment gameFragment = new GameFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.fragment_container, gameFragment);
                transaction.commit();
                currentFragment = GAME_FRAGMENT;

                break;
            }
        }
    }

    /** Get the current fragment
     * @return the actual fragment
     */
    public int getCurrentFragment() {
        if (currentFragment != -1) {
            return currentFragment;
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null) {
                if (currentFragment.getClass().equals(PairingFragment.class)) {
                    return PAIRING_FRAGMENT;
                }
                if (currentFragment.getClass().equals(GameFragment.class)) {
                    return GAME_FRAGMENT;
                }
            }
        }
        return -1;
    }

    /** set the color piece of the player running the app
     * @param s, the color of the pawns
     */
    public void setColorPiece(String s){
        this.colorPiece = s;
    }

    /**
     * @return the color of the piece of the player
     */
    public String getColorPiece(){
        if(this.colorPiece != null) {
            return this.colorPiece;
        }
        return null;
    }

    /** at the beginning set the txt that will be print to inform the player who will start to play
     * @param txt
     */
    public void setTxtActualPlayer(String txt){
        Log.i("wsh","set : " + txt);
        this.txtActualBeg = txt;
    }

    /**
     * @return the player who'll start to play
     */
    public String getTxtActualPlayer(){
        Log.i("wsh","get : " + this.txtActualBeg);
        if(this.txtActualBeg != null) {
            return this.txtActualBeg;
        }
        return null;
    }

    @Override
    /** quit to conversation between the 2 devices, if the player is in game, ask a confirmation
     */
    public void onBackPressed() {
        DialogInterface.OnClickListener confirmExitListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitFromConversation();
            }
        };
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            if (fragment instanceof GameFragment) {
                showConfirmExitDialog(confirmExitListener);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    /** quit to conversation between the 2 devices
     */
    public void exitFromConversation() {
        if (global.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            global.getBluetoothCommunicator().disconnectFromAll();
        } else {
            setFragment(DEFAULT_FRAGMENT);
        }
    }

    /**ask if the player really want to leave the conversation with an alert dialog
     * @param confirmListener
     */
    protected void showConfirmExitDialog(DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirm exit");
        builder.setPositiveButton(android.R.string.ok, confirmListener);
        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * @return the state of the search, error, success,not supported, no permissions ect...
     */
    public int startSearch() {
        if (global.getBluetoothCommunicator().isBluetoothLeSupported() == BluetoothCommunicator.SUCCESS) {
            if (Tools.hasPermissions(this, REQUIRED_PERMISSIONS)) {
                int advertisingCode = global.getBluetoothCommunicator().startAdvertising();
                int discoveringCode = global.getBluetoothCommunicator().startDiscovery();
                if (advertisingCode == discoveringCode) {
                    return advertisingCode;
                }
                if (advertisingCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED || discoveringCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED) {
                    return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
                }
                if (advertisingCode == BluetoothCommunicator.SUCCESS || discoveringCode == BluetoothCommunicator.SUCCESS) {
                    if (advertisingCode == BluetoothCommunicator.ALREADY_STARTED || discoveringCode == BluetoothCommunicator.ALREADY_STARTED) {
                        return BluetoothCommunicator.SUCCESS;
                    }
                }
                return BluetoothCommunicator.ERROR;
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
                return NO_PERMISSIONS;
            }
        } else {
            return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
        }
    }

    /** stop the search and try to restore the bluetooth status
     * @param tryRestoreBluetoothStatus, try to
     * @return the state of the stop of the search
     */
    public int stopSearch(boolean tryRestoreBluetoothStatus) {
        int advertisingCode = global.getBluetoothCommunicator().stopAdvertising(tryRestoreBluetoothStatus);
        int discoveringCode = global.getBluetoothCommunicator().stopDiscovery(tryRestoreBluetoothStatus);
        if (advertisingCode == discoveringCode) {
            return advertisingCode;
        }
        if (advertisingCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED || discoveringCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED) {
            return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
        }
        if (advertisingCode == BluetoothCommunicator.SUCCESS || discoveringCode == BluetoothCommunicator.SUCCESS) {
            if (advertisingCode == BluetoothCommunicator.ALREADY_STOPPED || discoveringCode == BluetoothCommunicator.ALREADY_STOPPED) {
                return BluetoothCommunicator.SUCCESS;
            }
        }
        return BluetoothCommunicator.ERROR;
    }

    /**
     * @return true if the bluetooth communicator is searching, else return false
     */
    public boolean isSearching() {
        return global.getBluetoothCommunicator().isAdvertising() && global.getBluetoothCommunicator().isDiscovering();
    }

    /** Connect the device with the Peer in paramater
     * @param peer
     */
    public void connect(Peer peer) {
        stopSearch(false);
        global.getBluetoothCommunicator().connect(peer);
    }

    /** when get a request for connection of the Peer, permit to accept it
     * @param peer
     */
    public void acceptConnection(Peer peer) {
        global.getBluetoothCommunicator().acceptConnection(peer);
    }

    /** when get a request for connection of the Peer, permit to reject it
     * @param peer
     */
    public void rejectConnection(Peer peer) {
        global.getBluetoothCommunicator().rejectConnection(peer);
    }

    /** permit to disconnect of the Peer
     * @param peer
     * @return true if the disconnection was a success
     */
    public int disconnect(Peer peer) {
        return global.getBluetoothCommunicator().disconnect(peer);
    }

    /**
     * @return the fragment container
     */
    public CoordinatorLayout getFragmentContainer() {
        return fragmentContainer;
    }

    /** in this way the listener will listen to both this activity and the communicatorexample
     * @param callback
     */
    public void addCallback(Callback callback) {
        global.getBluetoothCommunicator().addCallback(callback);
        clientsCallbacks.add(callback);
    }

    /** remove the callback wrapped to stop listening to the communicationexample
     * @param callback
     */
    public void removeCallback(Callback callback) {
        global.getBluetoothCommunicator().removeCallback(callback);
        clientsCallbacks.remove(callback);
    }

    /**
     * notify all the clientCallbacks that we start the searching
     */
    private void notifySearchStarted() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onSearchStarted();
        }
    }

    /**
     * notify all the clientCallbacks that we stop the searching
     */
    private void notifySearchStopped() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onSearchStopped();
        }
    }

    public static class Callback extends BluetoothCommunicator.Callback {
        public void onSearchStarted() {
        }

        public void onSearchStopped() {
        }

        public void onMissingSearchPermission() {
        }

        public void onSearchPermissionGranted() {
        }
    }
}