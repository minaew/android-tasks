package com.antipipison.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TasksRepository _tasksRepository; // for tasks retrieving
    private Handler _handler; // for repeated action

    @Override
    /**
     * Initialization
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _tasksRepository = new TasksRepository(this);


//        final Handler mHandler;
//        final long startTime;
//
//        startTime = System.currentTimeMillis();
//        mHandler = new Handler(){
//            public void handleMessage(Message msg){
//                super.handleMessage(msg);
//
//                long seconds = ((System.currentTimeMillis() - startTime)) / 1000;
//                Log.i("info", "seconds = " + seconds);
//
//                this.sendEmptyMessageDelayed(0, 1000);
//            }
//        };
//
//        mHandler.sendEmptyMessage(0);
        _handler = new Handler();
        // _handler.postDelayed(timeUpdaterRunnable, 100);
        _handler.post(timeUpdaterRunnable);
    }

    private Runnable timeUpdaterRunnable = new Runnable() {
        public void run() {
            Log.i("info", "updating ...");

            updateList();
            _handler.postDelayed(this, 1000);
        }
    };

    @Override
    /**
     * Start user interaction
     */
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_task) {
            Intent intent = new Intent(this, NewTask.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDeleteClick(View view) {

        String taskName = getTaskNameByButton(view);
        _tasksRepository.delete(taskName);
        updateList();

        View contextView = findViewById(R.id.tasks_header);
        Snackbar.make(contextView, taskName + " is deleted", Snackbar.LENGTH_LONG)
                // .setAction("Action", null) // TODO: add undone action
                .show();
    }

    private void updateList() {

        // get tasks and split into two lists
        List<Task> expiredTasks = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        for (Task task : _tasksRepository.getTasks()) {
            if (task.expired) {
                expiredTasks.add(task);
            } else {
                tasks.add(task);
            }
        }

        // bind each list to its own ListView

        ListAdapter arrayAdapter = new TaskArrayAdapter(this, tasks);
        ListView listView = findViewById(R.id.tasks_list);
        listView.setAdapter(arrayAdapter);

        ListAdapter expiredArrayAdapter = new TaskArrayAdapter(this, expiredTasks);
        ListView expiredListView = findViewById(R.id.expired_tasks_list);
        expiredListView.setAdapter(expiredArrayAdapter);

        // set headers visibility

        TextView expiredTextView = findViewById(R.id.expired_tasks_header);
        if (expiredTasks.size() > 0) {
            expiredTextView.setVisibility(View.VISIBLE);
        } else {
            expiredTextView.setVisibility(View.GONE);
        }

        TextView textView = findViewById(R.id.tasks_header);
        if (expiredTasks.size() > 0 && tasks.size() > 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String getTaskNameByButton(View view) {

        // FIXME: layout-specific code

        ImageButton deleteButton = (ImageButton) view;
        LinearLayout linearLayout = (LinearLayout)deleteButton.getParent();
        RelativeLayout relativeLayout = (RelativeLayout)linearLayout.getParent();
        TextView child = (TextView) relativeLayout.getChildAt(0);
        String taskName = child.getText().toString();

        return taskName;
    }

    public void onDoneClick(View view) {

        String taskName = getTaskNameByButton(view);
        _tasksRepository.markAsDone(taskName);
        updateList();

        View contextView = findViewById(R.id.tasks_header);
        Snackbar.make(contextView, taskName + " is performed", Snackbar.LENGTH_LONG)
                // .setAction("Action", null) // TODO: add undone action
                .show();
    }

    public void OnAddClicked(View view) {
        Intent intent = new Intent(this, NewTask.class);
        startActivity(intent);
    }
}
