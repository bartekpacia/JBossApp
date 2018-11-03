package baftek.pl.jbossapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private int forks;
    private int contributors;
    private String language;
    private String description;
    private String contributorsUrl;
    private String license;

    List<ContributorData> contributorDataList;
    ContributorRecyclerViewAdapter adapter;

    @BindView(R.id.textView_description) TextView textView_description;
    @BindView(R.id.textView_starCount) TextView textView_starCount;
    @BindView(R.id.textView_language) TextView textView_language;
    @BindView(R.id.textView_forkCount) TextView textView_forkCount;
    @BindView(R.id.button_github) Button button_github;
    @BindView(R.id.progressBar_contributors) ProgressBar progressBar;
    @BindView(R.id.textView_error) TextView textView_error;
    @BindView(R.id.button_tryAgain) Button button_tryAgain;
    @BindView(R.id.recyclerView_contributors) RecyclerView recyclerView;
    @BindView(R.id.textView_contributorLabel) TextView textView_contributorLabel;
    @BindView(R.id.textView_license) TextView textView_license;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        ButterKnife.bind(this);
        getDataFromBundle();
        makeRequest();

        adapter = new ContributorRecyclerViewAdapter(this, contributorDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        textView_description.setText(description);
        textView_starCount.setText(Integer.toString(stars));
        textView_forkCount.setText(Integer.toString(forks));
        textView_language.setText(language);
        textView_license.setText(license);
        button_github.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });

        button_tryAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                makeRequest();
            }
        });

        getSupportActionBar().setTitle(name);
    }

    /**
     * Creates a request to get additional data needed in this acivity, such as contributors (to this
     * specific repository)
     */
    private void makeRequest()
    {
        setLayoutLoading();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, contributorsUrl, new Response.Listener<String>()
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
                Toast.makeText(RepositoryActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                setLayoutError();
            }
        });
        requestQueue.add(request);
    }

    /**
     * Displays data already downloaded in MainActivity.
     */
    private void getDataFromBundle()
    {
        contributorDataList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        url = bundle.getString("url");
        stars = bundle.getInt("stars");
        forks = bundle.getInt("forks");
        language = bundle.getString("language");
        description = bundle.getString("description");
        contributorsUrl = bundle.getString("contributors_url");
        license = bundle.getString("license");
    }

    /**
     * Processes a response so everything is setup and ready
     * @param response Response got when {@link RepositoryActivity#makeRequest()} completes with a success
     */
    private void processResponse(String response)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(response);
            contributors = jsonArray.length();

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

    /**
     * Invoke this when data is being loaded. Displays progress bar.
     */
    private void setLayoutLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        textView_error.setVisibility(View.GONE);
        button_tryAgain.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        textView_contributorLabel.setVisibility(View.INVISIBLE);
    }

    /**
     * Invoke this when everything is OK. Displays complete, ready-to-use layout.
     */
    private void setLayoutReady()
    {
        textView_contributorLabel.setText(contributors + " " + getString(R.string.people_contributing));

        progressBar.setVisibility(View.GONE);
        textView_error.setVisibility(View.GONE);
        button_tryAgain.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        textView_contributorLabel.setVisibility(View.VISIBLE);
    }

    /**
     * Invoke this when everything something went wrong. Displays error message and Try Again button.
     */
    private void setLayoutError()
    {
        progressBar.setVisibility(View.GONE);
        textView_error.setVisibility(View.VISIBLE);
        button_tryAgain.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        textView_contributorLabel.setVisibility(View.INVISIBLE);
    }
}
