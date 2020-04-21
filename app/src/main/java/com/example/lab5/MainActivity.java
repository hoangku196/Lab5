package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtRss;
    private Button btnLoadRss;
    private ListView lvLoadRss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtRss = findViewById(R.id.edtRss);
        btnLoadRss = findViewById(R.id.btnLoadRss);
        lvLoadRss = findViewById(R.id.lvLoadRss);

        btnLoadRss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = edtRss.getText().toString();
                MyAsynctask myAsynctask = new MyAsynctask();
                myAsynctask.execute(link);
            }
        });
    }

    class MyAsynctask extends AsyncTask<String, Long, List<Item>> {

        @Override
        protected List<Item> doInBackground(String... strings) {
            List<Item> items = new ArrayList<>();

            try {
                URL url = new URL(strings[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();


                // khoi tao doi tuong xmlpullparser
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);

                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

                // truyen du lieu vao xmlpullparser tien hanh boc tach xml
                xmlPullParser.setInput(inputStream, "utf-8");

                int eventType = xmlPullParser.getEventType();
                Item item = null;
                String text = "";
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equals("item")) {
                                item = new Item();
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = xmlPullParser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (item != null && name.equalsIgnoreCase("title")) {
                                item.title = text;
                            } else if (item != null && name.equalsIgnoreCase("description")) {
                                item.description = text;
                            } else if (item != null && name.equalsIgnoreCase("pubDate")) {
                                item.pubDate = text;
                            } else if (item != null && name.equalsIgnoreCase("link")) {
                                item.link = text;
                            } else if (name.equalsIgnoreCase("item")) {
                                items.add(item);
                            }
                            break;

                    }
                    // di chuyen toi tag ke tiep
                    eventType = xmlPullParser.next(); //move to next element
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }

            Log.e("size", items.size() + "");

            return items;
        }


        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            AdapterList adapterList = new AdapterList(items);
            lvLoadRss.setAdapter(adapterList);

            Toast.makeText(MainActivity.this, "Size :" + items.size(), Toast.LENGTH_SHORT).show();
        }
    }
}
