package com.example.edzolnieryk.listview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // global variables

    public int tickCounter = 0;

    public boolean fetchingData = false;

    public int currentScreen = 0;

    public int numDataItems = 0;
    public int selectedItem = -1;

    ArrayList<String> listOfItems = new ArrayList<String>();
    ArrayList<String> customItems = new ArrayList<String>();

    JSONObject testJObject = null;

    public String testDataString = "";

    // user info

    public String userName = "";
    public String userThumbnail = "";
    public String userGender = "";
    public String userStreet = "";
    public String userCity = "";
    public String userState = "";
    public String userPostCode = "";
    public String userEmail = "";
    public String userEmailName = "";
    public String userPassword = "";
    public String userSalt = "";
    public String userSha1 = "";
    public String userSha256 = "";
    public String userDob = "";
    public String userRegistered = "";
    public String userPhone = "";
    public String userCell = "";
    public String userCellID = "";
    public String userCellValue = "";
    public String userNat = "";

    // state engine

    public static int MAIN_SCREEN = 0;
    public static int SECONDARY_SCREEN = 1;

    // widget stuff

    public ListView mainList = null;
    public ListView secondaryList = null;

    public ArrayAdapter<String> mainAdapter;
    public ArrayAdapter<String> secondaryAdapter;

    public Button mainButton = null;

    // entry point

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // general

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init my stuff

        resetVariables();

        loadTestJSON();

        createTimer();

        initListView(MAIN_SCREEN);

        Log.e("DEBUG", "onCreate Has Finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // simple touch handler, not used...

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                Log.e("DEBUG", "Touch DOWN X=" + x + "Y=" + y);
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.e("DEBUG","Touch  MOVE X="+x+"Y="+y);
                break;

            case MotionEvent.ACTION_UP:
                Log.e("DEBUG", "Touch UP X=" + x + "Y=" + y);
                break;
        }
        return true;
    }

    // Activity Cycles

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("DEBUG", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("DEBUG", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("DEBUG", "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("DEBUG", "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("DEBUG", "onStart");
    }

    // restart

    public void resetVariables() {
        tickCounter = 0;

        currentScreen = MAIN_SCREEN;

        fetchingData = false;

        numDataItems = 0;

        selectedItem = -1;

        populatelistView(MAIN_SCREEN);
    }

    // test data from a JSON file

    public void loadTestJSON() {
        // load a string from raw

        try {
            InputStream is = this.getResources().openRawResource(R.raw.testdata);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            testDataString = new String(buffer);
            Log.e("DEBUG", "JSON Data=" + testDataString);
        } catch (IOException e) {
            testDataString = "BAD READ!";
            Log.e("DEBUG", "JSON Data=" + testDataString);
        }

        createJSON(testDataString);
    }

    //
    public void createJSON(String testString)
    {
        // Now create Object

        numDataItems = 0;

        try {
            testJObject = new JSONObject(testString);
            JSONArray peopleArray = testJObject.getJSONArray("results");

            Log.e("DEBUG", "Got Array for Result, Entries=" + peopleArray.length());

            for (int i = 0; i < peopleArray.length(); i++) {
                // extract name and picture url...

                Log.e("DEBUG", "Got Array for Result, Entries=" + peopleArray.length());

                JSONObject o = peopleArray.getJSONObject(i);

                JSONObject o2 = o.getJSONObject("name");
                String name = o2.getString("title") + " " + o2.getString("first") + " " + o2.getString("last");

                JSONObject o3 = o.getJSONObject("picture");
                String thumbnail = o3.getString("thumbnail");

                Log.e("DEBUG", "FOUND ENTRY FOR :" + name + " PICTURE URL=" + thumbnail);

                listOfItems.add(i, "[" + i + "] " + name + " [" + thumbnail + "]");
                ++numDataItems;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // fetch info for user screen

    public void getUserData(int entry) {
        try {
            testJObject = new JSONObject(testDataString);
            JSONArray peopleArray = testJObject.getJSONArray("results");

            JSONObject o = peopleArray.getJSONObject(entry);

            JSONObject o2 = o.getJSONObject("name");
            userName = o2.getString("title") + " " + o2.getString("first") + " " + o2.getString("last");

            JSONObject o3 = o.getJSONObject("picture");
            userThumbnail = o3.getString("thumbnail");

            userGender = o.getString("gender");

            JSONObject o4 = o.getJSONObject("location");
            userStreet = o4.getString("street");
            userCity = o4.getString("city");
            userState = o4.getString("state");
            userPostCode = o4.getString("postcode");

            userEmail = o.getString("email");
            userPhone = o.getString("phone");
            userCell = o.getString("cell");
            userNat = o.getString("nat");

            // and build the list

            customItems.clear();
            customItems.add(0, userName);
            customItems.add(1, userStreet);
            customItems.add(2, userCity);
            customItems.add(3, userState);
            customItems.add(4, userPostCode);
            customItems.add(5, userEmail);
            customItems.add(6, userPhone);
            customItems.add(7, userCell);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // listView listener

    public void initListView(int whichScreen) {
        if (whichScreen == MAIN_SCREEN) {
            ListView lV = (ListView) findViewById(R.id.mainListView);

            lV.setClickable(true);
            lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Log.e("DEBUG", "ITEMLIST CLICK ON=" + position);
                    if (numDataItems > 0) {
                        selectedItem = position;
                        getUserData(selectedItem);
                        showScreen(SECONDARY_SCREEN);
                    }
                }
            });
        } else {
            ListView lV = (ListView) findViewById(R.id.secondaryListView);
        }
    }

    // clear listView / Debug

    public void populatelistView(int whichScreen) {
        if (whichScreen == MAIN_SCREEN) {
            ListView lV = (ListView) findViewById(R.id.mainListView);

            mainAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfItems);
            lV.setAdapter(mainAdapter);
            mainAdapter.notifyDataSetChanged();
        } else {
            ListView lV = (ListView) findViewById(R.id.secondaryListView);

            secondaryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, customItems);
            lV.setAdapter(secondaryAdapter);
            secondaryAdapter.notifyDataSetChanged();
        }
    }

    // button handlers

    public void fetchDataButtonClicked(View v) {
        new FetchDataAsync().execute();
        Log.e("DEBUG", "Fetch Data button clicked");

        populatelistView(MAIN_SCREEN);
    }

    public void returnToMainButtonClicked(View v) {
        showScreen(0);
        Log.e("DEBUG", "Return to main screen button clicked");
    }

    // switch between layouts...

    public void showScreen(int whichScreen) {
        String titleString = "";
        TextView tv = null;

        if (whichScreen == SECONDARY_SCREEN) {
            setContentView(R.layout.activity_secondary);
            titleString = "Custom Data for Entry[" + selectedItem + "]";
            tv = (TextView) findViewById(R.id.TitleSecondary);
        } else {
            setContentView(R.layout.activity_main);
            titleString = "Select an Entry";
            tv = (TextView) findViewById(R.id.TitleMain);
        }

        initListView(whichScreen);
        populatelistView(whichScreen);

        if (tv != null)
            tv.setText(String.valueOf(titleString));

        currentScreen = whichScreen;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // generic tick handler - games would use the OpenGL tick() for this...

    public void createTimer() {
        int interval = 1000; // 1 Second...

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            // run on thread to allow UI changes

            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ++tickCounter;
                 /*
                    String titleString = "";
                    TextView tv = null;

                    // simple layout flip...

                    if (showingScreen == 0)
                    {
                        showingScreen = 1;
                        setContentView(R.layout.activity_secondary);
                        titleString = "Secondary[" + tickCounter + "]";
                        tv = (TextView) findViewById(R.id.TitleSecondary);
                    }
                    else
                    {
                        showingScreen = 0;
                        setContentView(R.layout.activity_main);
                        titleString = "Main[" + tickCounter + "]";
                        tv = (TextView) findViewById(R.id.TitleMain);

                    }

                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);

                    if (tv != null)
                        tv.setText(String.valueOf(titleString));
                 */
                    }
                });
            }
        }, 0, interval);
    }

    // asynch routine to fetch another record

    private class FetchDataAsync extends AsyncTask<String, Void, String> {

        String httpData = "";

        @Override
        protected void onPreExecute() {
            Log.e("DEBUG", "FetchDataAsync PreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("DEBUG", "FetchDataAsync doInBackGround");
            httpData = downloadHttpData("https://randomuser.me/api/");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("DEBUG", "FetchDataAsync PostExecute");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createJSON(httpData);
                }
            });

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    // simple http downloader, no error checking

    public String downloadHttpData(String address) {
        URL url;
        String testString = "";
        HttpURLConnection urlConnection = null;
        Log.e("DEBUG", "downloadHttpData=" + address);

        try {
            url = new URL(address);

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            //int count = 0;
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                //count++;
                //Log.e("DEBUG",count+"]="+current);
                testString += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        Log.e("DEBUG", "FinalData=" + testString);
        return testString;
    }
}
