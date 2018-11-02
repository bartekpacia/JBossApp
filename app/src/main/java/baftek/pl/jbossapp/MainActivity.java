package baftek.pl.jbossapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import baftek.pl.jbossapp.internal.RepoData;
import baftek.pl.jbossapp.internal.ReposRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    String requestUrl = "https://api.github.com/orgs/JBossOutreach/repos";

    private ArrayList<RepoData> repoDataList;
    private ReposRecyclerViewAdapter adapter;

    @BindView(R.id.progressBar_repos) ProgressBar progressBar;
    @BindView(R.id.textView_error) TextView textView_error;
    @BindView(R.id.button_tryAgain) Button button_tryAgain;
    @BindView(R.id.recyclerView_repos) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        repoDataList = new ArrayList<>();
        adapter = new ReposRecyclerViewAdapter(this, repoDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        setLayoutLoading();

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
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                setLayoutError();
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
                int forks = repoJSON.getInt("forks");
                String language = repoJSON.getString("language");
                String description = repoJSON.getString("description");
                String contributorsUrl = repoJSON.getString("contributors_url");

                RepoData data = new RepoData(repoName, url, stars, forks, language, description, contributorsUrl);
                repoDataList.add(data);
            }

            setLayoutReady();
            runLayoutAnimation(recyclerView);
        } catch (JSONException e)
        {
            e.printStackTrace();
            setLayoutError();
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView)
    {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        adapter.notifyDataSetChanged();
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }


    private void setLayoutLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        textView_error.setVisibility(View.GONE);
        button_tryAgain.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void setLayoutReady()
    {
        progressBar.setVisibility(View.GONE);
        textView_error.setVisibility(View.GONE);
        button_tryAgain.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setLayoutError()
    {
        progressBar.setVisibility(View.GONE);
        textView_error.setVisibility(View.VISIBLE);
        button_tryAgain.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
