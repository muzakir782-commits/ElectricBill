package com.example.electricbill;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    ListView lvBills;
    TextView tvEmpty;
    DataHelper dbHelper;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("History");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DataHelper(this);
        lvBills = findViewById(R.id.lvBills);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        lvBills.setAdapter(adapter);

        lvBills.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(HistoryActivity.this, DetailActivity.class);
            i.putExtra("id", ids.get(position));
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        ids.clear();
        labels.clear();
        Cursor c = dbHelper.getAllBills();
        while (c.moveToNext()) {
            ids.add(c.getInt(c.getColumnIndexOrThrow("id")));
            String month = c.getString(c.getColumnIndexOrThrow("month"));
            double finalCost = c.getDouble(c.getColumnIndexOrThrow("final_cost"));
            labels.add(String.format(Locale.US, "%s  -  RM %.2f", month, finalCost));
        }
        c.close();
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(labels.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}