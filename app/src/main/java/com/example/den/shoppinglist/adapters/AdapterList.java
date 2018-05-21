package com.example.den.shoppinglist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.den.shoppinglist.R;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.OnItemListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder> {
    private LayoutInflater inflater;    // для загрузки разметки элемента
    private List<Lists> list;    // коллекция выводимых данных
    private OnItemListener OnItemListener;

    public AdapterList(Context context, List<Lists> list, OnItemListener OnItemListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.OnItemListener = OnItemListener;
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
        @BindView(R.id.container)
        ConstraintLayout constraintLayout;

        // в конструкторе получаем ссылки на элементы по id
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
        holder.category_id.setText(list.get(position).getListName());
    }//onBindViewHolder

    public void deleteFromListAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//обновляет после удаления Item на позиции position
        notifyItemRangeChanged(pos, list.size());//обновляет позиции последующих элементов
    }//deleteFromListAdapter
}//class Adapter