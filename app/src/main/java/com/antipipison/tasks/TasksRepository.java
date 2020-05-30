package com.antipipison.tasks;

import android.content.ContentValues;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


class TasksRepository implements Closeable {

    private SQLiteDatabase _db;

    public TasksRepository(ContextWrapper context) {

        _db = context.openOrCreateDatabase("tasks.db", MODE_PRIVATE, null);
        // _db.execSQL("DROP TABLE tasks");
        // _db.execSQL("CREATE TABLE tasks (name VARCHAR(200), interval INT, last INT);");
    }

    public void add(String name, int interval) {

        ContentValues row = new ContentValues();
        row.put("name", name);
        row.put("interval", interval);
        long currentTime = System.currentTimeMillis() / 1000;
        row.put("last", currentTime);

        long id = _db.insert("tasks", null, row);
        if (id == -1) {
            throw new RuntimeException("error inserting row in table");
        }
    }

    public void delete(String name) {
        int number = _db.delete("tasks", "name=?", new String[] {name});
        if (number < 1) {
            throw new RuntimeException("no row is deleted");
        }
    }

    public List<Task> getTasks() {

        Cursor cursor = _db.rawQuery("SELECT rowid _id, name, interval, last FROM tasks", null);

        List<Task> names = new ArrayList<>();
        boolean s = cursor.moveToFirst();
        if (s) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);

                long interval = cursor.getLong(2);
                long last = cursor.getLong(3);

                long expiringTimeSeconds = last + interval;
                long seconds = expiringTimeSeconds % 60L;
                long minutes = expiringTimeSeconds % (60L*60L) / 60L;
                long hours = expiringTimeSeconds % (24L*60L*60L) / (60L*60L) + 3L; // FIXME
                hours = hours % 24L;

                String expiringTimeString = String.format("%02d", hours) + ":" +
                                            String.format("%02d", minutes) + ":" +
                                            String.format("%02d", seconds);

                long currentTime = System.currentTimeMillis() / 1000;
                boolean expired = currentTime > last + interval;

                Task task = new Task();
                task.name = name;
                task.expired = expired;
                task.expiringTime = expiringTimeString;

                names.add(task);
            }
        }
        cursor.close();
        return names;
    }

    public void markAsDone(String name) {

        ContentValues values = new ContentValues();
        values.put("last", System.currentTimeMillis() / 1000);
        int n = _db.update("tasks", values, "name = ?", new String[] {name});
        if (n < 1) {
            throw new RuntimeException("error updating row");
        }
    }

    @Override
    public void close() throws IOException {
        _db.close();
    }
}
