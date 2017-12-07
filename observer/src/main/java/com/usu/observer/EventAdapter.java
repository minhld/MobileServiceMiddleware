package com.usu.observer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by minhld on 12/7/2017.
 */

public class EventAdapter extends ArrayAdapter<MainActivity.Event> {
    public EventAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return null;
    }
}