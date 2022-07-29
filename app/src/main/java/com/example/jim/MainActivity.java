package com.example.jim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar materialToolbar;
    private DrawerLayout navDrawer;
    private NavigationView navView;
    private Menu navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialToolbar = findViewById(R.id.materialToolbar);
        navDrawer = findViewById(R.id.navDrawer);
        navDrawer.closeDrawer(GravityCompat.START);
        navView = findViewById(R.id.navView);
        navMenu = navView.getMenu();

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!navDrawer.isOpen()) {
                    navDrawer.open();
                } else {
                    navDrawer.close();
                }
            }
        });

        setExcuteFragment();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.codeFun: setCodeFragment(); return true;
                    case  R.id.excuteFun: setExcuteFragment(); return true;
                }

                return false;
            }
        });

    }

    private void setExcuteFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExcuteFragment.class, null)
                .addToBackStack(null)
                .commit();

        navView.setCheckedItem(navMenu.findItem(R.id.excuteFun));
        if (navDrawer.isOpen()) navDrawer.close();
    }

    private void setCodeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CodeFragment.class, null)
                .addToBackStack(null)
                .commit();

        navView.setCheckedItem(navMenu.findItem(R.id.codeFun));
        if (navDrawer.isOpen()) navDrawer.close();
    }
}