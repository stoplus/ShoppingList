package com.example.den.shoppinglist;

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

import com.example.den.shoppinglist.adapters.AdapterProductList;
import com.example.den.shoppinglist.adapters.AdapterProductListPurchased;
import com.example.den.shoppinglist.entity.Product;

import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackProduct;
import com.example.den.shoppinglist.interfaces.OnItemListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Products extends AppCompatActivity implements DatabaseCallbackProduct {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecyclerProd)
    RecyclerView recyclerProd;
    @BindView(R.id.idRecyclerProdPurchased)
    RecyclerView recyclerProdPurchased;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
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
    public final int REQEST_EDIT = 101;                //ответ при изменении данных
    private final int REQEST_ADD = 102;             //константа для добавления

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

    //возврат из добавления / редактирования
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
    }

    //==============================================================================================
    public void updateTwoLists(List<Product> lists) {
        productList = new ArrayList<>();//список не купленных
        listPurchased = new ArrayList<>();//список купленых
        for (int i = 0; i < lists.size(); i++) {//фильтруем общий список на купленный и не купленный
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
            } else {
                if (flagDel) {
                    createAndInstallAdapter();
                }
            }
        }
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

        adapterProductList = new AdapterProductList(this, productList, getSupportFragmentManager(), onItemListener);//адаптер для не купленных
        adapterProductListPurchased = new AdapterProductListPurchased(this, listPurchased, getSupportFragmentManager(), onItemListenerPurchased);//для купленных
        recyclerProd.setAdapter(adapterProductList);//подсоединяем адаптер к ресайклеру
        recyclerProdPurchased.setAdapter(adapterProductListPurchased);
    }//createAndInstallAdapter

    private void itemClick(int position, List<Product> list) {
        flagDel = true;
        Product productPurchased = list.get(position);
        if (productPurchased.isBought())
            productPurchased.setBought(false);//устанавливаем, что не куплен
        else productPurchased.setBought(true);//устанавливаем, что куплен

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
                        default:
                            break;
                    }//switch
                    return false;
                }//onMenuItemClick
            });
            popup.show();//показываем окно меню
        } catch (IndexOutOfBoundsException e) {
            Log.d("ddd", e.getMessage());
        }
    }//itemLongClick

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //смотрим изменение в таблице "продукты"
    @Override
    public void onListProductsLoaded(List<Product> lists) {
        requestsLists.disposable.dispose();//отписываем наблюдателя
        Log.d("ddd", "onListProductsLoaded");

        updateTwoLists(lists);
        flagDel = false;
    }

    //добавили продукт в таблицу товаров
    @Override
    public void onProductAdded() {
        Log.d("ddd", "onProductAdded");
        //получаем последний добавленный продукт
        requestsLists.getlastProduct(Products.this);
    }

    //получаем последнюю запись из таблици продуктов
    @Override
    public void onLastProduct(int idProduct) {
        Log.d("ddd", "onLastProduct");
        requestsLists.dispListId.dispose();
        //добавляем в таблицу "товары в списке"
        ProductForList productForList = new ProductForList(idList, idProduct);
        requestsLists.addProductForList(Products.this, productForList);
    }

    //добавили запись в таблицу "товары в списке"
    @Override
    public void onProductForListAdded() {
        flagDel = true;
        requestsLists.getAllForList(Products.this, idList);
        Log.d("ddd", "добавили запись в таблицу \"товары в списке\"");
    }

    //удалили запись с таблици "товары в списке"
    @Override
    public void onProductForListDeleted() {
        Log.d("ddd", "onProductForListDeleted");
        //ищем запись с таким же id товара
        requestsLists.getSameIdProductForList(Products.this, idProduct);
    }

    //получаем список записей из таблици "товары в списке" с таким же id
    @Override
    public void onSameIdProductForList(List<ProductForList> list) {
        Log.d("ddd", "onSameIdProductForList");
        //удаляем товар, если нет записей с таким же id товара
        requestsLists.dispSameId.dispose();
        if (list.size() == 0) {
            requestsLists.deleteProduct(Products.this, product);
        }else requestsLists.getAllForList(Products.this, idList);
    }

    //удалили запись в таблице "товары"
    @Override
    public void onProductDeleted() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("ddd", "onProductDeleted");
    }

    @Override
    public void onDataNotAvailable() {
        Log.d("ddd", "onDataNotAvailable");
    }

    @Override
    public void onProductUpdated() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("ddd", "onProductUpdated");
    }

    @Override
    public void onUpdateList() {
        requestsLists.getAllForList(Products.this, idList);
        Log.d("ddd", "onUpdateList");
    }

    //--------------------------------------------------------------------------------------------------------
    // Загрузка меню в окне
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }//onCreateOptionsMenu

    // Обработка нажатий на кнопки в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.allItemsPurchased://все куплены
                for (int i = 0; i < productList.size(); i++) {
                    if (!productList.get(i).isBought())
                        productList.get(i).setBought(true);
                }
                flagDel = true;
                requestsLists.updateListProduct(this, productList);
                break;
            case R.id.back://назад
                finish();
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    //--------------------------------------------------------------------------------------------------------

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
