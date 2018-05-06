package com.example.den.shoppinglist;

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

import com.example.den.shoppinglist.adapters.AdapterProductList;
import com.example.den.shoppinglist.dialogs.AddEditListProductsDialog;
import com.example.den.shoppinglist.entity.Product;

import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.AddEditListProductInterface;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackListProduct;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsList extends AppCompatActivity implements AddEditListProductInterface, DatabaseCallbackListProduct {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecyclerProd)
    RecyclerView recyclerProd;
    @BindView(R.id.idRecyclerProdPurchased)
    RecyclerView idRecyclerProdPurchased;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    private RequestsLists requestsLists;
    private int positionDelete = -1;
    private List<Product> product;
    private List<Product> listPurchased;
    private AdapterProductList adapterProductList;
    private int idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        requestsLists = App.app().getComponent().getRequestsLists();

        idList = getIntent().getIntExtra("idList", -1);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditListProductsDialog addEditListProductsDialog = new AddEditListProductsDialog();
                Bundle args = new Bundle();
                args.putInt("idList", idList);
                addEditListProductsDialog.show(getSupportFragmentManager(), "addEditListProductsDialog");
            }
        });
        requestsLists.getAllForList(ProductsList.this, idList);

        recyclerProd.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerProd, new RecyclerItemClickListener.OnItemClickListener() {
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
                                Product product = ProductsList.this.product.get(position);
                                Bundle args = new Bundle();
                                args.putParcelable("product", product);
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        AddEditListProductsDialog addEditListProductsDialog = new AddEditListProductsDialog();
                                        addEditListProductsDialog.setArguments(args);
                                        addEditListProductsDialog.show(getSupportFragmentManager(), "addEditListProductsDialog");
                                        return true;
                                    case R.id.delete:
                                        positionDelete = position;
                                        requestsLists.deleteListProduct(ProductsList.this, product);
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

    //добавляем продукт в таблицу продуктов
    @Override
    public void addProduct(Product product) {
        requestsLists.addListProduct(this, product);
    }

    @Override
    public void updateProduct(Product lists) {
        requestsLists.updateListProduct(this, lists);
    }

    //==============================================================================================
    //смотрим измене
    @Override
    public void onListProductsLoaded(List<Product> lists) {
        product = lists;
        if (adapterProductList == null || positionDelete == -1) {
            adapterProductList = new AdapterProductList(ProductsList.this, product);//адаптер для ресайклера
            recyclerProd.setAdapter(adapterProductList);//подсоединяем адаптер к ресайклеру
        } else {
            adapterProductList.deleteFromListAdapter(positionDelete);
            positionDelete = -1;
        }
    }

    @Override
    public void onProductDeleted() {
    }

    //добавили продукт в таблицу товаров
    @Override
    public void onProductAdded() {
        //получаем последний добавленный продукт
        requestsLists.getlastProduct(ProductsList.this);
    }

    //получаем последнюю запись из таблици продуктов
    @Override
    public void onLastProduct(int idProduct) {
        //добавляем в таблицу "товары в списке"
        ProductForList productForList= new ProductForList(idList, idProduct);
        requestsLists.addProductForList(ProductsList.this, productForList);
    }

    //добавили запись в таблицу "товары в списке"
    @Override
    public void onProductForListAdded() {
        Log.d("lll", "добавили запись в таблицу \"товары в списке\"");
    }

    @Override
    public void onDataNotAvailable() {
    }

    @Override
    public void onProductUpdated() {
    }

}