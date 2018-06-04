package ru.deliveon.lists.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import ru.deliveon.lists.entity.Lists;

@Dao
public interface ListsDao {
    @Query("SELECT * FROM lists")
    Flowable<List<Lists>> getAll();

    @Query("SELECT COUNT(*) from lists")
    int count();

    @Query("SELECT * FROM lists WHERE id = :id")
    Flowable<Lists> getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lists lists);

    @Update
    void update(Lists lists);

    @Delete
    void delete(Lists... lists);
}
