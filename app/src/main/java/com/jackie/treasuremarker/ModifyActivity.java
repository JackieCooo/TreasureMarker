package com.jackie.treasuremarker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SpinnerAdapter;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.jackie.treasuremarker.databinding.ActivityModifyBinding;

public class ModifyActivity extends AppCompatActivity {
    private ActivityModifyBinding binding;

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
            Intent intent = new Intent("com.jackie.treasuremarker.ACTION_CARD_MODIFIED");
            Bundle b = new Bundle();

            b.putString("title", binding.titleText.getText().toString());
            b.putString("address", binding.addressText.getText().toString());
            b.putString("type", binding.categoryType.getSelectedItem().toString());
            b.putInt("index", bundle.getInt("index"));
            intent.putExtras(b);

            sendBroadcast(intent);

            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
