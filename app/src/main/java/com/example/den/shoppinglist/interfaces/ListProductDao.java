package com.example.den.shoppinglist.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.den.shoppinglist.entity.ListProduct;

import java.util.List;

@Dao
public interface ListProductDao {

    @Query("SELECT * FROM listproduct")
    List<ListProduct> getAll();

    @Query("SELECT * FROM listproduct WHERE id = :id")
    ListProduct getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ListProduct... listProduct);

    @Update
    void update(ListProduct listProduct);

    @Delete
    void delete(ListProduct... listProduct);
}
