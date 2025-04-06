package com.example.lostfoundapp;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class LostFoundAdapter extends ArrayAdapter<LostFoundItem> {

    private final Context context;
    private final ArrayList<LostFoundItem> items;

    public LostFoundAdapter(Context context, ArrayList<LostFoundItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LostFoundItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_custom, parent, false);
        }

        TextView title = convertView.findViewById(R.id.textTitle);
        TextView subtitle = convertView.findViewById(R.id.textSubtitle);

        title.setText(item.getType()); // Display type
        subtitle.setText(item.getDescription()); // Display description

        return convertView;
    }
}
