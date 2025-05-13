package com.example.personallearningapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class UpgradeActivity extends AppCompatActivity{

    TextView btnStarter, btnIntermediate, btnAdvanced;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        btnBack = findViewById(R.id.btnBack);
        btnStarter = findViewById(R.id.btn_starter);
        btnIntermediate = findViewById(R.id.btn_intermediate);
        btnAdvanced = findViewById(R.id.btn_advanced);

        btnStarter.setOnClickListener(v -> simulatePayment("Starter"));
        btnIntermediate.setOnClickListener(v -> simulatePayment("Intermediate"));
        btnAdvanced.setOnClickListener(v -> simulatePayment("Advanced"));


        btnBack.setOnClickListener(v -> finish());
    }

    private void simulatePayment(String plan) {
//        Toast.makeText(this, "Redirecting to Google Pay for: " + plan, Toast.LENGTH_SHORT).show();
//        // TODO: Integrate with real Google Pay

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fake_payment, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            Toast.makeText(this, plan + " Plan Activated!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

}
