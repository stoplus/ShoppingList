package ru.deliveon.lists;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.deliveon.lists.adapters.AdapterProductList;
import ru.deliveon.lists.adapters.AdapterProductListPurchased;
import ru.deliveon.lists.adapters.recyclerHelper.SimpleItemTouchHelperCallback;
import ru.deliveon.lists.addEdit.AddEditActivity;
import ru.deliveon.lists.di.App;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.database.entity.ProductForList;
import ru.deliveon.lists.interfaces.DatabaseCallbackProduct;
import ru.deliveon.lists.interfaces.OnItemListener;

public class Products extends AppCompatActivity implements DatabaseCallbackProduct {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecyclerProd)
    RecyclerView recyclerProd;
    @BindView(R.id.idRecyclerProdPurchased)
    RecyclerView recyclerProdPurchased;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    @BindView(R.id.idLinearLayout)
    LinearLayout layout;
    @BindView(R.id.textViewReady)
    TextView textViewReady;
    @BindView(R.id.textViewNotReady)
    TextView textViewNotReady;

    private RequestsLists requestsLists;
    private int positionDelete = -1;
    private int positionDeletePurchased = -1;
    private List<Product> productList;
    private List<Product> listPurchased;
    private AdapterProductList adapterProductList;
    private AdapterProductListPurchased adapterProductListPurchased;
    private int idList;
    private int idProduct;
    private Product product;
    private boolean flagDel = true;
    public final int REQEST_EDIT = 101;             //response when changing data
    private final int REQEST_ADD = 102;             //constant for adding
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper mItemTouchHelperPurchased;
    boolean isUpdate = false;
    boolean isUpdatePurchased = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        requestsLists = App.app().getComponent().getRequestsLists();

        idList = getIntent().getIntExtra("idList", -1);
        String nameList = getIntent().getStringExtra("nameList");
        setTitle(getResources().getString(R.string.list) + ": " + nameList);

        fabBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Products.this, AddEditActivity.class);
            intent.putExtra("idList", idList);
            intent.putExtra("sortNum", productList.size() + 1);
            intent.putExtra("newImageFlag", true);
            startActivityForResult(intent, REQEST_ADD);
        });
        requestsLists.getAllForList(Products.this, idList);

        recyclerProdPurchased.setNestedScrollingEnabled(false);
    }//onCreate

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //return from adding / editing
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQEST_ADD:
                    Product product = data.getParcelableExtra("product");
                    requestsLists.addListProduct(this, product);
                    break;
                case REQEST_EDIT:
                    Product productUpdate = data.getParcelableExtra("productUpdate");
                    requestsLists.updateProduct(this, productUpdate, idList);
                    break;
            }//switch
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult

    public void updateTwoLists(List<Product> lists) {
        productList = new ArrayList<>();//list of not bought
        listPurchased = new ArrayList<>();//list of purchased

        for (int i = 0; i < lists.size(); i++) {//filter the general list on bought and not bought
            if (lists.get(i).isBought()) listPurchased.add(lists.get(i));
            else productList.add(lists.get(i));
        }
        if (checkSortNum(productList, isUpdate) | checkSortNum(listPurchased, isUpdatePurchased)) {
            isUpdate = false;
            isUpdatePurchased = false;
            requestsLists.getAllForList(Products.this, idList);
            return;
        }

        Collections.sort(productList, (obj1, obj2) -> Integer.compare(obj1.sortNum, obj2.sortNum));
        Collections.sort(listPurchased, (obj1, obj2) -> Integer.compare(obj1.sortNum, obj2.sortNum));

        setHints(lists);

        if (adapterProductList == null || flagDel) {
            createAndInstallAdapter();
        } else {
            if (positionDelete > -1) {
                adapterProductList.deleteFromListAdapter(positionDelete);
                positionDelete = -1;
            } else if (positionDeletePurchased > -1) {
                adapterProductListPurchased.deleteFromListAdapter(positionDeletePurchased);
                positionDeletePurchased = -1;
            }
        }//if
    }//updateTwoLists

    private boolean checkSortNum(List<Product> productList, boolean update) {
        List<Product> newListProduct = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (product.sortNum == 0 || update) {
                product.sortNum = i + 1;
                update = true;
            }
            newListProduct.add(product);
        }
        if (update) {
            //если обновляли, отправляем на сохранение нового списка с обновленными номерами сортировки
            requestsLists.updateListProduct(Products.this, newListProduct, 0);
            return true;
        } else {
            flagDel = true;//разрешаем пересоздать адаптер
            return false;
        }
    }

    private void setHints(List<Product> lists) {
        if (lists.size() == 0) {
            textViewReady.setText(getResources().getString(R.string.add_post));
            textViewReady.setTextColor(getResources().getColor(R.color.colorAlert));
            textViewReady.setGravity(Gravity.CENTER);
            textViewNotReady.setVisibility(View.GONE);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(getResources().getString(R.string.collected)).append(" ")
                    .append(listPurchased.size());
            textViewReady.setText(builder);
            textViewReady.setGravity(Gravity.END);
            textViewReady.setTextColor(getResources().getColor(R.color.collected));

            builder.delete(0, builder.length());

            builder.append(getResources().getString(R.string.not_collected)).append(" ")
                    .append(productList.size());
            textViewNotReady.setVisibility(View.VISIBLE);
            textViewNotReady.setText(builder);
        }//if
    }//setHints

    private void createAndInstallAdapter() {
        OnItemListener onItemListener = new OnItemListener() {
            @Override
            public void onItemClick(int position, View v, List<Product> list) {
                itemClick(position, list);
            }

            @Override
            public void onItemLongClick(int position, View v, List<Product> list) {
                itemLongClickOrRemove(position, v, list, false);
            }

            @Override
            public void onRemoveItem(int position, View v) {
                itemLongClickOrRemove(position, v, productList, true);
            }

            @Override
            public void onRemoveItemPurchase(int position, View v) {
                itemLongClickOrRemove(position, v, listPurchased, true);
            }

            @Override
            public void onCheckSortNum(List<Product> list) {
                checkSortNum(list, true);
                Collections.sort(productList, (obj1, obj2) -> Integer.compare(obj1.sortNum, obj2.sortNum));
                Collections.sort(listPurchased, (obj1, obj2) -> Integer.compare(obj1.sortNum, obj2.sortNum));
            }
        };

        //адаптер для ожидающих
        adapterProductList = new AdapterProductList(
                this, productList, getSupportFragmentManager(), onItemListener, viewHolder -> {
            mItemTouchHelper.startDrag(viewHolder);//делаем вызов для перетаскивания или свайпа
        });//adapter for non-purchased
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapterProductList);
        mItemTouchHelper = new ItemTouchHelper(callback);

        //адаптер для сложенных
        adapterProductListPurchased = new AdapterProductListPurchased(
                this, listPurchased, getSupportFragmentManager(), onItemListener, viewHolder -> {
            mItemTouchHelperPurchased.startDrag(viewHolder);//делаем вызов для перетаскивания или свайпа
        });//for purchased
        ItemTouchHelper.Callback callbackPurchased = new SimpleItemTouchHelperCallback(adapterProductListPurchased);
        mItemTouchHelperPurchased = new ItemTouchHelper(callbackPurchased);

        recyclerProd.setAdapter(adapterProductList);
        recyclerProdPurchased.setAdapter(adapterProductListPurchased);
        mItemTouchHelper.attachToRecyclerView(recyclerProd);
        mItemTouchHelperPurchased.attachToRecyclerView(recyclerProdPurchased);
    }//createAndInstallAdapter

    private void itemClick(int position, List<Product> list) {
        flagDel = true;
        Product product = list.get(position);
        if (product.isBought()) {
            product.setBought(false);//establish that we have not bought
            product.sortNum = productList.size() + 1;
            isUpdatePurchased = true;// разрешаем переписать sortNum
        } else {
            product.setBought(true);//establish that we bought
            product.sortNum = listPurchased.size() + 1;
            isUpdate = true;// разрешаем переписать sortNum
        }

        requestsLists.updateProduct(Products.this, product, idList);
    }//itemClick

    private void itemLongClickOrRemove(final int position, View v, final List<Product> list, boolean remove) {
        product = list.get(position);
        idProduct = product.getId();
        flagDel = true;
        if (remove && v == null) {
            if (product.isBought()) positionDeletePurchased = position;
            else positionDelete = position;
            requestsLists.deleteProductForList(Products.this, idProduct, idList);
        } else {
            try {
                PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);//создаем объект окна меню
                popup.inflate(R.menu.context_menu);//закачиваем меню из XML файла
                //определяем нажатия на элементы меню
                //onMenuItemClick
                popup.setOnMenuItemClickListener(item -> {
                    Intent intent = new Intent(Products.this, AddEditActivity.class);
                    intent.putExtra("idList", idList);
                    intent.putExtra("product", product);
                    intent.putExtra("newImageFlag", false);
                    startActivityForResult(intent, REQEST_EDIT);
                    return false;
                });
                popup.show();
            } catch (IndexOutOfBoundsException e) {
                Log.d("Productsclass", e.getMessage());
            }
        }
    }//itemLongClick

    //see the change in the table "products"
    @Override
    public void onListProductsLoaded(List<Product> lists) {
        requestsLists.disposable.dispose();// unsubscribe the observer
        Log.d("Productsclass", "onListProductsLoaded");
        if (lists.size() == 0) {
            layout.setOnClickListener(v -> {
                Intent intent = new Intent(Products.this, AddEditActivity.class);
                intent.putExtra("idList", idList);
                intent.putExtra("sortNum", productList.size() + 1);
                intent.putExtra("newImageFlag", true);
                startActivityForResult(intent, REQEST_ADD);
            });
        } else layout.setOnClickListener(null);
        updateTwoLists(lists);
        flagDel = false;
    }

    // added the product to the product table
    @Override
    public void onProductAdded() {
        Log.d("Productsclass", "onProductAdded");
//        textViewReady.setText(getResources().getString(R.string.purchased));
        // get the last added product
        requestsLists.getlastProduct(Products.this);
    }

    // get the last entry from the product table
    @Override
    public void onLastProduct(int idProduct) {
        Log.d("Productsclass", "onLastProduct");
        requestsLists.dispListId.dispose();
        // add to the table "goods in the list"
        ProductForList productForList = new ProductForList(idList, idProduct);
        requestsLists.addProductForList(Products.this, productForList);
    }

    // added an entry to the table "items in the list"
    @Override
    public void onProductForListAdded() {
        flagDel = true;
        requestsLists.getAllForList(Products.this, idList);
        Log.d("Productsclass", "добавили запись в таблицу \"товары в списке\"");
    }

    // deleted the record from the table "goods in the list"
    @Override
    public void onProductForListDeleted() {
        Log.d("Productsclass", "onProductForListDeleted");
        // Looking for a record with the same product id
        requestsLists.getSameIdProductForList(Products.this, idProduct);
    }

    // get the list of records from the table "products in the list" with the same id
    @Override
    public void onSameIdProductForList(List<ProductForList> list) {
        Log.d("Productsclass", "onSameIdProductForList");
        // delete the goods, if there are no entries with the same product id
        requestsLists.dispSameId.dispose();
        if (list.size() == 0) {
            requestsLists.deleteProduct(Products.this, product, idList);
        } else requestsLists.getAllForList(Products.this, idList);
    }

    //Loading the menu in the window
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }//onCreateOptionsMenu

    //Processing of button presses in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the identifier of the selected menu item
        int id = item.getItemId();
        switch (id) {
            case R.id.allItemsPurchased:// all bought
                for (int i = 0; i < productList.size(); i++) {
                    if (!productList.get(i).isBought())
                        productList.get(i).setBought(true);
                }
                flagDel = true;
                requestsLists.updateListProduct(this, productList, idList);
                break;
            case R.id.allItemsNotPurchased:
                for (int i = 0; i < listPurchased.size(); i++) {
                    if (listPurchased.get(i).isBought())
                        listPurchased.get(i).setBought(false);
                }
                flagDel = true;
                requestsLists.updateListProduct(this, listPurchased, idList);
                break;
            case R.id.sortByName:
                Collections.sort(productList, (obj1, obj2) -> obj1.getNameProduct().compareTo(obj2.getNameProduct()));
                Collections.sort(listPurchased, (obj1, obj2) -> obj1.getNameProduct().compareTo(obj2.getNameProduct()));
                createAndInstallAdapter();
                break;
            case R.id.back:
                finish();
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("idList", idList);
        outState.putInt("positionDelete", positionDelete);
        outState.putInt("positionDeletePurchased", positionDeletePurchased);
        outState.putInt("idProduct", idProduct);
        outState.putBoolean("flagDel", flagDel);
        outState.putParcelableArrayList("productList", (ArrayList<? extends Parcelable>) productList);
        outState.putParcelableArrayList("listPurchased", (ArrayList<? extends Parcelable>) listPurchased);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        idList = savedInstanceState.getInt("idList");
        positionDelete = savedInstanceState.getInt("positionDelete");
        positionDeletePurchased = savedInstanceState.getInt("positionDeletePurchased");
        idProduct = savedInstanceState.getInt("idProduct");
        flagDel = savedInstanceState.getBoolean("flagDel");
        productList = savedInstanceState.getParcelableArrayList("productList");
        listPurchased = savedInstanceState.getParcelableArrayList("listPurchased");
    }
}
