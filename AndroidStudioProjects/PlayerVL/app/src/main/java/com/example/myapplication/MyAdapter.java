package com.example.myapplication;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.playlist.PlaylistData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
    private static String plaName;
    private final List<Channel> mDataset; // список данных, которые будут отображаться в RecyclerView
    private final OnStateClickListener onClickListener;
    private Integer selectedPosition = RecyclerView.NO_POSITION;
    private Channel channel;
    private int like;

    // Конструктор класса MyAdapter, который получает список данных и сохраняет его в приватной переменной mDataset
    public MyAdapter(List<Channel> myDataset, OnStateClickListener onClickListener) {
        this.mDataset = myDataset;
        this.onClickListener = onClickListener;

    }

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycleview_channel, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {


        AppDateBase db = AppDateBase.getInstance(holder.itemView.getContext());
        channel = mDataset.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PlaylistData playlist = db.playlistDAO().getById(mDataset.get(position).getPlaylist_id());
                like = db.channelEntityDAO().isActive(channel.getId());
                plaName = playlist.getName();
            }
        }).start();


        // Установка имя канала
        holder.mTextView.setText(mDataset.get(position).getNameChannel());
        // Установка Logo канала через библиотек Glade
        Glide.with(holder.itemView.getContext())
                .load(mDataset.get(position).getUrlLogo())
                .into(holder.mImageView);
        holder.mTextViewGroup.setText(mDataset.get(position).getGroupChannel() + "\n" + plaName);
        channel.setLike(like);
        //holder.imageViewDivider.setImageResource(R.drawable.list_item_selector);


        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.list_item_selector);
        } else {
            holder.itemView.setBackgroundResource(com.google.android.material.R.color.background_floating_material_dark);
        }



        // Слушатель на click, возвращает объект и номер позиции
        Channel channel = mDataset.get(position);
        holder.itemView.setOnClickListener(v -> {

            try {
                // Выполняем действие при нажатии на item
                onClickListener.onStateClick(channel, position);
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                setSelectedItemPosition(holder.getAdapterPosition());

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);

            }
            notifyItemChanged(position);
        });


        holder.btnStarLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable.fromCallable(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                Boolean result = false;
                                Log.d("LickeResul", String.valueOf(db.channelEntityDAO().isActive(mDataset.get(position).getId())));
                                if (db.channelEntityDAO().isActive(channel.getId())==1) {
                                    db.channelEntityDAO().updateLike(channel.getId(), 0 );

                                    result = true;
                                }else{
                                    db.channelEntityDAO().updateLike(channel.getId(), 1 );
                                    result = false;
                                }
                                return result;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean result) throws Throwable {

                                if (result == true){
                                    holder.btnStarLike.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.star_empt));
                                }else {
                                    holder.btnStarLike.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.star_full));
                                }
                                //Log.d("LickeResul", String.valueOf(result));
                            }
                        });

            }
        });
    }

    public void setSelectedItemPosition(int position) {
        if (selectedPosition != position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    /**
     * метод onItemMove() служит для перемещения строк в recyclerView
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    // getItemCount возвращает общее количество элементов в списке данных.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    interface OnStateClickListener {
        void onStateClick(Channel channel, int position) throws ExecutionException, InterruptedException;
    }

    // MyViewHolder описывает элемент RecyclerView и содержит ссылки на его элементы.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public RecyclerView recyclerView;
        public TextView mTextViewGroup;
        public ImageView imageViewDivider;
        public ImageButton btnStarLike;
        public LinearLayout linearLayout;

        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.title_text_view);
            mImageView = v.findViewById(R.id.image_view);
            recyclerView = v.findViewById(R.id.recycler_view);
            mTextViewGroup = v.findViewById(R.id.group_text_view);
            btnStarLike = v.findViewById(R.id.btn_star_like);
            linearLayout = v.findViewById(R.id.layout_recyclerview_channel);
            //imageViewDivider = v.findViewById(R.id.imageViewDivifer);

        }

    }
}
