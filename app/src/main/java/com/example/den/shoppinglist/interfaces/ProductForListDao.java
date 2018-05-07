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

    //ищем запись(и) с таким же id товара
    @Query("SELECT * FROM ProductForList WHERE id_product = :idProduct")
    Flowable<List<ProductForList>> getSameIdProductForList(int idProduct);

    //ищем запись(и) с таким же id списка
    @Query("SELECT * FROM ProductForList WHERE id_list = :idList")
    Flowable<List<ProductForList>> getSameIdListForList(int idList);

    //ищем запись(и) с таким же id списка
    @Query("SELECT * FROM ProductForList WHERE id_product IN (:list)")
    Flowable<List<ProductForList>> getInOtherLists(List<Integer> list);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProductForList productForLists);

    @Update
    void update(ProductForList productForList);


    @Delete
    void delete(ProductForList... productForLists);

    @Delete
    void deleteList(List<ProductForList> list);

    @Query("DELETE from ProductForList WHERE id_product =:idProduct AND id_list =:idList")
    int deleteByIdListAndIdProduct(int idProduct, int idList);

    //удаление по списку id
    @Query("DELETE from ProductForList WHERE id IN (:idList)")
    int deleteByIdList(List<Integer> idList);
}
