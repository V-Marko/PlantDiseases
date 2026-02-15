// BaseActivity.java
package com.example.plantdiseases;

import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {

    protected void updateNavigationMenuText(NavigationView navigationView, String language) {
        if (navigationView != null) {
            Menu menu = navigationView.getMenu();

            if ("en".equals(language)) {
                menu.findItem(R.id.nav_home).setTitle("Home");
                menu.findItem(R.id.nav_account).setTitle("My Account");
                menu.findItem(R.id.nav_settings).setTitle("App Settings");
            } else if ("hy".equals(language)) {
                menu.findItem(R.id.nav_home).setTitle("Տուն");
                menu.findItem(R.id.nav_account).setTitle("Իմ հաշիվը");
                menu.findItem(R.id.nav_settings).setTitle("Ծրագրի կարգավորումներ");
            } else {
                menu.findItem(R.id.nav_home).setTitle("Дом");
                menu.findItem(R.id.nav_account).setTitle("Мой аккаунт");
                menu.findItem(R.id.nav_settings).setTitle("Настройки приложения");
            }
        }
    }
}