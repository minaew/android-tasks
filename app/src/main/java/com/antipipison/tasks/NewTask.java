package com.antipipison.tasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;

public class NewTask extends AppCompatActivity {

    private NumberPicker _daysPicker;
    private NumberPicker _hoursPicker;
    private NumberPicker _minutesPicker;
    private TasksRepository _taskTasksRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        _minutesPicker = findViewById(R.id.minutesPicker);
        _minutesPicker.setMinValue(0);
        _minutesPicker.setMaxValue(59);

        _hoursPicker = findViewById(R.id.hoursPicker);
        _hoursPicker.setMinValue(0);
        _hoursPicker.setMaxValue(23);

        _daysPicker = findViewById(R.id.daysPicker);
        _daysPicker.setMinValue(0);
        _daysPicker.setMaxValue(30);

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

            int days = _daysPicker.getValue();
            int hours = _hoursPicker.getValue();
            int minutes = _minutesPicker.getValue();

            int totalSeconds = ((days*24 + hours)*60 + minutes)*60;
            _taskTasksRepository.add(name, totalSeconds);

            super.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
