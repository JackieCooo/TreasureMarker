package com.jackie.treasuremarker;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.jackie.treasuremarker.databinding.ActivityModifyBinding;
import com.jackie.treasuremarker.utils.RequestCode;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyActivity extends AppCompatActivity {
    private ActivityModifyBinding binding;
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
            if (bundle.getString("type").equals(adapter.getItem(i).toString())) {
                binding.categoryType.setSelection(i);
                break;
            }
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
            resultBundle.putInt("index", bundle.getInt("index"));
            intent.putExtras(resultBundle);

            setResult(Activity.RESULT_OK, intent);

            finish();
        });

        binding.image.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ModifyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ModifyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.EXTERNAL_STORAGE);
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
