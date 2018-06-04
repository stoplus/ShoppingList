package ru.deliveon.lists;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.deliveon.lists.adapters.AdapterProductList;
import ru.deliveon.lists.adapters.AdapterProductListPurchased;
import ru.deliveon.lists.entity.Product;
import ru.deliveon.lists.entity.ProductForList;
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
    @BindView(R.id.textView2)
    TextView textPurchased;
    @BindView(R.id.idLinearLayout)
    LinearLayout layout;
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

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Products.this, AddEdit.class);
                intent.putExtra("idList", idList);
                intent.putExtra("newImageFlag", true);
                startActivityForResult(intent, REQEST_ADD);
            }
        });
        requestsLists.getAllForList(Products.this, idList);

        recyclerProdPurchased.setNestedScrollingEnabled(false);
    }//onCreate

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
                    requestsLists.updateProduct(this, productUpdate);
                    break;
            }//switch
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult

    public void updateTwoLists(List<Product> lists) {
        productList = new ArrayList<>();//list of not bought
        listPurchased = new ArrayList<>();//list of purchased
        for (int i = 0; i < lists.size(); i++) {//filter the general list on bought and not bought
            if (lists.get(i).isBought()) {
                listPurchased.add(lists.get(i));
            } else productList.add(lists.get(i));
        }

        if (adapterProductList == null) {
            createAndInstallAdapter();
        } else {
            if (positionDelete > -1) {
                adapterProductList.deleteFromListAdapter(positionDelete);
                positionDelete = -1;
            } else if (positionDeletePurchased > -1) {
                adapterProductListPurchased.deleteFromListAdapter(positionDeletePurchased);
                positionDeletePurchased = -1;
            } else if (flagDel) createAndInstallAdapter();
        }//if
    }//updateTwoLists

    private void createAndInstallAdapter() {
        OnItemListener onItemListener = new OnItemListener() {
            @Override
            public void onItemClick(int position, View v) {
                itemClick(position, productList);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                itemLongClick(position, v, productList);
            }
        };

        OnItemListener onItemListenerPurchased = new OnItemListener() {
            @Override
            public void onItemClick(int position, View v) {
                itemClick(position, listPurchased);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                itemLongClick(position, v, listPurchased);
            }
        };

        adapterProductList = new AdapterProductList(
                this, productList, getSupportFragmentManager(), onItemListener);//adapter for non-purchased
        adapterProductListPurchased = new AdapterProductListPurchased(
                this, listPurchased, getSupportFragmentManager(), onItemListenerPurchased);//for purchased
        recyclerProd.setAdapter(adapterProductList);
        recyclerProdPurchased.setAdapter(adapterProductListPurchased);
    }//createAndInstallAdapter

    private void itemClick(int position, List<Product> list) {
        flagDel = true;
        Product productPurchased = list.get(position);
        if (productPurchased.isBought())
            productPurchased.setBought(false);//establish that we have not bought
        else productPurchased.setBought(true);//establish that we bought

        requestsLists.updateProduct(Products.this, productPurchased);
    }//itemClick

    private void itemLongClick(final int position, View v, final List<Product> list) {
        try {
            PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);//создаем объект окна меню
            popup.inflate(R.menu.context_menu);//закачиваем меню из XML файла
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {//определяем нажатия на элементы меню
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    product = list.get(position);
                    idProduct = product.getId();
                    Boolean bought = product.isBought();
                    Bundle args = new Bundle();
                    args.putParcelable("product", product);
                    flagDel = true;
                    switch (item.getItemId()) {
                        case R.id.edit:
                            Intent intent = new Intent(Products.this, AddEdit.class);
                            intent.putExtra("idList", idList);
                            intent.putExtra("product", product);
                            intent.putExtra("newImageFlag", false);
                            startActivityForResult(intent, REQEST_EDIT);
                            return true;
                        case R.id.delete:
                            if (bought) positionDeletePurchased = position;
                            else positionDelete = position;
                            requestsLists.deleteProductForList(Products.this, idProduct, idList);
                            return true;
                    }//switch
                    return false;
                }//onMenuItemClick
            });
            popup.show();
        } catch (IndexOutOfBoundsException e) {
            Log.d("Productsclass", e.getMessage());
        }
    }//itemLongClick

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //see the change in the table "products"
    @Override
    public void onListProductsLoaded(List<Product> lists) {
        requestsLists.disposable.dispose();// unsubscribe the observer
        Log.d("Productsclass", "onListProductsLoaded");
        if (lists.size() == 0) {
            textPurchased.setText(getResources().getString(R.string.add_post));
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Products.this, AddEdit.class);
                    intent.putExtra("idList", idList);
                    intent.putExtra("newImageFlag", true);
                    startActivityForResult(intent, REQEST_ADD);
                }
            });
        }else layout.setOnClickListener(null);
        updateTwoLists(lists);
        flagDel = false;
    }

    // added the product to the product table
    @Override
    public void onProductAdded() {
        Log.d("Productsclass", "onProductAdded");
        textPurchased.setText(getResources().getString(R.string.purchased));
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
            requestsLists.deleteProduct(Products.this, product);
        } else requestsLists.getAllForList(Products.this, idList);
    }

    // deleted the entry in the "Products" table
    @Override
    public void onProductDeleted() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("Productsclass", "onProductDeleted");
    }

    @Override
    public void onDataNotAvailable() {
        Log.d("Productsclass", "onDataNotAvailable");
    }

    @Override
    public void onProductUpdated() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("Productsclass", "onProductUpdated");
    }

    @Override
    public void onUpdateList() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("Productsclass", "onUpdateList");
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
                requestsLists.updateListProduct(this, productList);
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