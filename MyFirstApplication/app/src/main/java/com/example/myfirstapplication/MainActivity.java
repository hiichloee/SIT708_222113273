package com.example.myfirstapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }


    private Spinner spinnerFrom, spinnerTo;
    private EditText inputValue;
    private TextView resultText;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI组件
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);

        // 设置Spinner适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // **防止选择分类标题**
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.contains("----") || selectedItem.contains("Category")) {
                    spinnerFrom.setSelection(0); // 让它默认选中第一个可用单位
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.contains("----") || selectedItem.contains("Category")) {
                    spinnerTo.setSelection(0); // 让它默认选中第一个可用单位
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 转换按钮点击事件
        convertButton.setOnClickListener(v -> convertUnits());
    }




    //    Validation and Error Handling 验证和错误处理
    private void convertUnits() {
        // 获取输入值
        String input = inputValue.getText().toString().trim();
        if (input.isEmpty()) {
            resultText.setText(getString(R.string.error_empty_input));
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            resultText.setText(getString(R.string.error_invalid_input));
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        // 获取单位
        String sourceUnit = spinnerFrom.getSelectedItem().toString();
        String targetUnit = spinnerTo.getSelectedItem().toString();

        // 验证单位是否相同
        if (sourceUnit.equals(targetUnit)) {
            resultText.setText(getString(R.string.error_same_units));
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        // 验证单位类型是否匹配
        String sourceCategory = getUnitCategory(sourceUnit);
        String targetCategory = getUnitCategory(targetUnit);
        if (!sourceCategory.equals(targetCategory)) {
            resultText.setText(getString(R.string.error_type_input));
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        // 执行转换
        double result = calculateConversion(value, sourceUnit, targetUnit);
        String resultString = String.format("%.2f %s = %.2f %s", value, sourceUnit, result, targetUnit);
        resultText.setText(resultString);
        resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    // 获取单位类别
    private String getUnitCategory(String unit) {
        switch (unit) {
            case "Inch":
            case "Foot":
            case "Yard":
            case "Mile":
            case "Centimeter":
            case "Kilometer":
                return "Length";
            case "Pound":
            case "Ounce":
            case "Ton":
            case "Kilogram":
            case "Gram":
                return "Weight";
            case "Celsius":
            case "Fahrenheit":
            case "Kelvin":
                return "Temperature";
            default:
                return "";
        }
    }

    // 计算转换结果
    private double calculateConversion(double value, String sourceUnit, String targetUnit) {
        String category = getUnitCategory(sourceUnit);
        switch (category) {
            case "Length":
                return convertLength(value, sourceUnit, targetUnit);
            case "Weight":
                return convertWeight(value, sourceUnit, targetUnit);
            case "Temperature":
                return convertTemperature(value, sourceUnit, targetUnit);
            default:
                return 0;
        }
    }

    // 长度转换
    private double convertLength(double value, String sourceUnit, String targetUnit) {
        // 转换为基准单位（米）
        double meters = 0;
        switch (sourceUnit) {
            case "Inch":    meters = value * 0.0254; break;
            case "Foot":    meters = value * 0.3048; break;
            case "Yard":    meters = value * 0.9144; break;
            case "Mile":    meters = value * 1609.34; break;
            case "Centimeter": meters = value * 0.01; break;
            case "Kilometer": meters = value * 1000; break;
        }

        // 转换为目标单位
        switch (targetUnit) {
            case "Inch":    return meters / 0.0254;
            case "Foot":    return meters / 0.3048;
            case "Yard":    return meters / 0.9144;
            case "Mile":    return meters / 1609.34;
            case "Centimeter": return meters * 100;
            case "Kilometer": return meters / 1000;
            default: return 0;
        }
    }

    // 重量转换
    private double convertWeight(double value, String sourceUnit, String targetUnit) {
        // 转换为基准单位（千克）
        double kilograms = 0;
        switch (sourceUnit) {
            case "Pound": kilograms = value * 0.453592; break;
            case "Ounce": kilograms = value * 0.0283495; break;
            case "Ton": kilograms = value * 907.185; break;
            case "Kilogram": kilograms = value; break;
            case "Gram": kilograms = value * 0.001; break;
        }

        // 转换为目标单位
        switch (targetUnit) {
            case "Pound": return kilograms / 0.453592;
            case "Ounce": return kilograms / 0.0283495;
            case "Ton": return kilograms / 907.185;
            case "Kilogram": return kilograms;
            case "Gram": return kilograms * 1000;
            default: return 0;
        }
    }

    // 温度转换
    private double convertTemperature(double value, String sourceUnit, String targetUnit) {
        if ("Celsius".equals(sourceUnit)) {
            if ("Fahrenheit".equals(targetUnit)) return (value * 1.8) + 32;
            if ("Kelvin".equals(targetUnit)) return value + 273.15;
        } else if ("Fahrenheit".equals(sourceUnit)) {
            if ("Celsius".equals(targetUnit)) return (value - 32) / 1.8;
            if ("Kelvin".equals(targetUnit)) return (value - 32) / 1.8 + 273.15;
        } else if ("Kelvin".equals(sourceUnit)) {
            if ("Celsius".equals(targetUnit)) return value - 273.15;
            if ("Fahrenheit".equals(targetUnit)) return (value - 273.15) * 1.8 + 32;
        }
        return value;
    }

}