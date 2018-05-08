package com.example.den.shoppinglist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterProductList extends RecyclerView.Adapter<AdapterProductList.ViewHolder> {
    private LayoutInflater inflater;    // для загрузки разметки элемента
    private List<Product> list;    // коллекция выводимых данных
    private String resourceType;//данные тега из файлаов XML (для разных экранов разные адаптеры)
    private Context context;

    public AdapterProductList(Context context, List<Product> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
    }//AdapterProductList

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
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.imageView)
        ImageView imageView;

        // в конструкторе получаем ссылки на элементы по id
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // связать отображаемые элементы и значения полей
        holder.textView.setText(list.get(position).nameProduct);

        if (list.get(position).getPictureLink() != null) {
            Picasso.with(context)
                    .load(new File(list.get(position).getPictureLink()))
                    .resize(50, 50)
                    .error(R.mipmap.ic_launcher_round)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }//onSuccess

                        @Override
                        public void onError() {
                        }//onError
                    });
        }
    }//onBindViewHolder
    //=================================================================================================

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//обновляет после удаления Item на позиции position
        notifyItemRangeChanged(pos, list.size());//обновляет позиции последующих элементов
    }//deleteFromListAdapter

    //=================================================================================================
}//class AdapterProductList