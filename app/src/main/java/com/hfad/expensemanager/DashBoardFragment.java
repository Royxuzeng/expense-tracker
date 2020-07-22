package com.hfad.expensemanager;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;


public class DashBoardFragment extends Fragment {

    // Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    // Floating button textview

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    // boolean

    private boolean isOpen;

    // Animation

    private Animation FadeOpen, FadeClose;

    //Dashboard income and expense result

    private  TextView totalIncomeResult;
    private  TextView totalExpenseResult;

    // Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //list of data

    private List<Data> incomeData;
    private List<Data> expenseData;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    //Line charts

    private LineChartView mIncomeLineChart;
    private LineChartView mExpenseLineChart;
  //  private List<PointValue> mIncomePointValue;
    // private List<PointValue> mExpensePointValue;

    //Pie charts

    private PieChartView mIncomePieChart;
    private PieChartView mExpensePieChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().
                getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().
                getReference().child("ExpenseDatabase").child(uid);

        // Connect floating button to layout

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        // Connect floating text.

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        //Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        // Connect animation

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        //line charts

        mIncomeLineChart = myview.findViewById(R.id.income_line_chart);
        mExpenseLineChart = myview.findViewById(R.id.expense_line_chart);

        //pie charts

        mIncomePieChart = myview.findViewById(R.id.income_pie_chart);
        mExpensePieChart = myview.findViewById(R.id.expense_pie_chart);

        //list of data

        incomeData = new ArrayList<>();
        expenseData = new ArrayList<>();

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();

                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });

        //Calculate total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum = 0;
                incomeData = new ArrayList<>();

                for(DataSnapshot mysnap: dataSnapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    incomeData.add(data);

                    totalsum += data.getAmount();
                }

                String stResult = String.valueOf(totalsum);

                totalIncomeResult.setText(stResult + ".00");

                updateLineChart(mIncomeLineChart, incomeData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum = 0;
                expenseData = new ArrayList<>();

                for(DataSnapshot mysnap: dataSnapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    expenseData.add(data);

                    totalsum += data.getAmount();
                }

                String stResult = String.valueOf(totalsum);

                totalExpenseResult.setText(stResult + ".00");

                updateLineChart(mExpenseLineChart, expenseData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    // Floating button animation

    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {

        // Fab Button income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insert_incomedata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText edtAmount = myview.findViewById(R.id.amount_edt);
        final Spinner edtType = myview.findViewById(R.id.type_income_sp);
        final EditText edtNote = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = String.valueOf(edtType.getSelectedItem());
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Required Field");
                    return;
                }

                int ouramountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ouramountint, type, note, id, mDate);
                
                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insert_expensedata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText amount = myview.findViewById(R.id.amount_edt);
        final Spinner type = myview.findViewById(R.id.type_expense_sp);
        final EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmAmount = amount.getText().toString().trim();
                String tmtype = String.valueOf(type.getSelectedItem());
                String tmnote = note.getText().toString().toLowerCase();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required Field..");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmnote)) {
                    note.setError("Required Field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount, tmtype, tmnote, id, mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // update line charts

    public void updateLineChart(LineChartView lineChart, List<Data> list) {
        System.out.println("yeah! Successfully updated!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        List<PointValue> pointValues = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        HashMap<String, Integer> map = new HashMap<>();
        List<String> dates = new ArrayList<>();
        Data data;
        String date;
        int amount;

        for (int i = 0; i < list.size(); i++) {
            data = list.get(i);
            date = data.getDate();

            if (map.get(date) == null) {
                amount = data.getAmount();
                dates.add(date);
            } else {
                amount = map.get(date) + data.getAmount();
            }

            map.put(date, amount);

        }

        for(int i = 0; i < dates.size(); i++) {
            axisValues.add(new AxisValue(i).setLabel(dates.get(i)));

            pointValues.add(new PointValue(i, map.get(dates.get(i))));
        }

        Line line = new Line(pointValues).setColor(Color.parseColor("#3700B3"));
        List<Line> lines = new ArrayList<Line>();

        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);

        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        //axis X

        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.parseColor("#D6D6D9"));

        axisX.setTextSize(8);// font size
        axisX.setMaxLabelChars(7);//maximum axis x
        axisX.setValues(axisValues);
        lineChartData.setAxisXBottom(axisX);
        axisX.setHasLines(true);

        //axis Y

        Axis axisY = new Axis();
        axisY.setName("$"); //label
        axisY.setTextSize(8); //font size
        lineChartData.setAxisYLeft(axisY);

        //properties for line charts

        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 3);
        lineChart.setLineChartData(lineChartData);
        lineChart.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.left = 0;
        v.right= 6;

        lineChart.setCurrentViewport(v);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashBoardFragment.IncomeViewHolder.class,
                        mIncomeDatabase

                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeDate(model.getDate());
                viewHolder.setIncomeAmount(model.getAmount());

            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_expense,
                        DashBoardFragment.ExpenseViewHolder.class,
                        mExpenseDatabase

                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseDate(model.getDate());
                viewHolder.setExpenseAmount(model.getAmount());

            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
    }

    //for income data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {

            TextView mtype = mIncomeView.findViewById(R.id.type_income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmount(int amount) {

            TextView mamount = mIncomeView.findViewById(R.id.amount_income_ds);
            String strAmount = String.valueOf(amount);
            mamount.setText(strAmount);

        }

        public void setIncomeDate(String date) {

            TextView mdate = mIncomeView.findViewById(R.id.date_income_ds);
            mdate.setText(date);

        }
    }

    //for expense data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type) {

            TextView mtype = mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);

        }

        public void setExpenseAmount(int amount) {

            TextView mamount = mExpenseView.findViewById(R.id.amount_expense_ds);
            String strAmount = String.valueOf(amount);
            mamount.setText(strAmount);

        }

        public void setExpenseDate(String date) {

            TextView mdate = mExpenseView.findViewById(R.id.date_expense_ds);
            mdate.setText(date);

        }
    }
}

