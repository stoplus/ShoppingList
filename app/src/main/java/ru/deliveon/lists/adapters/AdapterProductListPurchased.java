package ru.deliveon.lists.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.deliveon.lists.GlideApp;
import ru.deliveon.lists.R;
import ru.deliveon.lists.adapters.recyclerHelper.ItemTouchHelperAdapter;
import ru.deliveon.lists.adapters.recyclerHelper.ItemTouchHelperViewHolder;
import ru.deliveon.lists.adapters.recyclerHelper.OnStartDragListener;
import ru.deliveon.lists.dialogs.BigPhotoFragment;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.interfaces.OnItemListener;

public class AdapterProductListPurchased extends RecyclerView.Adapter<AdapterProductListPurchased.ViewHolder>
        implements ItemTouchHelperAdapter {
    private LayoutInflater inflater;
    private List<Product> list;
    private Context context;
    private FragmentManager fm;
    private OnItemListener onItemListener;
    private final OnStartDragListener mDragStartListener;

    public AdapterProductListPurchased(Context context, List<Product> list, FragmentManager fm, OnItemListener onItemListener, OnStartDragListener mDragStartListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
        this.fm = fm;
        this.onItemListener = onItemListener;
        this.mDragStartListener = mDragStartListener;
    }//AdapterProductListPurchased

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
        View view = inflater.inflate(R.layout.item_product, parent, false);
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
        onItemListener.onRemoveItemPurchase(position, null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @BindView(R.id.textView) TextView textView;
        @BindView(R.id.imageView) ImageView imageView;
        @BindView(R.id.move) ImageView move;
        @BindView(R.id.container) ConstraintLayout constraintLayout;

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
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBotomDrag));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBotom));
        }
    }//class ViewHolder

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // связать отображаемые элементы и значения полей
        holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBotom));
        holder.textView.setText(list.get(position).nameProduct);
        holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.textView.setTextColor(context.getResources().getColor(R.color.colorTextShadowed));

        if (!list.get(position).getPictureLink().isEmpty()) {
            final String finalPath = list.get(position).getPictureLink();
            Uri uri = Uri.parse(finalPath);
            GlideApp.with(context)
                    .load(uri)
                    .override(80, 80)
                    .centerCrop()
                    .error(R.drawable.no_photo)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(holder.imageView);

            View.OnClickListener listener = v -> {
                BigPhotoFragment bigPhotoFragment = new BigPhotoFragment();
                Bundle args = new Bundle();
                args.putString("url", finalPath);
                bigPhotoFragment.setArguments(args);
                bigPhotoFragment.show(fm, "bigPhotoFragment");
            };
            holder.imageView.setOnClickListener(listener);
        }//if

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
}//class AdapterProductListPurchased
