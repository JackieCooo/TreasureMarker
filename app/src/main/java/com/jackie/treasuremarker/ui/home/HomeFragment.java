package com.jackie.treasuremarker.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.jackie.treasuremarker.AddActivity;
import com.jackie.treasuremarker.MainActivity;
import com.jackie.treasuremarker.R;
import com.jackie.treasuremarker.ModifyActivity;
import com.jackie.treasuremarker.databinding.FragmentHomeBinding;
import com.jackie.treasuremarker.databinding.LayoutInfoCardBinding;
import com.jackie.treasuremarker.ui.card.*;
import com.jackie.treasuremarker.utils.RequestCode;

import java.util.Date;
import java.util.LinkedList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CardViewModel model;
    private LinearLayout cardHolder;
    private ActivityResultLauncher<Intent> launcher;
    private final static String TAG = "HomeFragment";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CardViewModel.class);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                assert result.getData() != null;
                Bundle bundle = result.getData().getExtras();
                int code = bundle.getInt("request_code");
                if (code == RequestCode.CARD_ADDED) {
                    CardInfo info = new CardInfo();
                    fillInfoByBundle(info, bundle);
                    Log.i(TAG, "Card added: " + info);

                    model.append(info);
                    appendCard(info);
                }
                else if (code == RequestCode.CARD_MODIFIED) {
                    LinkedList<CardInfo> value = model.getInfo().getValue();
                    assert value != null;
                    CardInfo info = value.get(bundle.getInt("index") - 1);
                    fillInfoByBundle(info, bundle);
                    Log.i(TAG, "Card modified: " + info);

                    View view = cardHolder.getChildAt(bundle.getInt("index"));
                    LayoutInfoCardBinding tarBinding = LayoutInfoCardBinding.bind(view);
                    updateCardByModel(tarBinding, info);
                }
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cardHolder = binding.homeCardHolder;
        refreshCardList();

        Button addBtn = binding.addBtn;
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            launcher.launch(intent);
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ConstraintLayout createCard(CardInfo info) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        LayoutInfoCardBinding cardBinding = LayoutInfoCardBinding.inflate(getLayoutInflater());
        updateCardByModel(cardBinding, info);

        cardBinding.cardAlarmBtn.setOnCheckedChangeListener((v, c) -> {
            CardInfo i = getModelByView(cardBinding.getRoot());
            assert i != null;
            if (c) {
                TimePickerBuilder builder = new TimePickerBuilder(getActivity(), (date, view) -> {
                    i.setDate(date);
                    cardBinding.cardAlarmBtn.setBackground(activity.getDrawable(R.drawable.ic_notifications_primary));
                });
                builder.setType(new boolean[]{true, true, true, true, true, true});
                TimePickerView timePickerView = builder.build();
                timePickerView.show();
            }
            else {
                cardBinding.cardAlarmBtn.setBackground(activity.getDrawable(R.drawable.ic_notifications_disable));
                i.setDate(null);
            }
        });

        cardBinding.cardModifyBtn.setOnClickListener(v -> {
            CardInfo i = getModelByView(cardBinding.getRoot());
            assert i != null;
            Intent intent = new Intent(getActivity(), ModifyActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("title", i.getTitle());
            bundle.putString("address", i.getAddress());
            bundle.putString("type", i.getType().toString());
            bundle.putInt("index", cardHolder.indexOfChild(cardBinding.getRoot()));
            intent.putExtras(bundle);

            launcher.launch(intent);
        });

        cardBinding.planCardDelBtn.setOnClickListener(v -> {
            delModelByView(cardBinding.getRoot());
            cardHolder.removeView(cardBinding.getRoot());
        });

        return cardBinding.getRoot();
    }

    private void fillInfoByBundle(CardInfo info, Bundle bundle) {
        info.setTitle(bundle.getString("title"));
        info.setAddress(bundle.getString("address"));
        CategoryType type;
        switch (bundle.getString("type")) {
            case "Foods":
                type = CategoryType.FOODS;
                break;
            case "Travel":
                type = CategoryType.TRAVEL;
                break;
            case "Sports":
                type = CategoryType.SPORTS;
                break;
            case "Photography":
                type = CategoryType.PHOTOGRAPHY;
                break;
            case "Entertainment":
                type = CategoryType.ENTERTAINMENT;
                break;
            case "Uncategorized":
                type = CategoryType.UNCATEGORIZED;
                break;
            default:
                type = null;
        }
        info.setType(type);
        info.setDate((Date) bundle.getSerializable("alarm"));
        info.setPicUri((Uri) bundle.getParcelable("uri"));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateCardByModel(LayoutInfoCardBinding view, CardInfo info) {
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
            view.cardAlarmBtn.setBackground(activity.getDrawable(R.drawable.ic_notifications_primary));
        }
        if (info.getPicUri() != null) {
            view.planCardImage.setImageURI(info.getPicUri());
        }
    }

    public void refreshCardList() {
        assert model.getInfo().getValue() != null;
        LinkedList<CardInfo> value = model.getInfo().getValue();
        for (CardInfo i : value) {
            appendCard(i);
        }
    }

    public void appendCard(CardInfo info) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = 20;
        cardHolder.addView(createCard(info), layoutParams);
    }

    private CardInfo getModelByView(View view) {
        int i = cardHolder.indexOfChild(view);
        if (i == -1) return null;
        LinkedList<CardInfo> value = model.getInfo().getValue();
        assert value != null;
        return value.get(i - 1);
    }

    private void delModelByView(View view) {
        int i = cardHolder.indexOfChild(view);
        if (i == -1) return;
        LinkedList<CardInfo> value = model.getInfo().getValue();
        assert value != null;
        value.remove(i - 1);
    }
}