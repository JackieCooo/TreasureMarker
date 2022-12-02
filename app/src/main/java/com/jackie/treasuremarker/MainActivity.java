package com.jackie.treasuremarker;

import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.jackie.treasuremarker.databinding.ActivityMainBinding;
import com.jackie.treasuremarker.service.NotificationService;
import com.jackie.treasuremarker.ui.card.CardViewModel;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private ActivityMainBinding binding;
    private CardViewModel model;
    private NotificationService.NotificationBinder notificationBinder;
    private MainActivityReceiver receiver;
    private final static String TAG = "MainActivity";

    private class MainActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.jackie.treasuremarker.DATE_SET")) {
                Bundle bundle = intent.getExtras();
                notificationBinder.appendNotification(bundle.getString("title"), (Date) bundle.getSerializable("date"));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init view model");
        model = new ViewModelProvider(this).get(CardViewModel.class);
        model.load();

        Log.i(TAG, "Init service");
        Intent intent = new Intent(MainActivity.this, NotificationService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        Log.i(TAG, "Init broadcast");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.jackie.treasuremarker.DATE_SET");
        receiver = new MainActivityReceiver();
        registerReceiver(receiver, intentFilter);

        Log.i(TAG, "Init content view");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i(TAG, "Init navigation");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        unregisterReceiver(receiver);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        notificationBinder = (NotificationService.NotificationBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}