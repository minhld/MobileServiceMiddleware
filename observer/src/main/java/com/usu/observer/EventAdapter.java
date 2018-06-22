package com.usu.observer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by minhld on 12/7/2017.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    Context context;
    LayoutInflater inflater;

    public EventAdapter(Context context) {
        super(context, R.layout.row_event);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public Event getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.row_event, parent, false);
        }

        Event e = getItem(position);

        ImageView imgView = (ImageView) view.findViewById(R.id.bgImage);
        imgView.setOnClickListener(new DetailClick(e));
        Picasso.with(context).load(e.info).fit().into(imgView);

        TextView type = (TextView) view.findViewById(R.id.typeText);
        type.setText(e.type);

        TextView time = (TextView) view.findViewById(R.id.timeText);
        time.setText(e.time);

        return view;
    }

    class DetailClick implements View.OnClickListener {
        Event e;
        public DetailClick(Event e) {
            this.e = e;
        }

        @Override
        public void onClick(View v) {
            Intent zoomAct = new Intent(context, ZoomActivity.class);
            zoomAct.putExtra("file", e.info);
            context.startActivity(zoomAct);
        }
    }
}