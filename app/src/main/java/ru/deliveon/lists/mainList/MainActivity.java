package ru.deliveon.lists.mainList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.deliveon.lists.Products;
import ru.deliveon.lists.R;
import ru.deliveon.lists.RequestsLists;
import ru.deliveon.lists.adapters.AdapterList;
import ru.deliveon.lists.adapters.recyclerHelper.OnStartDragListener;
import ru.deliveon.lists.adapters.recyclerHelper.SimpleItemTouchHelperCallback;
import ru.deliveon.lists.di.App;
import ru.deliveon.lists.addEdit.AddEditListDialog;
import ru.deliveon.lists.dialogs.DeleteListDialog;
import ru.deliveon.lists.dialogs.ExitDialog;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.database.entity.ProductForList;
import ru.deliveon.lists.interfaces.AddEditListInterface;
import ru.deliveon.lists.interfaces.DatabaseCallbackLists;
import ru.deliveon.lists.interfaces.DeleteListInterface;

public class MainActivity extends AppCompatActivity implements DeleteListInterface,
        AddEditListInterface, DatabaseCallbackLists, OnStartDragListener, ColorPickerDialogListener {
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
    private ItemTouchHelper mItemTouchHelper;
    boolean isItemMove = false;
    private ColorPickerDialog.Builder colorDialog;
    private int colorForStartDialog = 0;
    private final int DIALOG_ID_COLOR = 0;
    private Lists lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        requestsLists = App.app().getComponent().getRequestsLists();
        App.app().getListComponent().inject(requestsLists);
        setSupportActionBar(toolbar);

        fabBtn.setOnClickListener(view -> openAddEditListDialog());

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
    public void openPicker(int colorForStartDialog, Lists lists) {
        this.lists = lists;
        colorDialog = ColorPickerDialog.newBuilder();
        colorDialog.setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(DIALOG_ID_COLOR)
                .setColor(colorForStartDialog)
                .setDialogTitle(R.string.select_color)
                .setSelectedButtonText(R.string.select)
                .setShowAlphaSlider(false)
                .setPresetsButtonText(R.string.presets)
                .setCustomButtonText(R.string.custom)
                .setShowAlphaSlider(false)
                .show(this);
    }

    @Override
    public void deleteList(Lists lists) {
        requestsLists.deleteList(this, lists);
    }

    @Override
    public void cancelDeleteList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListsLoaded(final List<Lists> lists) {
        listLists = lists;
        if (lists.size() == 0) {
            openAddEditListDialog();
            mainLayout.setOnClickListener(v -> openAddEditListDialog());
        } else{
            mainLayout.setOnClickListener(null);
        }

        if (checkSortNum(lists, false) || isItemMove){
            isItemMove = false;
            return;
        }

        Collections.sort(listLists, (obj1, obj2) -> Integer.compare(obj1.getSortNum(), obj2.getSortNum()));

        if (adapter == null || positionDelete == -1) {
            OnItemListenerMain onItemListener = new OnItemListenerMain() {
                @Override
                public void onItemClick(int position, View v, List<Lists> list) {
                    itemClick(position, list);
                }

                @Override
                public void onItemLongClick(int position, View v, List<Lists> list) {
                    itemLongClickOrRemove(position, v, list, false);
                }

                @Override
                public void onRemoveItem(int position, View v) {
                    itemLongClickOrRemove(position, v, listLists, true);
                }

                @Override
                public void onCheckSortNum(List<Lists> list) {
                    isItemMove = true;
                    checkSortNum(list, true);
                }
            };
            adapter = new AdapterList(MainActivity.this, listLists, onItemListener, this);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
        } else {
            adapter.deleteFromListAdapter(positionDelete);
            positionDelete = -1;
        }
    }//onListsLoaded

    private boolean checkSortNum(List<Lists> productList, boolean update) {
        List<Lists> newListProduct = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            Lists product = productList.get(i);
            if (product.getSortNum() == 0 || update) {
                product.setSortNum(i + 1);
                update = true;
            }
            newListProduct.add(product);
        }
        if (update) {
            //если обновляли, отправляем на сохранение нового списка с обновленными номерами сортировки
            requestsLists.updateList(this, newListProduct);
            return true;
        } else {
            return false;
        }
    }

    private void itemClick(int position, List<Lists> list) {
        Intent intent = new Intent(MainActivity.this, Products.class);
        idList = list.get(position).getListId();
        String nameList = list.get(position).getListName();
        intent.putExtra("nameList", nameList);
        intent.putExtra("idList", idList);
        startActivity(intent);
    }//itemClick

    private void itemLongClickOrRemove(final int position, View v, final List<Lists> list, boolean remove) {
        Lists lists = list.get(position);
        Bundle args = new Bundle();
        args.putParcelable("lists", lists);
        if (remove && v == null) {
            positionDelete = position;
            DeleteListDialog deleteDialog = new DeleteListDialog();
            deleteDialog.setArguments(args);
            deleteDialog.show(getSupportFragmentManager(), "deleteList");
        } else {
            PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);
            popup.inflate(R.menu.context_menu);
            //определяем нажатия на элементы меню
            popup.setOnMenuItemClickListener(item -> {
                AddEditListDialog addEditListDialog = new AddEditListDialog();
                addEditListDialog.setArguments(args);
                addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
                return false;
            });
            popup.show();
        }
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
    }//onInOtherList

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onColorSelected(int dialogId, int selectedColor) {
        if (dialogId == DIALOG_ID_COLOR) {
            colorForStartDialog = selectedColor;
//            datable.saveColor(selectedColor);
         //   lists.setColor();
            Bundle args = new Bundle();
            args.putParcelable("lists", lists);
            AddEditListDialog addEditListDialog = new AddEditListDialog();
            addEditListDialog.setArguments(args);
            addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        //auto generate
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
