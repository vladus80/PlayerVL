package com.example.myapplication;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class ChannelAdapterRecyclerView extends RecyclerView.Adapter<ChannelAdapterRecyclerView.ItemViewHolder> implements ItemTouchHelperAdapter {

    private static OnClickListenerBtnLike onClickListenerBtnLike;
    private static OnClickListenerItem onClickListenerItem;
    private List<Channel> channels;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ChannelAdapterRecyclerView(List<Channel> channels, OnClickListenerBtnLike onClickListenerBtnLike,
                                      OnClickListenerItem onClickListenerItem) {

        this.channels = channels;
        ChannelAdapterRecyclerView.onClickListenerBtnLike = onClickListenerBtnLike;
        ChannelAdapterRecyclerView.onClickListenerItem = onClickListenerItem;

    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @NonNull
    @Override
    public ChannelAdapterRecyclerView.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holderView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycleview_channel, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load(channels.get(position).getUrlLogo())
                .into(holder.mImageView);


        //String nameGroup = channels.get(position).getPlaylist_id();

        String name = channels.get(position).getNameChannel();
        String group = channels.get(position).getGroupChannel();
        String playlistName = channels.get(position).getPlaylistName();
        holder.textViewNimberChan.setText(String.valueOf(position + 1));
        holder.mTextView.setText(name);
        holder.mTextViewGroup.setText(group + "\n" + playlistName);

        /* Устанваливаем звездочку при клике на звездочку*/
        int like = channels.get(position).getLike();
        if (like == 0) {
            holder.btnStarLike.setImageDrawable(ResourcesCompat
                    .getDrawable(holder.itemView.getResources(),
                            android.R.drawable.btn_star_big_off, null));
        } else {
            holder.btnStarLike.setImageDrawable(ResourcesCompat
                    .getDrawable(holder.itemView.getResources(),
                            android.R.drawable.btn_star_big_on, null));
        }


        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.rgb(62, 55, 55));
        }


    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(channels, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public int getItemCount() {
        return channels.size();
    }


    // Создаем ViewHolder
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public RecyclerView recyclerView;
        public TextView mTextViewGroup;
        public ImageButton btnStarLike;
        public LinearLayout linearLayout;
        public RecyclerView recyclerView_land;
        public TextView textViewNimberChan;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.title_text_view);
            mImageView = itemView.findViewById(R.id.image_view);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            mTextViewGroup = itemView.findViewById(R.id.group_text_view);
            btnStarLike = itemView.findViewById(R.id.btn_star_like);
            linearLayout = itemView.findViewById(R.id.layout_recyclerview_channel);
            textViewNimberChan = itemView.findViewById(R.id.textViewNimberChan);

            btnStarLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getBindingAdapterPosition();
                    onClickListenerBtnLike.onClickBtnLike(channels.get(position));

                    int like = channels.get(position).getLike();
                    if (like == 0) {
                        channels.get(position).setLike(1);
                    } else {
                        channels.get(position).setLike(0);
                    }
                    notifyItemChanged(position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int previousSelectedPosition = selectedPosition;
                    selectedPosition = getBindingAdapterPosition();
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);

                    if (onClickListenerItem != null) {
                        onClickListenerItem.onClickItem(selectedPosition);
                    }
                }

            });

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                View nextFocus = view.focusSearch(View.FOCUS_DOWN);

                                if (nextFocus != null) {
                                    clearSelections();
                                    nextFocus.requestFocus();
                                    nextFocus.setBackgroundColor(Color.BLACK);
                                    //notifyDataSetChanged();
                                    return true;
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                View prevFocus = view.focusSearch(View.FOCUS_UP);
                                if (prevFocus != null) {
                                    clearSelections();
                                    prevFocus.requestFocus();
                                    prevFocus.setBackgroundColor(Color.BLACK);

                                    return true;
                                }
                                //notifyDataSetChanged();
                                break;
                            case KeyEvent.KEYCODE_DPAD_RIGHT:

                                int posit = getBindingAdapterPosition();

                                /*Like-Dislike при нажатии кнопки на пульте*/
                                onClickListenerBtnLike.onClickBtnLike(channels.get(posit));

                                int like = channels.get(posit).getLike();
                                if (like == 0) {
                                    channels.get(posit).setLike(1);
                                } else {
                                    channels.get(posit).setLike(0);
                                }
                                notifyItemChanged(posit);
                                break;
                        }

                    }
                    return false;
                }
            });
        }


        private void clearSelections() {
            for (int i = 0; i < getItemCount(); i++) {
                itemView.setBackgroundColor(Color.rgb(62, 55, 55));
            }
        }
    }

}

// Toast.makeText(itemView.getContext(), "короткое нажатие", Toast.LENGTH_SHORT).show();

