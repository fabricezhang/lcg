package top.easelink.lcg.ui.splash.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.view.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(() -> {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }, 200);
    }
}
