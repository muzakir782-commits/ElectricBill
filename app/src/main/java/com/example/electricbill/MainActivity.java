package com.example.electricbill;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnits;
    SeekBar seekRebate;
    TextView tvRebate, tvTotal, tvFinal;
    Button btnCalculate, btnSave, btnHistory, btnAbout;
    DataHelper dbHelper;

    int lastUnits, lastRebate;
    double lastTotal, lastFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Bolt symbol next to the title in the top bar
        Drawable bolt = ContextCompat.getDrawable(this, R.drawable.ic_bolt);
        if (bolt != null && getSupportActionBar() != null) {
            bolt = DrawableCompat.wrap(bolt.mutate());
            DrawableCompat.setTint(bolt, ContextCompat.getColor(this, R.color.brand_accent));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(bolt);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        dbHelper = new DataHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        seekRebate = findViewById(R.id.seekRebate);
        tvRebate = findViewById(R.id.tvRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
        btnHistory = findViewById(R.id.btnHistory);
        btnAbout = findViewById(R.id.btnAbout);

        seekRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebate.setText("Rebate: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnCalculate.setOnClickListener(v -> calculate());
        btnSave.setOnClickListener(v -> saveBill());
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AboutActivity.class)));
    }

    private boolean calculate() {
        int units = BillCalculator.readUnits(etUnits);
        if (units < 0) return false;

        int rebate = seekRebate.getProgress();
        double total = BillCalculator.calculateTotal(units);
        double finalCost = BillCalculator.applyRebate(total, rebate);

        tvTotal.setText(String.format(Locale.US, "Total charges: RM %.2f", total));
        tvFinal.setText(String.format(Locale.US, "Final cost: RM %.2f", finalCost));

        lastUnits = units;
        lastRebate = rebate;
        lastTotal = total;
        lastFinal = finalCost;
        return true;
    }

    private void saveBill() {
        if (!calculate()) return;
        String month = spinnerMonth.getSelectedItem().toString();
        long result = dbHelper.insertBill(month, lastUnits, lastTotal, lastRebate, lastFinal);
        if (result == -1) {
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data Successfully Added", Toast.LENGTH_SHORT).show();
        }
    }
}