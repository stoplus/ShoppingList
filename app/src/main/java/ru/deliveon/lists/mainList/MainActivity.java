package ru.deliveon.lists.mainList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import ru.deliveon.lists.addEdit.AddEditListDialog;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.database.entity.ProductForList;
import ru.deliveon.lists.di.App;
import ru.deliveon.lists.dialogs.DeleteListDialog;
import ru.deliveon.lists.dialogs.ExitDialog;
import ru.deliveon.lists.entity.ExportList;
import ru.deliveon.lists.interfaces.AddEditListInterface;
import ru.deliveon.lists.interfaces.DatabaseCallbackLists;
import ru.deliveon.lists.interfaces.DeleteListInterface;
import ru.deliveon.lists.utils.UtilIntentShare;

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
    boolean isItemRemove = false;
    private final int DIALOG_ID_COLOR = 0;
    private Lists lists;
    private String selectedName;
    private boolean isImport = false;
    private ExportList importedModel;
    private int countImportProd = 0;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("text/plain");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 11111);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 11111:// reqCode system when selecting file
                    importedModel = UtilIntentShare.importFile(this, data.getData());
                    if (importedModel != null) {
                        isImport = true;
                        requestsLists.addLists(this, new Lists(importedModel.getName(), importedModel.getColor()));
                    } else {
                        Snackbar.make(mainLayout, getResources().getString(R.string.import_error), Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }//switch
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult

    private void openAddEditListDialog() {
        AddEditListDialog addEditListDialog = new AddEditListDialog();
        addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
    }

    @Override
    public void addList(final Lists lists) {
        selectedName = "";
        requestsLists.addLists(this, lists);
    }

    @Override
    public void update(Lists lists) {
        selectedName = "";
        requestsLists.updateLists(this, lists);
    }

    @Override
    public void openPicker(int colorForStartDialog, Lists lists, String name) {
        selectedName = name;
        this.lists = lists;
        ColorPickerDialog.Builder colorDialog = ColorPickerDialog.newBuilder();
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
        isItemRemove = true;
        requestsLists.deleteList(this, lists);
    }

    @Override
    public void cancelDeleteList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListsLoaded(final List<Lists> lists) {
        setListener(lists);

        if (checkSortNum(lists, isItemRemove) || isItemMove) {
            isItemMove = false;
            isItemRemove = false;
            Collections.sort(listLists, (obj1, obj2) -> Integer.compare(obj1.getSortNum(), obj2.getSortNum()));
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
            adapter = new AdapterList(listLists, onItemListener, this);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

            if (isImport) {
                isImport = false;
                setImportedProducts();
            }
        } else {
            adapter.deleteFromListAdapter(positionDelete);
            positionDelete = -1;
        }
    }//onListsLoaded

    private void setImportedProducts() {
        Lists importedList = listLists.get(listLists.size() - 1);
        List<Product> importedProductList = importedModel.getListProduct();
        Product importedProduct = importedProductList.get(countImportProd);
        Product newProduct = new Product(
                importedProduct.nameProduct,
                "",
                importedProduct.isBought(),
                0
        );
        requestsLists.addProduct(this, null, newProduct, importedList.id);
        countImportProd++;
    }

    private void setListener(List<Lists> lists) {
        if (lists.size() == 0) {
            openAddEditListDialog();
            mainLayout.setOnClickListener(v -> openAddEditListDialog());
        } else {
            mainLayout.setOnClickListener(null);
        }
    }

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

        listLists = newListProduct;

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
        int color = list.get(position).getColor();
        intent.putExtra("nameList", nameList);
        intent.putExtra("idList", idList);
        intent.putExtra("color", color);
        startActivity(intent);
    }//itemClick

    private void itemLongClickOrRemove(final int position, View v, final List<Lists> list, boolean remove) {
        Lists lists = list.get(position);
        Bundle args = new Bundle();
        args.putSerializable("lists", lists);
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
    public void productRemoved() {

    }

    @Override
    public void onProductImported() {
        int sizeImportedList = importedModel.getListProduct().size();
        if (countImportProd < sizeImportedList) {
            setImportedProducts();
        } else {
            countImportProd = 0;
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onColorSelected(int dialogId, int selectedColor) {
        if (dialogId == DIALOG_ID_COLOR) {
            Bundle args = new Bundle();
            args.putSerializable("lists", lists);
            args.putInt("color", selectedColor);
            args.putString("name", selectedName);
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
