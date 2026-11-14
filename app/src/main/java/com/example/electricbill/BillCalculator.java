package com.example.electricbill;

import android.widget.EditText;

public class BillCalculator {

    private static final int[] BLOCK = {200, 100, 300, 1000000};
    private static final double[] RATE = {0.218, 0.334, 0.516, 0.546};

    public static double calculateTotal(int units) {
        double total = 0;
        int remaining = units;
        for (int i = 0; i < BLOCK.length && remaining > 0; i++) {
            int used = Math.min(remaining, BLOCK[i]);
            total += used * RATE[i];
            remaining -= used;
        }
        return total;
    }

    public static double applyRebate(double total, int rebatePercent) {
        return total - (total * rebatePercent / 100.0);
    }


    public static int readUnits(EditText et) {
        String s = et.getText().toString().trim();
        if (s.isEmpty()) {
            et.setError("Please enter the units used (kWh)");
            et.requestFocus();
            return -1;
        }
        int units;
        try {
            units = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            et.setError("Please enter a whole number");
            et.requestFocus();
            return -1;
        }
        if (units < 1 || units > 1000) {
            et.setError("Units must be between 1 and 1000 kWh");
            et.requestFocus();
            return -1;
        }
        return units;
    }
}