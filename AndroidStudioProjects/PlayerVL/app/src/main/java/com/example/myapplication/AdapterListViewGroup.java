package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterListViewGroup extends BaseAdapter {

    private final Context mContext;
    private final List<ModelItemListViewGroup> mItems;

    public AdapterListViewGroup(Context context, List<ModelItemListViewGroup> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_listview_group, null);
        }

        TextView name = view.findViewById(R.id.item_name_group);
        TextView description = view.findViewById(R.id.item_count_group);

        ModelItemListViewGroup item = mItems.get(position);
        name.setText(item.getName());
        description.setText(String.valueOf(item.getDescription()));

        // установить слушатель на элемент списка
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // обработка клика на элементе списка
                Toast.makeText(mContext, "Клик на элементе " + name.getText(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(v.getContext(), MainActivity.class);
               // v.getContext().startActivity(intent);

            }
        });*/

        return  view;
    }
}
