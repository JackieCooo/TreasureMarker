package com.jackie.treasuremarker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.jackie.treasuremarker.databinding.ActivityAddBinding;
import com.jackie.treasuremarker.databinding.LayoutDateSelectBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private ActivityAddBinding binding;
    private LayoutDateSelectBinding dateSelectBinding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dateSelectBinding = LayoutDateSelectBinding.inflate(getLayoutInflater());

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.submitBtn.setOnClickListener(v -> {
            Intent intent = new Intent("com.jackie.treasuremarker.ACTION_CARD_ADDED");
            Bundle bundle = new Bundle();

            bundle.putString("title", binding.titleText.getText().toString());
            bundle.putString("address", binding.addressText.getText().toString());
            bundle.putString("type", binding.categoryType.getSelectedItem().toString());
            if (binding.alarmSwitch.isChecked()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = null;
                try {
                    d = dateFormat.parse(dateSelectBinding.dateText.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                bundle.putSerializable("alarm", d);
            }
            else {
                bundle.putSerializable("alarm", null);
            }
            intent.putExtras(bundle);

            sendBroadcast(intent);

            finish();
        });

        binding.alarmSwitch.setOnCheckedChangeListener((v, c) -> {
            ConstraintLayout layout = binding.addActivityRoot;
            ConstraintSet constraintSet = new ConstraintSet();
            if (c) {
                layout.addView(dateSelectBinding.getRoot());
                constraintSet.clone(layout);
                constraintSet.connect(R.id.layout_date_select, ConstraintSet.LEFT, R.id.add_activity_root, ConstraintSet.LEFT);
                constraintSet.connect(R.id.layout_date_select, ConstraintSet.RIGHT, R.id.add_activity_root, ConstraintSet.RIGHT);
                constraintSet.connect(R.id.layout_date_select, ConstraintSet.TOP, R.id.alarm_setting_layout, ConstraintSet.BOTTOM);
                layout.setConstraintSet(constraintSet);
            }
            else {
                constraintSet.clone(layout);
                constraintSet.clear(R.id.layout_date_select);
                layout.setConstraintSet(constraintSet);
                layout.removeView(dateSelectBinding.getRoot());
            }
        });

        dateSelectBinding.pickBtn.setOnClickListener(v -> {
            TimePickerBuilder builder = new TimePickerBuilder(AddActivity.this, (date, view) -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(date);
                dateSelectBinding.dateText.setText(dateString);
            });
            builder.setType(new boolean[]{true, true, true, true, true, true});
            TimePickerView timePickerView = builder.build();
            timePickerView.show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        dateSelectBinding = null;
    }
}
