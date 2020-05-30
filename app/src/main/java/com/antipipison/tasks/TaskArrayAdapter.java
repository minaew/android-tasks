package com.antipipison.tasks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class TaskArrayAdapter extends BaseAdapter {

    private List<Task> _objects;
    private Context _context;

    public TaskArrayAdapter(Context context, List<Task> objects) {
        _context = context;
        _objects = objects;
    }

    @Override
    public int getCount() {
        return _objects.size();
    }

    @Override
    public Object getItem(int i) {
        return _objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        Task task = _objects.get(position);

        if (convertView == null) {
            convertView = View.inflate (_context, R.layout.task, null);
        }

        TextView tvName = convertView.findViewById(R.id.tvText);

        tvName.setText(task.name);
        if (task.expired) {
            tvName.setTextColor(0xFFFF0000); // red
        } else {
            tvName.setTextColor(0xFF000000); // black
        }

        TextView tvExpiring = convertView.findViewById(R.id.tvExpiringTime);
        tvExpiring.setText(task.expiringTime);

        return convertView;
    }
}
