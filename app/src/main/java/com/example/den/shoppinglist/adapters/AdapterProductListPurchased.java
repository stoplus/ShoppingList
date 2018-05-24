package com.example.den.shoppinglist.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.den.shoppinglist.dialogs.BigPhotoFragment;
import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.interfaces.OnItemListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterProductListPurchased extends RecyclerView.Adapter<AdapterProductListPurchased.ViewHolder> {
    private LayoutInflater inflater;
    private List<Product> list;
    private Context context;
    private FragmentManager fm;
    private OnItemListener OnItemListener;

    public AdapterProductListPurchased(Context context, List<Product> list, FragmentManager fm, OnItemListener OnItemListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
        this.fm = fm;
        this.OnItemListener = OnItemListener;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.container)
        ConstraintLayout constraintLayout;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemListener.onItemClick(getAdapterPosition(), v);
                }
            });
            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OnItemListener.onItemLongClick(getAdapterPosition(), v);
                    return false;
                }
            });
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // связать отображаемые элементы и значения полей
        holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBotom));
        holder.textView.setText(list.get(position).nameProduct);
        holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.textView.setTextColor(context.getResources().getColor(R.color.colorTextShadowed));

        Uri uri = null;
        if (!list.get(position).getPictureLink().isEmpty()) {
            String finalPath = list.get(position).getPictureLink();

            switch (list.get(position).getCamera()){
                case 2:
                    uri = Uri.fromFile(new File(finalPath));
                    setPhoto(uri, holder);
                    break;
                case  1:
                    uri = Uri.parse(finalPath);
                    setPhoto(uri, holder);
                    break;
            }

            final String url = String.valueOf(uri);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BigPhotoFragment bigPhotoFragment = new BigPhotoFragment();
                    Bundle args = new Bundle();
                    args.putString("url", url);
                    bigPhotoFragment.setArguments(args);
                    bigPhotoFragment.show(fm, "bigPhotoFragment");
                }
            };
            holder.imageView.setOnClickListener(listener);
        }//if
    }//onBindViewHolder

    private void setPhoto(Uri uri, ViewHolder holder){
        Glide.with(context)
                .load(uri)
                .override(80, 80)
                .centerCrop()
                .error(R.mipmap.no_photo)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.imageView);
    }//setPhoto

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//updates after removing Item at position
        notifyItemRangeChanged(pos, list.size());//updates the items of the following items
    }//deleteFromListAdapter
}//class AdapterProductListPurchased
