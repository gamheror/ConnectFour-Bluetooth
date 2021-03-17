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

package com.bluetooth.connectFour.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluetooth.connectFour.R;
import com.bluetooth.communicator.Peer;
import com.bluetooth.connectFour.gui.CustomAnimator;

import java.util.ArrayList;

public class PeerListAdapter extends BaseAdapter {
    public static final int HOST = 1;
    private ArrayList<Peer> array;
    private LayoutInflater inflater;
    private Callback callback;
    private CustomAnimator animator = new CustomAnimator();
    private Activity activity;
    private boolean isClickable = true;
    private boolean showToast = false;

    public PeerListAdapter(Activity activity, ArrayList<Peer> array, Callback callback) {
        this.array = array;
        this.callback = callback;
        if (array.size() > 0) {
            callback.onFirstItemAdded();
        }
        this.activity = activity;
        notifyDataSetChanged();
        inflater = activity.getLayoutInflater();
    }

    /**
     * BaseAdapter method
     * How many items are in the data set represented by this Adapter.
     * @return
     */
    @Override
    public int getCount() {
        return array.size();
    }

    /**
     * BaseAdapter method
     * Get the data item associated with the specified position in the data set.
     * @return
     */
    @Override
    public Object getItem(int i) {
        return array.get(i);
    }

    /**
     * BaseAdapter method
     * Get the row id associated with the specified position in the list.
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * BaseAdapter method
     * Indicates whether the item ids are stable across changes to the underlying data.
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * BaseAdapter method
     * Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return HOST;
    }

    /**
     * BaseAdapter method
     * Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * BaseAdapter method
     * Get a View that displays the data at the specified position in the data set.
     * @param position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Object item = getItem(position);
        int itemType = getItemViewType(position);
        if (itemType == HOST) {
            Peer guiPeer = (Peer) item;
            String peerName = ((Peer) item).getName();
            if (view == null) {
                view = inflater.inflate(R.layout.component_row, null);
            }
            ((TextView) view.findViewById(R.id.textRow)).setText(peerName);

        }
        return view;
    }

    /**
     * Method which adds a new device to the array list
     * @param peer
     */
    public synchronized void add(Peer peer) {
        if (array.size() == 0) {
            callback.onFirstItemAdded();
        }
        array.add(peer);
        notifyDataSetChanged();
    }

    /**
     * Method which adds a device to a given index
     * @param index
     * @param item
     */
    public synchronized void set(int index, Peer item) {
        array.set(index, item);
        notifyDataSetChanged();
    }

    /**
     * Method that allows access to a device from the array list
     * @param i
     * @return
     */
    public Peer get(int i) {
        return array.get(i);
    }

    public int indexOf(Peer object) {
        return array.indexOf(object);
    }

    /**
     * Method used to retrieve the index of a device found with its name
     * @param uniqueName
     * @return index of the device
     */
    public int indexOfPeer(String uniqueName) {
        for (int i = 0; i < array.size(); i++) {
            Peer peer = array.get(i);
            String uniqueName1 = peer.getUniqueName();
            if (uniqueName1.length() > 0 && uniqueName1.equals(uniqueName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method used to remove a device from the list
     * @param peer
     */
    public synchronized void remove(Peer peer) {
        if (array.remove(peer)) {
            notifyDataSetChanged();
        }
        if (array.size() == 0) {
            // deleting the listview
            callback.onLastItemRemoved();
        }
    }

    /**
     * Method used to empty the list
     */
    public synchronized void clear() {
        array.clear();
        notifyDataSetChanged();
        if (array.size() == 0) {
            callback.onLastItemRemoved();
        }
    }

    /**
     * Method that returns a boolean to check if the element is clickable
     * @return
     */
    public boolean isClickable() {
        return isClickable;
    }

    /**
     * Method that allows write access to the isClickable boolean
     * @param clickable
     * @param showToast
     */
    public void setClickable(boolean clickable, boolean showToast) {
        this.isClickable = clickable;
        this.showToast = showToast;
    }

    public boolean getShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    public Callback getCallback() {
        return callback;
    }

    public static abstract class Callback {
        public void onFirstItemAdded() {
        }

        public void onLastItemRemoved() {
        }

        public void onClickNotAllowed(boolean showToast) {
        }
    }
}
