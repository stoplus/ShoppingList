package com.example.den.shoppinglist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.den.shoppinglist.dialogs.AddEditListDialog;
import com.example.den.shoppinglist.dialogs.DeleteListDialog;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;
import com.example.den.shoppinglist.interfaces.DatabaseCallback;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DeleteListInterface, AddEditListInterface, DatabaseCallback {
    private List<Lists> list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    private AdapterList adapter;
    private int positionDelete = -1;

    @Inject
    ListsDao listsDao;
    @Inject
    ListProductDao listProductDao;
    @Inject
    RequestsLists requestsLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.app().getComponent().inject(this);
        setSupportActionBar(toolbar);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditListDialog addEditListDialog = new AddEditListDialog();
                addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
            }
        });

        requestsLists.getUsers(this, listsDao);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    // код для клика по элементу
                    @Override
                    public void onItemClick(View view, int position) {
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
        requestsLists.addUser(this, lists, listsDao);
    }

    @Override
    public void update(Lists lists) {
        requestsLists.updateLists(this, lists, listsDao);
    }

    @Override
    public void deleteList(Lists lists) {
        requestsLists.deleteLists(this, lists, listsDao);
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
    public void onListsDeleted() {
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
}
