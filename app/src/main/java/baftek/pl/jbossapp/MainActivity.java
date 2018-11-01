package baftek.pl.jbossapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import baftek.pl.jbossapp.internal.ReposRecyclerViewAdapter;
import baftek.pl.jbossapp.internal.RepoData;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    String requestUrl = "https://api.github.com/orgs/JBossOutreach/repos";

    @BindView(R.id.progressBar_repos)
    ProgressBar progressBar;
    @BindView(R.id.textView_error)
    TextView textView_error;
    @BindView(R.id.button_tryAgain)
    Button button_tryAgain;
    @BindView(R.id.recyclerView_repos)
    RecyclerView recyclerView;
    private ReposRecyclerViewAdapter adapter;

    private ArrayList<RepoData> repoDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        repoDataList = new ArrayList<>();
        adapter = new ReposRecyclerViewAdapter(this, repoDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        button_tryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                makeRequest();
            }
        });

        makeRequest();
    }

    private void makeRequest()
    {
        textView_error.setVisibility(View.GONE);
        button_tryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

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

                textView_error.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                button_tryAgain.setVisibility(View.VISIBLE);
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
                JSONObject repoJSON = jsonArray.getJSONObject(i);

                String repoName = repoJSON.getString("name");
                String url = repoJSON.getString("html_url");
                int stars = repoJSON.getInt("watchers");
                String description = repoJSON.getString("description");
                String contributorsUrl = repoJSON.getString("contributors_url");

                RepoData data = new RepoData(repoName, url, stars, description, contributorsUrl);
                repoDataList.add(data);
                adapter.notifyDataSetChanged();
            }

            textView_error.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            button_tryAgain.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } catch (JSONException e)
        {
            e.printStackTrace();
            textView_error.setVisibility(View.VISIBLE);
            button_tryAgain.setVisibility(View.VISIBLE);
        }
    }
}
