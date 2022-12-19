package com.block.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.block.memo.adapter.RecyclerViewAdapter;
import com.block.memo.data.DatabaseHandler;
import com.block.memo.model.Memo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnAdd;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Memo> memoArrayList;

    EditText editSearch;
    ImageView imgSearch;
    ImageView imgClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        editSearch = findViewById(R.id.editSearch);
        imgSearch = findViewById(R.id.imgSearch);
        imgClear = findViewById(R.id.imgClear);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddMemo.class);
                startActivity(i);
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.setText("");

                DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
                memoArrayList = dh.getAllMemo();
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = editSearch.getText().toString().trim();
                DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
                memoArrayList = dh.search(keyword);
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
                memoArrayList = dh.search(keyword);
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
        memoArrayList = dh.getAllMemo();
        // 어댑터를 연결해야지 화면에 표시가 됨.
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}



