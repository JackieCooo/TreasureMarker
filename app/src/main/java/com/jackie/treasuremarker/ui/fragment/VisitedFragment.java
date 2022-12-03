package com.jackie.treasuremarker.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jackie.treasuremarker.MainActivity;
import com.jackie.treasuremarker.R;
import com.jackie.treasuremarker.databinding.FragmentVisitedBinding;
import com.jackie.treasuremarker.databinding.LayoutVisitedCardBinding;
import com.jackie.treasuremarker.ui.card.CardInfo;
import com.jackie.treasuremarker.ui.card.CardViewModel;
import com.jackie.treasuremarker.ui.card.CategoryType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class VisitedFragment extends Fragment {
    private FragmentVisitedBinding binding;
    private CardViewModel model;
    private LinearLayout cardHolder;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentVisitedBinding.inflate(getLayoutInflater());
        cardHolder = binding.visitedCardHolder;
        model = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        refreshCardList(0);

        binding.visitedTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshCardList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateCardByModel(LayoutVisitedCardBinding view, CardInfo info) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        view.visitedCardTitle.setText(info.getTitle());
        view.visitedCardAddress.setText(info.getAddress());
        int color;
        switch (info.getType()) {
            case FOODS:
                color = activity.getColor(R.color.color_category_foods);
                break;
            case TRAVEL:
                color = activity.getColor(R.color.color_category_travel);
                break;
            case SPORTS:
                color = activity.getColor(R.color.color_category_sports);
                break;
            case PHOTOGRAPHY:
                color = activity.getColor(R.color.color_category_photography);
                break;
            case ENTERTAINMENT:
                color = activity.getColor(R.color.color_category_entertainment);
                break;
            case UNCATEGORIZED:
                color = activity.getColor(R.color.color_category_uncategorized);
                break;
            default:
                color = 0;
        }
        view.visitedCardCategoryTag.setBackgroundColor(color);
        if (info.getPicUri() != null) {
            view.visitedCardImage.setImageURI(info.getPicUri());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ConstraintLayout createCard(CardInfo info) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        LayoutVisitedCardBinding cardBinding = LayoutVisitedCardBinding.inflate(getLayoutInflater());
        updateCardByModel(cardBinding, info);

        cardBinding.visitedCardDelBtn.setOnClickListener(v -> {
            info.setDate(null);
            cardHolder.removeView(cardBinding.getRoot());
        });

        return cardBinding.getRoot();
    }

    public void appendCard(CardInfo info) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = 20;
        cardHolder.addView(createCard(info), layoutParams);
    }

    public void refreshCardList(int pos) {
        assert model.getInfo().getValue() != null;
        cardHolder.removeAllViews();
        LinkedList<CardInfo> value = model.getInfo().getValue();
        for (CardInfo i : value) {
            if (i.getVisited() && isTypeMatched(i.getType(), pos)) {
                appendCard(i);
            }
        }
    }

    private boolean isTypeMatched(CategoryType type, int pos) {
        return (type == CategoryType.FOODS && pos == 1) || (type == CategoryType.TRAVEL && pos == 2) ||(type == CategoryType.SPORTS && pos == 3) ||(type == CategoryType.PHOTOGRAPHY && pos == 4) ||(type == CategoryType.ENTERTAINMENT && pos == 5) || pos == 0;
    }
}
