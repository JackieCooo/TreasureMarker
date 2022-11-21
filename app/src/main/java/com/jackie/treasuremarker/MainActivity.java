package com.jackie.treasuremarker;

import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.jackie.treasuremarker.databinding.ActivityMainBinding;
import com.jackie.treasuremarker.ui.card.CardViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CardViewModel model;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init content view");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i(TAG, "Init card view model");
        model = new ViewModelProvider(this).get(CardViewModel.class);
        model.setApplication(getApplication());
        model.load();

        Log.i(TAG, "Init navigation");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}