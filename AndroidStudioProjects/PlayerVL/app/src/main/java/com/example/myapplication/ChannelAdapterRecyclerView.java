package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private int activatedPosition = 0 ;

    private int count = 0;

    public ChannelAdapterRecyclerView(List<Channel> channels, OnClickListenerBtnLike onClickListenerBtnLike,
                                      OnClickListenerItem onClickListenerItem) {

        this.channels = channels;
        ChannelAdapterRecyclerView.onClickListenerBtnLike = onClickListenerBtnLike;
        ChannelAdapterRecyclerView.onClickListenerItem = onClickListenerItem;

    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChannelAdapterRecyclerView.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("ViewHolderCount", "Создано ViewHolder " + ++count);
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
        holder.mTextViewNameChannel.setText(name);
        holder.mTextViewGroup.setText(group + "\n" + playlistName);

        /* Устанавливаем звездочку при клике на звездочку*/
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


        int activated = channels.get(position).getActivated();
        if (activated == 1 ){

           // holder.itemView.setBackgroundColor(Color.CYAN);

            holder.mTextViewNameChannel.setTextColor(Color.GREEN);
            holder.itemView.setBackgroundColor(Color.DKGRAY);
            activatedPosition = holder.getBindingAdapterPosition();
        }else{
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.mTextViewNameChannel.setTextColor(Color.WHITE);
            //holder.itemView.setBackgroundColor(Color.BLUE);
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
        public TextView mTextViewNameChannel;
        public ImageView mImageView;
        public RecyclerView recyclerView;
        public TextView mTextViewGroup;
        public ImageButton btnStarLike;
        public LinearLayout linearLayout;
        public TextView textViewNimberChan;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewNameChannel = itemView.findViewById(R.id.title_text_view);
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

                    int prevPosition = activatedPosition;          // активная позиция при загрузке
                    int curPosition = getBindingAdapterPosition(); // Текущая позиция при клике

                    onClickListenerItem.onClickItem(curPosition);
                    onClickListenerItem.onClickItem(channels.get(curPosition));
                    onClickListenerItem.getPosition(getBindingAdapterPosition());
                    int activated = channels.get(curPosition).getActivated();  // Узнаем является ли активным

                    if(channels.size()< prevPosition){prevPosition = 0;}


                    if (activated == 0) {
                        channels.get(curPosition).setActivated(1);

                        if(channels.get(prevPosition) != null){
                            channels.get(prevPosition).setActivated(0);
                        }

                    }
                    notifyItemChanged(curPosition);
                    notifyItemChanged(prevPosition);
                }

            });

            itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    view.requestFocus();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int posit = getBindingAdapterPosition();
                    onClickListenerBtnLike.onClickBtnLike(channels.get(posit));

                    int like = channels.get(posit).getLike();
                    if (like == 0) {
                        channels.get(posit).setLike(1);
                        Toast.makeText(view.getContext(), view.getContext().
                                getString(R.string.channel) + channels.get(posit).getNameChannel()
                                + " добавлен в избранное", Toast.LENGTH_SHORT).show();
                    } else {
                        channels.get(posit).setLike(0);
                        Toast.makeText(view.getContext(), view.getContext().
                                        getString(R.string.channel) + channels.get(posit).
                                        getNameChannel() + " удален из избранного",
                                Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(posit);
                    return true;
                }
            });

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                View nextFocus = view.focusSearch(View.FOCUS_DOWN);
                                View curFocus  = view.findFocus();

                                if (nextFocus != null) {
                                    //clearSelections();

                                    int position = getBindingAdapterPosition();
                                    nextFocus.requestFocus();
                                   // nextFocus.setBackgroundColor(Color.BLACK);
                                   // curFocus.setBackgroundColor(Color.rgb(62, 55, 55));
                                    //notifyItemChanged(position);

                                    return true;
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                View prevFocus = view.focusSearch(View.FOCUS_UP);
                                View curFocus2  = view.findFocus();

                                if (prevFocus != null) {
                                    //clearSelections();
                                    int position = getBindingAdapterPosition();


                                    prevFocus.requestFocus();
                                    //prevFocus.setBackgroundColor(Color.BLACK);
                                    //curFocus2.setBackgroundColor(Color.rgb(62, 55, 55));
                                   // notifyItemChanged(position);
                                    return true;
                                }
                                //notifyDataSetChanged();
                                break;
                            case KeyEvent.KEYCODE_DPAD_RIGHT:

//                                int posit = getBindingAdapterPosition();
//
//                                /*Like-Dislike при нажатии кнопки на пульте*/
//                                onClickListenerBtnLike.onClickBtnLike(channels.get(posit));
//
//                                int like = channels.get(posit).getLike();
//                                if (like == 0) {
//                                    channels.get(posit).setLike(1);
//                                } else {
//                                    channels.get(posit).setLike(0);
//                                }
//                                notifyItemChanged(posit);
                                break;
                        }

                    }
                    return false;
                }
            });

            int[][] states = new int[][] {

                    new int[] { android.R.attr.state_focused },
                    new int[] { android.R.attr.state_enabled}
            };

            int[] colors = new int[] {
                    Color.BLACK, // цвет при получении фокуса
                    Color.DKGRAY // цвет в обычном состоянии
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);
            itemView.setBackgroundTintList(colorStateList);

        }

    }

}


