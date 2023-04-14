package com.example.myapplication;

import android.graphics.Color;
import android.provider.CalendarContract;
import android.util.Log;
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

    private List<Channel> channels;
    private static OnClickListenerBtnLike onClickListenerBtnLike;
    private static OnClickListenerItem onClickListenerItem;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ChannelAdapterRecyclerView(List<Channel> channels,  OnClickListenerBtnLike onClickListenerBtnLike,
                                                               OnClickListenerItem onClickListenerItem ){

        this.channels = channels;
        ChannelAdapterRecyclerView.onClickListenerBtnLike = onClickListenerBtnLike;
        ChannelAdapterRecyclerView.onClickListenerItem = onClickListenerItem;

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
        holder.mTextView.setText(name);
        holder.mTextViewGroup.setText(group + "\n" + playlistName);


        /* Устанваливаем звездочку при клике на звездочку*/
        int like = channels.get(position).getLike();
        if(like == 0){
            holder.btnStarLike.setImageDrawable(ResourcesCompat
                    .getDrawable(holder.itemView.getResources(),
                            R.drawable.star_empt, null));
        }else {
            holder.btnStarLike.setImageDrawable(ResourcesCompat
                    .getDrawable(holder.itemView.getResources(),
                            R.drawable.star_full, null));
        }


        /* Выделяем строку при нажатии */
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.rgb(53, 54,58));
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
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mImageView;
        public RecyclerView recyclerView;
        public TextView mTextViewGroup;
        public ImageView imageViewDivider;
        public ImageButton btnStarLike;
        public LinearLayout linearLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.title_text_view);
            mImageView = itemView.findViewById(R.id.image_view);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            mTextViewGroup = itemView.findViewById(R.id.group_text_view);
            btnStarLike = itemView.findViewById(R.id.btn_star_like);
            linearLayout = itemView.findViewById(R.id.layout_recyclerview_channel);


            btnStarLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int position = (int) view.getTag();
                    int position = getAdapterPosition();
                    onClickListenerBtnLike.onClickBtnLike(channels.get(position));

                    Log.d("ChanelAdapter", channels.get(position).toString());
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
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);

                    if (onClickListenerItem != null) {
                        onClickListenerItem.onClickItem(selectedPosition);
                    }

                }
            });

        }
    }

}


