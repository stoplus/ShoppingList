package ru.deliveon.lists.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import ru.deliveon.lists.entity.ProductForList;

@Dao
public interface ProductForListDao {
    @Query("SELECT * FROM productforlist")
    Flowable<List<ProductForList>> getAllProductForList();

    @Query("SELECT * FROM productforlist WHERE id = :id")
    Flowable<ProductForList> getById(int... id);

    //looking for a record (s) with the same product id
    @Query("SELECT * FROM ProductForList WHERE id_product = :idProduct")
    Flowable<List<ProductForList>> getSameIdProductForList(int idProduct);

    //Looking for a record (s) with the same list id
    @Query("SELECT * FROM ProductForList WHERE id_list = :idList")
    Flowable<List<ProductForList>> getSameIdListForList(int idList);

    //Looking for a record (s) with the same list id
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

    // delete list id
    @Query("DELETE from ProductForList WHERE id IN (:idList)")
    int deleteByIdList(List<Integer> idList);
}
