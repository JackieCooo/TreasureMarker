package com.jackie.treasuremarker.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jackie.treasuremarker.AddActivity;
import com.jackie.treasuremarker.MainActivity;
import com.jackie.treasuremarker.R;
import com.jackie.treasuremarker.databinding.FragmentHomeBinding;
import com.jackie.treasuremarker.ui.card.*;
import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CardViewModel model;
    private final static String TAG = "HomeFragment";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(CardViewModel.class);
//        model.load();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LinearLayout holder = binding.homeCardHolder;
        Button addBtn = binding.addBtn;
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });

        return root;
    }
}