package com.bitspilani.inventorytrackerjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bitspilani.inventorytrackerjava.model.Adapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;
    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteLists = findViewById(R.id.notelist);

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close );
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();

        titles.add("first note title");
        content.add("first note Content sample");

        titles.add("second note title");
        content.add("second note Content sample.second note Content sample.second note Content sample.second note Content sample.second note Content sample.second note Content sample.second note Content sample.second note Content sample.second note Content sample.");

        titles.add("third note title");
        content.add("third note Content sample");

        adapter = new Adapter(titles,content);
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addNote:
                startActivity(new Intent(this,AddNote.class));
            default:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Toast.makeText(this, "Settings Menu is clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
