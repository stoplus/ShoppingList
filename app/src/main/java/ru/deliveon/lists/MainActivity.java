package ru.deliveon.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.deliveon.lists.adapters.AdapterList;
import ru.deliveon.lists.dialogs.AddEditListDialog;
import ru.deliveon.lists.dialogs.DeleteListDialog;
import ru.deliveon.lists.dialogs.ExitDialog;
import ru.deliveon.lists.entity.Lists;
import ru.deliveon.lists.entity.ProductForList;
import ru.deliveon.lists.interfaces.AddEditListInterface;
import ru.deliveon.lists.interfaces.DatabaseCallbackLists;
import ru.deliveon.lists.interfaces.DeleteListInterface;
import ru.deliveon.lists.interfaces.OnItemListener;

public class MainActivity extends AppCompatActivity implements DeleteListInterface, AddEditListInterface, DatabaseCallbackLists {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    @BindView(R.id.idMainLayout)
    LinearLayout mainLayout;
    private AdapterList adapter;
    private int positionDelete = -1;
    private RequestsLists requestsLists;
    private int idList;
    private List<ProductForList> sameIdList;
    private List<Lists> listLists;


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
                openAddEditListDialog();
            }
        });

        requestsLists.getLists(this);
    }//onCreate

    private void openAddEditListDialog() {
        AddEditListDialog addEditListDialog = new AddEditListDialog();
        addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
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


    @Override
    public void onListsLoaded(final List<Lists> lists) {
        if (lists.size() == 0) {
            openAddEditListDialog();
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddEditListDialog();
                }
            });
        } else mainLayout.setOnClickListener(null);
        listLists = lists;
        if (adapter == null || positionDelete == -1) {
            OnItemListener onItemListener = new OnItemListener() {
                @Override
                public void onItemClick(int position, View v) {
                    itemClick(position, listLists);
                }

                @Override
                public void onItemLongClick(int position, View v) {
                    itemLongClick(position, v, listLists);
                }
            };
            adapter = new AdapterList(MainActivity.this, listLists, onItemListener);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.deleteFromListAdapter(positionDelete);
            positionDelete = -1;
        }
    }//onListsLoaded

    private void itemClick(int position, List<Lists> list) {
        Intent intent = new Intent(MainActivity.this, Products.class);
        idList = list.get(position).getListId();
        String nameList = list.get(position).getListName();
        intent.putExtra("nameList", nameList);
        intent.putExtra("idList", idList);
        startActivity(intent);
    }//itemClick

    private void itemLongClick(final int position, View v, final List<Lists> list) {
        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);
        popup.inflate(R.menu.context_menu);
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
        popup.show();
    }//itemLongClick

    @Override
    public void onListDeleted() {
        //search in the table "goods in the list" records with the same id LIST
        requestsLists.getSameIdListForList(MainActivity.this, idList);
    }

    //found in the table "goods in the list" all records with the same id LIST
    @Override
    public void onSameIdList(List<ProductForList> list) {
        requestsLists.dispSameIdList.dispose();
        sameIdList = list;//all records with the same id LIST
        //delete from the table "goods in the list" records with the same id LIST
        if (list.size() > 0) {
            requestsLists.deleteListProductForList(MainActivity.this, list);
        }
    }

    //removed from the table "goods in the list" all entries with the same id LIST
    @Override
    public void onDeletedListProductForList() {
        //get a list with duplicates in other lists
        List<Integer> listIdProduct = new ArrayList<>();
        for (int i = 0; i < sameIdList.size(); i++) {
            listIdProduct.add(sameIdList.get(i).getIdProduct());
        }
        requestsLists.getInOtherLists(MainActivity.this, listIdProduct);
    }//onDeletedListProductForList

    //received a list with duplicates in other cliches
    @Override
    public void onInOtherLists(List<ProductForList> list) {
        requestsLists.dispInOtherLists.dispose();
        //create a list c id of the goods that need to be deleted and deleted
        List<Integer> listForDeleting = new ArrayList<>();//the final list to delete
        for (int i = 0; i < sameIdList.size(); i++) {
            if (!list.contains(sameIdList.get(i)))
                listForDeleting.add(sameIdList.get(i).getIdProduct());
        }
        if (listForDeleting.size() > 0)
            requestsLists.deleteProductList(MainActivity.this, listForDeleting);
    }//onInOtherLists

    @Override
    public void onDeletedProductList() {
        Log.d("MainActivityclass", "jjj");
    }

    @Override
    public void onListsAdded() {
        Log.d("MainActivityclass", "jjj");
    }

    @Override
    public void onDataNotAvailable() {
        Log.d("MainActivityclass", "jjj");
    }

    @Override
    public void onListsUpdated() {
        Log.d("MainActivityclass", "jjj");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestsLists.dispos.dispose();
    }

    @Override
    public void onBackPressed() {
        ExitDialog exitDialog = new ExitDialog();
        exitDialog.show(getSupportFragmentManager(), "exit");
    }
}
