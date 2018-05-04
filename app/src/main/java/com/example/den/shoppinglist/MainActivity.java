package com.example.den.shoppinglist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.idRecycler)
    RecyclerView recycler;
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;

    @Inject ListsDao listsDao;
    @Inject ListProductDao listProductDao;

//    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.app().getComponent().inject(this);
        setSupportActionBar(toolbar);
        toolbarTitle.setText(getResources().getString(R.string.lists));



        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showAddListDialog();
            }
        });

//        db =  Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "database").build();
//        db = App.getComponent().getAppDatabase();
//        ListsDao lists = db.listsDao();
//        ListProductDao listProductDao = db.listProductDao();

    }
}
