package baftek.pl.jbossapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import baftek.pl.jbossapp.internal.ContributorData;
import baftek.pl.jbossapp.internal.ContributorRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoryActivity extends AppCompatActivity
{
    private static final String TAG = "RepositoryActivity";

    private String name;
    private String url;
    private int stars;
    private String description;
    private String contributorsUrl;

    List<ContributorData> contributorDataList;
    ContributorRecyclerViewAdapter adapter;

    @BindView(R.id.textView_description)
    TextView textView_description;
    @BindView(R.id.textView_starCount)
    TextView textView_starCount;
    @BindView(R.id.button_github)
    Button button_github;
    @BindView(R.id.recyclerView_contributors)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar_contributors)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        ButterKnife.bind(this);
        processBundleData();

        adapter = new ContributorRecyclerViewAdapter(this, contributorDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        textView_description.setText(description);
        textView_starCount.setText(Integer.toString(stars));
        button_github.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });

        getSupportActionBar().setTitle(name);
    }

    private void processBundleData()
    {
        contributorDataList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        url = bundle.getString("url");
        stars = bundle.getInt("stars");
        description = bundle.getString("description");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        contributorsUrl = bundle.getString("contributors_url");
        StringRequest request = new StringRequest(Request.Method.GET, contributorsUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Toast.makeText(RepositoryActivity.this, "Download successful", Toast.LENGTH_SHORT).show();
                processResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, error.getMessage());
                Toast.makeText(RepositoryActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void processResponse(String response)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject contributorJSON = jsonArray.getJSONObject(i);
                String login = contributorJSON.getString("login");
                String avatarUrl = contributorJSON.getString("avatar_url");
                String githubProfileUrl = contributorJSON.getString("html_url");
                int contributions = contributorJSON.getInt("contributions");

                ContributorData contributorData = new ContributorData(login, avatarUrl, githubProfileUrl, contributions);
                contributorDataList.add(contributorData);
            }
        } catch (JSONException e)
        {
            Log.e(TAG, e.getMessage());
        }

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
