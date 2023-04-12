package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChannelAdapterRecyclerView extends RecyclerView.Adapter<ChannelAdapterRecyclerView.ItemViewHolder> {

    private List<Channel> channels;
    private static OnClickListenerBtnLike onClickListenerBtnLike;
    private static OnClickListenerItem onClickListenerItem;

    public ChannelAdapterRecyclerView(List<Channel> channels,  OnClickListenerBtnLike onClickListenerBtnLike,
                                                               OnClickListenerItem onClickListenerItem ){

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

        holder.mTextView.setText(channels.get(position).getNameChannel());
        holder.mTextViewGroup.setText(channels.get(position).getGroupChannel());


        int like = channels.get(position).getLike();
        if(like == 0){
            holder.btnStarLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.star_empt));
        }else {
            holder.btnStarLike.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.star_full));
        }

        holder.btnStarLike.setTag(position);

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
                    int position = (int) view.getTag();
                    onClickListenerBtnLike.onClickBtnLike(position, btnStarLike);
                   // notifyItemChanged(position);

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
                    int position = getAdapterPosition();
                    onClickListenerItem.onClickItem(position);
                }
            });
        }
    }
}


