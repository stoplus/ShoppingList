package ru.deliveon.lists.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.deliveon.lists.R;
import ru.deliveon.lists.adapters.recyclerHelper.ItemTouchHelperAdapter;
import ru.deliveon.lists.adapters.recyclerHelper.ItemTouchHelperViewHolder;
import ru.deliveon.lists.adapters.recyclerHelper.OnStartDragListener;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.mainList.OnItemListenerMain;

public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder> implements ItemTouchHelperAdapter {
    private LayoutInflater inflater;
    private List<Lists> list;
    private OnItemListenerMain onItemListener;
    private final OnStartDragListener mDragStartListener;

    public AdapterList(Context context, List<Lists> list, OnItemListenerMain onItemListener, OnStartDragListener mDragStartListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.onItemListener = onItemListener;
        this.mDragStartListener = mDragStartListener;
    }//AdapterForAdmin

    @Override
    public int getItemCount() {
        return list.size();
    }//getItemCount

    @Override
    public long getItemId(int position) {
        return position;
    }//getItemId

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        onItemListener.onCheckSortNum(list);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        onItemListener.onRemoveItem(position, null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @BindView(R.id.idTextViewLists)
        TextView category_id;
        @BindView(R.id.move)
        ImageView move;
        @BindView(R.id.container)
        ConstraintLayout constraintLayout;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            constraintLayout.setOnClickListener(v -> onItemListener.onItemClick(getAdapterPosition(), v, list));
            constraintLayout.setOnLongClickListener(v -> {
                onItemListener.onItemLongClick(getAdapterPosition(), v, list);
                return false;
            });
        }//ViewHolder

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorListDrag));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorList));
        }
    }//class ViewHolder

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.category_id.setText(list.get(position).getListName());
        // Start a drag whenever the handle view it touched
        holder.move.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder);
            } else {
                v.performClick();
            }
            return false;
        });
    }//onBindViewHolder

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//updates after removing Item at position
        notifyItemRangeChanged(pos, list.size());//updates the items of the following items
    }//deleteFromListAdapter
}//class Adapter