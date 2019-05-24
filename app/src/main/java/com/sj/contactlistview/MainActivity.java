package com.sj.contactlistview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SJ
 */
public class MainActivity extends AppCompatActivity implements IndexList.OnWordsChangeListener, ContactAdapter.OnClickListener {
    private List<String> stringList = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stringList.add("cc");
        stringList.add("dc");

        initView();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.contact_rv);
        TextView textView = findViewById(R.id.tv);
        IndexList indexList = findViewById(R.id.index_list);

        indexList.setTextView(textView);
        indexList.setOnWordsChangeListener(this);

        contactAdapter = new ContactAdapter();
        contactAdapter.setData(getContactInfo());
        contactAdapter.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void wordsChange(String words) {
        layoutManager.scrollToPositionWithOffset(contactAdapter.getFirstWordListPosition(words), 0);
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(MainActivity.this, "onClick,position:" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View view, int position) {
        Toast.makeText(MainActivity.this, "onLongClick,position:" + position, Toast.LENGTH_SHORT).show();
    }


    public List<String> getContactInfo() {
        List<String> data = new ArrayList<>();
        // 获得联系人名字 ，URI是ContactsContract.RawContacts.CONTENT_URI
        Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, null, null, ContactsContract.RawContacts.SORT_KEY_PRIMARY);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获得通讯录中联系人的名字
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
                data.add(name);
            }
            cursor.close();
        }
        return data;
    }
}
