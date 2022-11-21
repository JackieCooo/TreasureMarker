package com.jackie.treasuremarker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jackie.treasuremarker.databinding.FragmentHomeBinding;
import com.jackie.treasuremarker.ui.card.AlarmDate;
import com.jackie.treasuremarker.ui.card.CardInfo;
import com.jackie.treasuremarker.ui.card.CardViewModel;
import com.jackie.treasuremarker.ui.card.CategoryType;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CardViewModel model;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        model = new ViewModelProvider(this).get(CardViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout holder = binding.homeCardHolder;

        CardInfo card1 = new CardInfo("card1", "address1", CategoryType.FOODS, null);
        model.append(card1);
        CardInfo card2 = new CardInfo("card2", "address2", CategoryType.SPORTS, new AlarmDate(2022, 11, 21, 22, 18, 0));
        model.append(card2);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}