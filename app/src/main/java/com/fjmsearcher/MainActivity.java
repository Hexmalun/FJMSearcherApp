package com.fjmsearcher;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements
        ExpandableListView.OnChildClickListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private String jsonResponse;
    private String [] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pesquisar (View view) {
        EditText pesquisa = (EditText) findViewById(R.id.editText);
        String saida = "http://13.88.255.190:8080/pesquisa?s=";
        String aux = pesquisa.getText().toString();
        saida = saida + aux + "&p=1";
        RequestQueue queue = Volley.newRequestQueue(this);
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expListView.setOnChildClickListener(this);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        final Context c = this;
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET,
                saida, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());


                jsonResponse = "";
                try {
                    urls = new String [response.length()];
                    // Parsing json array response
                    for (int i = 0; i < response.length() || i < 10; i++) {
                        JSONObject object = (JSONObject) response.get(i);

                        String score = object.getString("score");
                       // JSONArray categoraias = object.getJSONArray("categ");
                        String text = object.getString("text");
                        String title = object.getString("title");
                      //  JSONArray desck = object.getJSONArray("desck");
                        String url = object.getString("url");

                        listDataHeader.add(title);
                        List<String> top = new ArrayList<String>();
                        top.add(text);
                        top.add(score);
                        urls [i] = url;
                        top.add("Link");

                        listDataChild.put(listDataHeader.get(i), top); // Header, Child data
                        listAdapter = new ExpandableListAdapter(c, listDataHeader, listDataChild);

                        // setting list adapter
                        expListView.setAdapter(listAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("asd", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrReq);
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

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if(childPosition == 2) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[groupPosition]));
            startActivity(browserIntent);
        }
        return false;
    }
}
