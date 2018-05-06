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
import com.example.den.shoppinglist.entity.ListProduct;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterProductList extends RecyclerView.Adapter<AdapterProductList.ViewHolder> {
    private LayoutInflater inflater;    // для загрузки разметки элемента
    private List<ListProduct> list;    // коллекция выводимых данных
    private String resourceType;//данные тега из файлаов XML (для разных экранов разные адаптеры)
    private Context context;

    public AdapterProductList(Context context, List<ListProduct> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
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
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    //внутрений класс ViewHolder для хранения элементов разметки
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView) TextView textView;
        @BindView(R.id.imageView) ImageView imageView;
        @BindView(R.id.checkBox) CheckBox checkBox;

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
        holder.checkBox.setVisibility(View.GONE);
        if (!list.get(position).getPictureLink().isEmpty()){
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
    // Filter Class
    public void animateTo(List<ListProduct> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }//animateTo

    //удаляем лишние Items
    private void applyAndAnimateRemovals(List<ListProduct> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final ListProduct model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }//if
        }//for
    }//applyAndAnimateRemovals

    // добавляем Items
    private void applyAndAnimateAdditions(List<ListProduct> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ListProduct model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }//if
        }//for
    }//applyAndAnimateAdditions

    //присваиваем новые позиции
    private void applyAndAnimateMovedItems(List<ListProduct> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ListProduct model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }//if
        }//for
    }//applyAndAnimateMovedItems

    private void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);//обновляем ресайклер при удалении Item
    }//removeItem

    private void addItem(int position, ListProduct model) {
        list.add(position, model);
        notifyItemInserted(position);//обновляем ресайклер при вставке Item
    }//addItem

    private void moveItem(int fromPosition, int toPosition) {
        final ListProduct model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);//обновляем ресайклер при перемещении Items
    }//moveItem
}//class AdapterForAdmin