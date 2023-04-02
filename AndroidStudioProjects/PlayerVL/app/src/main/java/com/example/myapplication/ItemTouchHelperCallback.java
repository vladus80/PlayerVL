package com.example.myapplication;


/** Класс ItemTouchHelperCallback расширяет ItemTouchHelper.Callback и имеет методы
isLongPressDragEnabled(), getMovementFlags(), onMove() и onSwiped().
Метод isLongPressDragEnabled() возвращает true, чтобы включить долгое нажатие для перемещения элементов.
Метод getMovementFlags() устанавливает флаги для перемещения элементов вверх и вниз.
Метод onMove() вызывается, когда элемент перемещается и вызывает метод onItemMove()
из ItemTouchHelperAdapter */

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter adapter;

    ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // not used in this example
    }
}