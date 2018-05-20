package com.example.den.shoppinglist.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.den.shoppinglist.BigPhotoFragment;
import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterProductListPurchased extends RecyclerView.Adapter<AdapterProductListPurchased.ViewHolder> {
    private LayoutInflater inflater;    // для загрузки разметки элемента
    private List<Product> list;    // коллекция выводимых данных
    private Context context;
    private FragmentManager fm;
    private static ClickListener clickListener;

    public AdapterProductListPurchased(Context context, List<Product> list, FragmentManager fm) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
        this.fm = fm;
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

    //внутрений класс ViewHolder для хранения элементов разметки
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.container)
        ConstraintLayout constraintLayout;

        // в конструкторе получаем ссылки на элементы по id
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }//ViewHolder

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }//class ViewHolder

    public void setOnItemClickListener(ClickListener clickListener) {
        AdapterProductListPurchased.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }


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
            if (list.get(position).getCamera() == 2) {
                uri = Uri.fromFile(new File(finalPath));
                Glide.with(context)
                        .load(uri)
                        .override(80, 80)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);
            }
            if (list.get(position).getCamera() == 1) {
                uri = Uri.parse(finalPath);
                Glide.with(context)
                        .load(uri)
                        .override(80, 80)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher_round)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);
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
        }
    }//onBindViewHolder

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//обновляет после удаления Item на позиции position
        notifyItemRangeChanged(pos, list.size());//обновляет позиции последующих элементов
    }//deleteFromListAdapter
}//class AdapterProductListPurchased