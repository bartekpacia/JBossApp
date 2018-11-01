package baftek.pl.jbossapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import baftek.pl.jbossapp.internal.MyRecyclerViewAdapter;
import baftek.pl.jbossapp.internal.RepoData;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;

    private ArrayList<RepoData> repoDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repoDataList = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(this, repoDataList);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView_repos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        String requestUrl = "https://api.github.com/orgs/JBossOutreach/repos";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                processResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    private void processResponse(String response)
    {
        Log.d(TAG, "Got response!");

        JSONArray jsonArray;
        try
        {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String repoName = jsonObject.getString("name");
                String url = jsonObject.getString("html_url");
                int stars = jsonObject.getInt("watchers");
                String description = jsonObject.getString("description");

                RepoData data = new RepoData(repoName, url, stars, description);
                repoDataList.add(data);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
