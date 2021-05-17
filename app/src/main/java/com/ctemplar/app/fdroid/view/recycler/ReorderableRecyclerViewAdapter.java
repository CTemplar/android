package com.ctemplar.app.fdroid.view.recycler;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ReorderableRecyclerViewAdapter<VH extends ReorderableRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final ItemTouchHelper itemTouchHelper;

    public ReorderableRecyclerViewAdapter() {
        ReorderableRecyclerViewItemTouchCallback touchCallback = new ReorderableRecyclerViewItemTouchCallback(this);
        itemTouchHelper = new ItemTouchHelper(touchCallback);
    }

    public abstract void onItemMove(int oldPosition, int newPosition);

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(null);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        private final ReorderableRecyclerViewAdapter<? extends ViewHolder> adapter;

        public ViewHolder(@NonNull View itemView, ReorderableRecyclerViewAdapter<? extends ViewHolder> adapter) {
            super(itemView);
            this.adapter = adapter;
        }

        protected void onSelected() {

        }

        protected void onDeselected() {

        }

        protected void setDraggableView(View view) {
            view.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    adapter.itemTouchHelper.startDrag(this);
                }
                return false;
            });
        }
    }
}
