package com.usu.observer;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefresh;
    private ListView mListView;
    private ProgressBar mLoadingBar;

    EventAdapter mEventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mListView = (ListView) findViewById(R.id.viewList);
        mLoadingBar = (ProgressBar) findViewById(R.id.loadingBar);

        addEventHandlers();
    }

    private void addEventHandlers() {
        // setup scroll-down refresh handler
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // reload the list
                new LoadList().execute();
            }
        });

        // event list view
        mEventAdapter = new EventAdapter(this);
        mListView.setAdapter(mEventAdapter);

        // reload data
        new LoadList().execute();
    }

    /**
     * loading class
     */
    class LoadList extends AsyncTask {
        List<Event> events;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            events = EventDb.getEvents(10, 0);
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mLoadingBar.setVisibility(View.INVISIBLE);
            mEventAdapter.clear();
            mEventAdapter.addAll(events);
            mEventAdapter.notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);
        }
    }
}
