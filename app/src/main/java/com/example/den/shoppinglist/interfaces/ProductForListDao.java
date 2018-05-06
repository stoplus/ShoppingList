package com.example.den.shoppinglist.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.den.shoppinglist.entity.ProductForList;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ProductForListDao {
    @Query("SELECT * FROM productforlist")
    Flowable<List<ProductForList>> getAllProductForList();

    @Query("SELECT * FROM productforlist WHERE id = :id")
    Flowable<ProductForList> getById(int... id);


//    @Query("SELECT * FROM ProductForList WHERE id_list = :idList")
//    Flowable<List<ProductForList>> getProductsInList(int idList);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProductForList... productForLists);

    @Update
    void update(ProductForList productForList);

    @Delete
    void delete(ProductForList... productForLists);
}
