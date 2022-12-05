package com.jackie.treasuremarker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SpinnerAdapter;
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
import com.jackie.treasuremarker.databinding.ActivityModifyBinding;
import com.jackie.treasuremarker.databinding.LayoutDateSelectBinding;
import com.jackie.treasuremarker.utils.RequestCode;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ModifyActivity extends AppCompatActivity {
    private ActivityModifyBinding binding;
    private LayoutDateSelectBinding dateSelectBinding;
    private Bundle resultBundle = new Bundle();
    private final static String TAG = "ModifyActivity";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        binding.titleText.setText(bundle.getString("title"));
        binding.addressText.setText(bundle.getString("address"));

        SpinnerAdapter adapter = binding.categoryType.getAdapter();
        int cnt = adapter.getCount();
        for (int i = 0; i < cnt; ++i) {
            String strType = adapter.getItem(i).toString().toUpperCase();
            if (bundle.getString("type").equals(strType)) {
                binding.categoryType.setSelection(i);
                break;
            }
        }

        if (bundle.getParcelable("img") != null) {
            binding.image.setImageURI((Uri) bundle.getParcelable("img"));
            binding.image.setBackground(null);
        }

        if (bundle.getSerializable("date") != null) {
            ConstraintLayout layout = binding.modifyActivityRoot;
            dateSelectBinding = LayoutDateSelectBinding.inflate(getLayoutInflater());
            ConstraintSet constraintSet = new ConstraintSet();

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layout.addView(dateSelectBinding.getRoot(), layoutParams);
            constraintSet.clone(layout);
            constraintSet.connect(R.id.layout_date_select, ConstraintSet.LEFT, R.id.modify_activity_root, ConstraintSet.LEFT);
            constraintSet.connect(R.id.layout_date_select, ConstraintSet.RIGHT, R.id.modify_activity_root, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.layout_date_select, ConstraintSet.TOP, R.id.image_setting_layout, ConstraintSet.BOTTOM);
            layout.setConstraintSet(constraintSet);

            Date date = (Date) bundle.getSerializable("date");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(date);
            dateSelectBinding.dateText.setText(dateString);

            dateSelectBinding.pickBtn.setOnClickListener(v -> {
                TimePickerBuilder builder = new TimePickerBuilder(ModifyActivity.this, (d, view) -> {
                    String strDate = dateFormat.format(d);
                    dateSelectBinding.dateText.setText(strDate);
                    resultBundle.putSerializable("date", d);
                });
                builder.setType(new boolean[]{true, true, true, true, true, true});
                TimePickerView timePickerView = builder.build();
                timePickerView.show();
            });
        }

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.submitBtn.setOnClickListener(v -> {
            Intent intent = new Intent();

            resultBundle.putInt("request_code", RequestCode.CARD_MODIFIED);
            resultBundle.putString("title", binding.titleText.getText().toString());
            resultBundle.putString("address", binding.addressText.getText().toString());
            resultBundle.putString("type", binding.categoryType.getSelectedItem().toString());
            intent.putExtras(resultBundle);

            setResult(Activity.RESULT_OK, intent);

            finish();
        });

        binding.image.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ModifyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ModifyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RequestCode.EXTERNAL_STORAGE);
            }
            else {
                createPhotoPicker();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.MATISSE && resultCode == RESULT_OK) {
            assert data != null;
            List<Uri> uris = Matisse.obtainResult(data);
            resultBundle.putParcelable("uri", uris.get(0));
            Log.i(TAG, "Selected: " + uris);

            Glide.with(ModifyActivity.this).load(uris.get(0)).into(binding.image);
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
        Matisse.from(ModifyActivity.this)
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
