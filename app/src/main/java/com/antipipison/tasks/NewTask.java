package com.antipipison.tasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NewTask extends AppCompatActivity {

    private TasksRepository _taskTasksRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        _taskTasksRepository = new TasksRepository(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            EditText editText = findViewById(R.id.editText);
            String name = editText.getText().toString();

            EditText intervalEditText = findViewById(R.id.intervalEditText);
            String intervalString = intervalEditText.getText().toString();
            Integer interval = Integer.parseInt(intervalString);

            _taskTasksRepository.add(name, 60*interval); // convert to second

            super.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
