package com.example.electricbill;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView tvSummary, tvRebate;
    Spinner spinnerMonth;
    EditText etUnits;
    SeekBar seekRebate;
    Button btnUpdate, btnDelete;
    DataHelper dbHelper;
    int recordId;
    String[] months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Bill Details");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DataHelper(this);
        recordId = getIntent().getIntExtra("id", -1);
        months = getResources().getStringArray(R.array.months);

        tvSummary = findViewById(R.id.tvSummary);
        tvRebate = findViewById(R.id.tvRebate);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        seekRebate = findViewById(R.id.seekRebate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        seekRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebate.setText("Rebate: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        loadRecord();

        btnUpdate.setOnClickListener(v -> updateRecord());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadRecord() {
        Cursor c = dbHelper.getBill(recordId);
        if (c.moveToFirst()) {
            String month = c.getString(c.getColumnIndexOrThrow("month"));
            int units = c.getInt(c.getColumnIndexOrThrow("units"));
            double total = c.getDouble(c.getColumnIndexOrThrow("total"));
            int rebate = c.getInt(c.getColumnIndexOrThrow("rebate"));
            double finalCost = c.getDouble(c.getColumnIndexOrThrow("final_cost"));

            tvSummary.setText(String.format(Locale.US,
                    "Month: %s\nUnits used: %d kWh\nTotal charges: RM %.2f\nRebate: %d%% (- RM %.2f)\nFinal cost: RM %.2f",
                    month, units, total, rebate, total - finalCost, finalCost));

            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    spinnerMonth.setSelection(i);
                    break;
                }
            }
            etUnits.setText(String.valueOf(units));
            seekRebate.setProgress(rebate);
            tvRebate.setText("Rebate: " + rebate + "%");
        } else {
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        c.close();
    }

    private void updateRecord() {
        int units = BillCalculator.readUnits(etUnits);
        if (units < 0) return;

        int rebate = seekRebate.getProgress();
        double total = BillCalculator.calculateTotal(units);
        double finalCost = BillCalculator.applyRebate(total, rebate);
        String month = spinnerMonth.getSelectedItem().toString();

        dbHelper.updateBill(recordId, month, units, total, rebate, finalCost);
        Toast.makeText(this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete record")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBill(recordId);
                    Toast.makeText(this, "Data Successfully Removed", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}