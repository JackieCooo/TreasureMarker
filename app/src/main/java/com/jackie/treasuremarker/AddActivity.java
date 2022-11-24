package com.jackie.treasuremarker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.jackie.treasuremarker.databinding.ActivityAddBinding;
import com.jackie.treasuremarker.databinding.LayoutDateSelectBinding;
import com.jackie.treasuremarker.utils.RequestCode;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private ActivityAddBinding binding;
    private LayoutDateSelectBinding dateSelectBinding;
    private final static String TAG = "AddActivity";

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

        binding.image.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.EXTERNAL_STORAGE);
            }
            else {
                createPhotoPicker();
            }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.MATISSE && resultCode == RESULT_OK) {
            assert data != null;
            List<Uri> uris = Matisse.obtainResult(data);
            Log.i(TAG, "Selected: " + uris);

            Glide.with(AddActivity.this).load(uris.get(0)).into(binding.image);
            binding.image.setBackground(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode.EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPhotoPicker();
            }
            else {
                Log.i(TAG, "External storage permission denied");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        dateSelectBinding = null;
    }

    private void createPhotoPicker() {
        Matisse.from(AddActivity.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(360)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .theme(com.zhihu.matisse.R.style.Matisse_Zhihu)
                .imageEngine(new GlideEngine())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.jackie.treasuremarker.fileprovider"))
                .forResult(RequestCode.MATISSE);
    }
}
