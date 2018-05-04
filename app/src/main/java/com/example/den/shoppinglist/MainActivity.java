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
import android.widget.TextView;
import android.widget.Toast;

import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DefaultSubscriber;

public class MainActivity extends AppCompatActivity {
    private List<Lists> list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;

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
                showAddListDialog();
            }
        });

        listsDao.getAll()
                .observeOn(AndroidSchedulers.mainThread())//чтобы результат пришел в UI
                .subscribe(new Consumer<List<Lists>>() {
                    @Override
                    public void accept(List<Lists> newlist) throws Exception {
                        Log.d("fff", newlist.toString());
                        list = newlist;
                        AdapterList adapter = new AdapterList(MainActivity.this, list);//адаптер для ресайклера
                        recyclerView.setAdapter(adapter);//подсоединяем адаптер к ресайклеру
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
//                                int carId = list.get(position).getListId();//получаем id транспорта
                                switch (item.getItemId()) {
                                    case R.id.edit:
//                                        if (Utils.isOnline(context)) // если есть подключение к интернету
//                                            editDataCar(carId);//редактировании данных транспорта
                                        return true;
                                    case R.id.delete:
//                                        DeleteCarDialog deleteDialog = new DeleteCarDialog();
//                                        Bundle args = new Bundle();
//                                        args.putString("carId", carId);
//                                        deleteDialog.setArguments(args);
//                                        deleteDialog.show(getSupportFragmentManager(), "deleteCar");
//                                        //deleteCar(carId);//удаление транспорта
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

    public void showAddListDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("Название");
        alertDialog.setTitle("Создание нового списка")
                .setView(input)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            addList(name);
                            Toast.makeText(MainActivity.this, "Добавили", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void addList(final String name) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Lists lists = new Lists(name);
                listsDao.insert(lists);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
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



}
