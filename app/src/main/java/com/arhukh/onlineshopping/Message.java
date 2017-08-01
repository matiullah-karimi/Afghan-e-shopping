package com.arhukh.onlineshopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Message extends AppCompatActivity {
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // message arraylist
        arrayList = new ArrayList<>();
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mess = (EditText) findViewById(R.id.message);
                arrayList.add(mess.getText().toString());
                mess.setText("");
            }
        });
        ListView mlist = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter1 = new ArrayAdapter(Message.this, android.R.layout.simple_list_item_1, arrayList);
        mlist.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
    }
}
