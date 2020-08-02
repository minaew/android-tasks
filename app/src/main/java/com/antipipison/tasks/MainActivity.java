package com.antipipison.tasks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    private NotificationManagerCompat _notificationManager;

    @Override
    /**
     * Initialization
     */
    protected void onCreate(Bundle savedInstanceState) {

        // UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // data
        _tasksRepository = new TasksRepository(this);

        // repeated task
        _handler = new Handler();
        _handler.post(timeUpdaterRunnable);

        // notifications
        createNotificationChannel();
        _notificationManager = NotificationManagerCompat.from(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ass","name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("decription");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

        // show notification for each expired task

        for (Task task : expiredTasks) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ass")
                    .setSmallIcon(R.drawable.ic_add_circle_outline_white_24dp)
                    .setContentTitle("Task expired")
                    .setContentText(task.name)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            _notificationManager.notify(task.name.hashCode(), builder.build());
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
