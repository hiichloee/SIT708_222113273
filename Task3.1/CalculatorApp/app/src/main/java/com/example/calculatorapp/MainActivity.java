package com.example.calculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNum1, etNum2;
    private Button btnAdd, btnSubtract;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI components
        etNum1 = findViewById(R.id.etNum1);
        etNum2 = findViewById(R.id.etNum2);
        btnAdd = findViewById(R.id.btnAdd);
        btnSubtract = findViewById(R.id.btnSubtract);
        tvResult = findViewById(R.id.tvResult);

        // Addition button click event
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('+');
            }
        });

        // Addition button click event
        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('-');
            }
        });
    }

    // Perform addition and subtraction calculations
    private void calculate(char operation) {
        String num1Str = etNum1.getText().toString().trim();
        String num2Str = etNum2.getText().toString().trim();

        //  Check if input is empty
        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter both numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to floating-point numbers
        try {
            double num1 = Double.parseDouble(num1Str);
            double num2 = Double.parseDouble(num2Str);
            double result;

            if (operation == '+') {
                result = num1 + num2;
            } else {
                result = num1 - num2;
            }

            tvResult.setText("Result: " + result);

        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid input! Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}

