package com.example.den.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.den.shoppinglist.adapters.AdapterList;
import com.example.den.shoppinglist.dialogs.AddEditListDialog;
import com.example.den.shoppinglist.dialogs.DeleteListDialog;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackLists;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DeleteListInterface, AddEditListInterface, DatabaseCallbackLists {
    private List<Lists> list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    private AdapterList adapter;
    private int positionDelete = -1;
    private RequestsLists requestsLists;
    private int idList;
    private List<ProductForList> sameIdList;
    private List<ProductForList> inOtherLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        requestsLists = App.app().getComponent().getRequestsLists();
        App.app().getListComponent().inject(requestsLists);
        setSupportActionBar(toolbar);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditListDialog addEditListDialog = new AddEditListDialog();
                addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
            }
        });

        requestsLists.getLists(this);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    // код для клика по элементу
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, Products.class);
                        idList = list.get(position).getListId();
                        String nameList = list.get(position).getListName();
                        intent.putExtra("nameList", nameList);
                        intent.putExtra("idList", idList);
                        startActivity(intent);
                    }//onItemClick

                    //длинное нажатие по элементу
                    @Override
                    public void onLongItemClick(View view, final int position) {
                        PopupMenu popup = new PopupMenu(view.getContext(), view, Gravity.CENTER);//создаем объект окна меню
                        popup.inflate(R.menu.context_menu);//закачиваем меню из XML файла
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {//определяем нажатия на элементы меню
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Lists lists = list.get(position);
                                Bundle args = new Bundle();
                                args.putParcelable("lists", lists);
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        AddEditListDialog addEditListDialog = new AddEditListDialog();
                                        addEditListDialog.setArguments(args);
                                        addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
                                        return true;
                                    case R.id.delete:
                                        positionDelete = position;
                                        DeleteListDialog deleteDialog = new DeleteListDialog();
                                        deleteDialog.setArguments(args);
                                        deleteDialog.show(getSupportFragmentManager(), "deleteList");
                                        return true;
                                    default:
                                        break;
                                }//switch
                                return false;
                            }//onMenuItemClick
                        });
                        popup.show();//показываем окно меню
                    }//onLongItemClick
                })//RecyclerItemClickListener
        );
    }

    @Override
    public void addList(final Lists lists) {
        requestsLists.addLists(this, lists);
    }

    @Override
    public void update(Lists lists) {
        requestsLists.updateLists(this, lists);
    }

    @Override
    public void deleteList(Lists lists) {
        requestsLists.deleteList(this, lists);
    }

    //==============================================================================================
    @Override
    public void onListsLoaded(List<Lists> lists) {
        list = lists;
        if (adapter == null || positionDelete == -1) {
            adapter = new AdapterList(MainActivity.this, list);//адаптер для ресайклера
            recyclerView.setAdapter(adapter);//подсоединяем адаптер к ресайклеру
        } else {
            adapter.deleteFromListAdapter(positionDelete);
            positionDelete = -1;
        }
    }

    @Override
    public void onListDeleted() {
        //ищем в таблице "товары в списке" записи с таким же id СПИСКА
        requestsLists.getSameIdListForList(MainActivity.this, idList);
    }

    //нашли в таблице "товары в списке" все записи с таким же id СПИСКА
    @Override
    public void onSameIdList(List<ProductForList> list) {
        requestsLists.dispSameId.dispose();
        sameIdList = list;//все записи с таким же id СПИСКА
        //удаляем из таблици "товары в списке" записи с таким же id СПИСКА
        if (list.size() > 0) {
            requestsLists.deleteListProductForList(MainActivity.this, list);
        }
    }

    //удалили из таблици "товары в списке" все записи с таким же id СПИСКА
    @Override
    public void onDeletedListProductForList() {
        //получаем Список с повторяющимися в других стисках
        List<Integer> listIdProduct = new ArrayList<>();
        for (int i = 0; i < sameIdList.size(); i++) {
            listIdProduct.add(sameIdList.get(i).getIdProduct());
        }
        requestsLists.getInOtherLists(MainActivity.this, listIdProduct);
    }

    //получили Список с повторяющимися в других стисках
    @Override
    public void onInOtherLists(List<ProductForList> list) {
        requestsLists.dispInOtherLists.dispose();
        inOtherLists = list;//повторяющиеся в других стисках
        // создаем список c id товаров, которые надо удалить и удаляем
        List<Integer> listForDeleting = new ArrayList<>();//конечный список для удаления
        for (int i = 0; i < sameIdList.size(); i++) {
            if (!inOtherLists.contains(sameIdList.get(i)))
                listForDeleting.add(sameIdList.get(i).getIdProduct());
        }
        if (listForDeleting.size() > 0)
            requestsLists.deleteProductList(MainActivity.this, listForDeleting);

    }

    @Override
    public void onDeletedProductList() {
        Log.d("lll", "jjj");
    }

    @Override
    public void onListsAdded() {
    }

    @Override
    public void onDataNotAvailable() {
    }

    @Override
    public void onListsUpdated() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestsLists.dispos.dispose();//отписываем наблюдателя
    }
}
