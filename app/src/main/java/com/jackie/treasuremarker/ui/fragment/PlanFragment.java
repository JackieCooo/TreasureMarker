package com.jackie.treasuremarker.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jackie.treasuremarker.MainActivity;
import com.jackie.treasuremarker.R;
import com.jackie.treasuremarker.databinding.FragmentPlanBinding;
import com.jackie.treasuremarker.databinding.LayoutPlanCardBinding;
import com.jackie.treasuremarker.ui.card.CardInfo;
import com.jackie.treasuremarker.ui.card.CardViewModel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlanFragment extends Fragment {
    private FragmentPlanBinding binding;
    private LinearLayout cardHolder;
    private CardViewModel model;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        cardHolder = binding.planCardHolder;
        refreshCardList();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateCardByModel(LayoutPlanCardBinding view, CardInfo info) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        view.planCardTitle.setText(info.getTitle());
        view.planCardAddress.setText(info.getAddress());
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
        view.planCardCategoryTag.setBackgroundColor(color);
        if (info.getDate() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            view.planCardDateText.setText(dateFormat.format(info.getDate()));
        }
        if (info.getPicUri() != null) {
            view.planCardImage.setImageURI(info.getPicUri());
        }
        if (info.getVisited()) {
            view.planCardCheckedBtn.setChecked(true);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ConstraintLayout createCard(CardInfo info) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        LayoutPlanCardBinding cardBinding = LayoutPlanCardBinding.inflate(getLayoutInflater());
        updateCardByModel(cardBinding, info);

        cardBinding.planCardDelBtn.setOnClickListener(v -> {
            info.setDate(null);
            cardHolder.removeView(cardBinding.getRoot());
        });

        cardBinding.planCardCheckedBtn.setOnCheckedChangeListener((v, c) -> {
            if (c) {
                v.setBackground(activity.getDrawable(R.drawable.ic_checked));
                info.setVisited(true);
            }
            else {
                v.setBackground(activity.getDrawable(R.drawable.ic_unchecked));
                info.setVisited(false);
            }
        });

        return cardBinding.getRoot();
    }

    public void appendCard(CardInfo info) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = 20;
        cardHolder.addView(createCard(info), layoutParams);
    }

    public void refreshCardList() {
        assert model.getInfo().getValue() != null;
        cardHolder.removeAllViews();
        List<CardInfo> value = model.getInfo().getValue();
        value.sort(Comparator.comparing(CardInfo::getDate));
        for (CardInfo i : value) {
            if (i.getDate() != null) {
                appendCard(i);
            }
        }
    }
}