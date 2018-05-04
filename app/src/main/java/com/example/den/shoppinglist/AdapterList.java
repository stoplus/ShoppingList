package com.example.den.shoppinglist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.den.shoppinglist.entity.Lists;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder> {
    private LayoutInflater inflater;    // для загрузки разметки элемента
    private List<Lists> list;    // коллекция выводимых данных
    private String resourceType;//данные тега из файлаов XML (для разных экранов разные адаптеры)

    public AdapterList(Context context, List<Lists> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
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

    //внутрений класс ViewHolder для хранения элементов разметки
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.idTextViewLists)
        TextView category_id;

        // в конструкторе получаем ссылки на элементы по id
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // связать отображаемые элементы и значения полей
        holder.category_id.setText(list.get(position).getListName());
    }//onBindViewHolder
    //=================================================================================================

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//обновляет после удаления Item на позиции position
        notifyItemRangeChanged(pos, list.size());//обновляет позиции последующих элементов
    }//deleteFromListAdapter

    //=================================================================================================
    // Filter Class
    public void animateTo(List<Lists> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }//animateTo

    //удаляем лишние Items
    private void applyAndAnimateRemovals(List<Lists> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final Lists model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }//if
        }//for
    }//applyAndAnimateRemovals

    // добавляем Items
    private void applyAndAnimateAdditions(List<Lists> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Lists model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }//if
        }//for
    }//applyAndAnimateAdditions

    //присваиваем новые позиции
    private void applyAndAnimateMovedItems(List<Lists> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Lists model = newModels.get(toPosition);
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

    private void addItem(int position, Lists model) {
        list.add(position, model);
        notifyItemInserted(position);//обновляем ресайклер при вставке Item
    }//addItem

    private void moveItem(int fromPosition, int toPosition) {
        final Lists model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);//обновляем ресайклер при перемещении Items
    }//moveItem
}//class AdapterForAdmin