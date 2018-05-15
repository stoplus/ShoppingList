package com.example.den.shoppinglist.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.entity.ProductForList;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM Product")
    Flowable<List<Product>> getAll();

    @Query("SELECT * FROM Product WHERE id = :id")
    Flowable<Product> getById(int... id);


    @Query("SELECT * FROM Product WHERE id IN (SELECT id_product FROM ProductForList WHERE id_list = :id)")
    Flowable<List<Product>> getAllFromList(int id);

    @Query("SELECT MAX(id) FROM Product")
    Flowable<Integer> getlastProduct();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product... product);

    @Update
    void update(Product... product);

    //обновление по списку продуктов
    @Update
    void updateList(List<Product> productList);

    @Delete
    void delete(Product... product);

    @Delete
    void deleteListProduct(List<Product> list);

    //удаление по списку id
    @Query("DELETE from Product WHERE id IN (:idList)")
    int deleteByIdList(List<Integer> idList);
}
