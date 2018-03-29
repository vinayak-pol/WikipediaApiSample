package com.vinayak.sample.wikipediaapi_httprequest;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView tvResponse;
    SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        tvResponse = (TextView) findViewById(R.id.textView);


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String sourceText=decodeSourceText(mSearchView.getQuery().toString());
                //WikiAPI
                new HttpAsyncTask().execute("https://en.wikipedia.org/w/api.php?" +
                        "format=json" +
                        "&action=query" +
                        "&prop=extracts" +

                        "&explaintext=" +
                        "&titles="+sourceText);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("well", " this worked");
                return false;
            }
        });

    }
    String decodeSourceText(String sourceText){
        return sourceText.toLowerCase().replace(" ","%20");
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";

        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            String UN="",PW="",user="",wikiText="";
            try {
                JSONObject json = new JSONObject(result); // convert String to JSONObject
                //JSONArray articles = json.getJSONArray("array"); // get articles array
                JSONObject query = json.getJSONObject("query");
                JSONObject pages = query.getJSONObject("pages");
                //user=pages.toString();

                String[] str_array = pages.toString().substring(0,20).split(":");
                String string1 = str_array[0];
                String pageid=string1.substring(2,string1.length()-1);
                user=string1+"\n"+pageid;


                JSONObject page = pages.getJSONObject(pageid);

                wikiText=(String) page.get("extract");
                String[] str_array2 = wikiText.split("==");
                wikiText="";
                for(String temp:str_array2)
                {
                    wikiText=wikiText+"\n-----------------------------------------------\n"+temp.trim();
                }

                if(!wikiText.isEmpty())
                {
                    tvResponse.setText(wikiText.trim());

                }
                else
                    tvResponse.setText("No Result Found");
            }
            catch(JSONException e)
            {
                tvResponse.setText(e.toString());
                Toast.makeText(getBaseContext(), "JSONException!", Toast.LENGTH_SHORT).show();
            }

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
