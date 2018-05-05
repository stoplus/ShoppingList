package com.example.den.shoppinglist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.AddEditListInterface;
import com.example.den.shoppinglist.interfaces.DeleteListInterface;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements DeleteListInterface, AddEditListInterface {
    private List<Lists> list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    private AdapterList adapter;
    private int positionDelete = -1;

    @Inject
    ListsDao listsDao;
    @Inject
    ListProductDao listProductDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.app().getComponent().inject(this);
        setSupportActionBar(toolbar);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditListDialog addEditListDialog = new AddEditListDialog();
                addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
            }
        });

        listsDao.getAll()
                .observeOn(AndroidSchedulers.mainThread())//чтобы результат пришел в UI
                .subscribe(new Consumer<List<Lists>>() {
                    @Override
                    public void accept(List<Lists> newlist) throws Exception {
                        Log.d("fff", newlist.toString());
                        list = newlist;
                        if (adapter == null || positionDelete == -1) {
                            adapter = new AdapterList(MainActivity.this, list);//адаптер для ресайклера
                            recyclerView.setAdapter(adapter);//подсоединяем адаптер к ресайклеру
                        } else {
                            adapter.deleteFromListAdapter(positionDelete);
                            positionDelete = -1;
                        }
                    }
                });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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
                                Lists lists = list.get(position);
                                Bundle args = new Bundle();
                                args.putParcelable("lists", lists);
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        AddEditListDialog addEditListDialog = new AddEditListDialog();
                                        addEditListDialog.setArguments(args);
                                        addEditListDialog.show(getSupportFragmentManager(), "addEditListDialog");
                                        return true;
                                    case R.id.delete:
                                        positionDelete = position;
                                        DeleteListDialog deleteDialog = new DeleteListDialog();
                                        deleteDialog.setArguments(args);
                                        deleteDialog.show(getSupportFragmentManager(), "deleteList");
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

    @Override
    public void addList(final Lists lists) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listsDao.insert(lists);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @Override
    public void update(Lists lists) {
        getUser(lists, false);
    }

    @Override
    public void deleteList(Lists lists) {
        getUser(lists, true);
    }

    private void getUser(final Lists lists, boolean delete) {
//        listsDao.getById(id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Lists>() {
//            @Override
//            public void accept(@io.reactivex.annotations.NonNull final Lists lists) throws Exception {
        if (lists != null)
            if (delete) {
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        listsDao.delete(lists);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d("fff", "deleteOnSubscribe");
                            }

                            @Override
                            public void onComplete() {
                                Log.d("fff", "deleteOnComplete");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("fff", "deleteOnError");
                            }
                        });
            } else {
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        listsDao.update(lists);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d("fff", "updateOnSubscribe");
                            }

                            @Override
                            public void onComplete() {
                                Log.d("fff", "updateOnComplete");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("fff", "updateOnError");
                            }
                        });
            }
//            }
//        });
    }


}
