package baftek.pl.jbossapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private int allContributorsCount;
    private String language;
    private String description;
    private String contributorsUrl;
    private String license;

    List<ContributorData> topContributorDataList;
    ContributorRecyclerViewAdapter topAdapter;
    List<ContributorData> restContributorDataList;
    ContributorRecyclerViewAdapter restAdapter;

    @BindView(R.id.textView_description) TextView textView_description;
    @BindView(R.id.textView_starCount) TextView textView_starCount;
    @BindView(R.id.textView_language) TextView textView_language;
    @BindView(R.id.textView_forkCount) TextView textView_forkCount;
    @BindView(R.id.button_github) Button button_github;
    @BindView(R.id.progressBar_contributors) ProgressBar progressBar;
    @BindView(R.id.textView_error) TextView textView_error;
    @BindView(R.id.button_tryAgain) Button button_tryAgain;
    @BindView(R.id.recyclerView_topContributors) RecyclerView topRecyclerView;
    @BindView(R.id.recyclerView_restOfontributors) RecyclerView restRecyclerView;
    @BindView(R.id.textView_contributorCount) TextView textView_contributorLabel;
    @BindView(R.id.textView_license) TextView textView_license;

    @BindView(R.id.content_layout) LinearLayout contentLayout;
    @BindView(R.id.error_layout) LinearLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        ButterKnife.bind(this);
        getDataFromBundle();
        makeRequest();

        topAdapter = new ContributorRecyclerViewAdapter(this, topContributorDataList);
        topAdapter.setShowPosition(true);
        restAdapter = new ContributorRecyclerViewAdapter(this, restContributorDataList);

        topRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topRecyclerView.setHasFixedSize(true);
        topRecyclerView.setAdapter(topAdapter);

        restRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restRecyclerView.setHasFixedSize(true);
        restRecyclerView.setAdapter(restAdapter);

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
     * Creates a request to get additional data needed in this acivity, such as allContributorsCount (to this
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
        topContributorDataList = new ArrayList<>();
        restContributorDataList = new ArrayList<>();

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
     *
     * @param response Response got when {@link RepositoryActivity#makeRequest()} completes with a success
     */
    private void processResponse(String response)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(response);
            allContributorsCount = jsonArray.length();

            for (int i = 0; i < allContributorsCount; i++)
            {
                JSONObject contributorJSON = jsonArray.getJSONObject(i);
                String login = contributorJSON.getString("login");
                String avatarUrl = contributorJSON.getString("avatar_url");
                String githubProfileUrl = contributorJSON.getString("html_url");
                int contributions = contributorJSON.getInt("contributions");

                ContributorData contributorData = new ContributorData(login, avatarUrl, githubProfileUrl, contributions);
                restContributorDataList.add(contributorData);
            }

            /*  Get first 3 contributors and place them in topContributorDataList
                We're removing only 0 indexes, because after deletion the old 1 is now 0 :)
             */
            for (int i = 0; i < 3; i++)
            {
                // Get first element
                ContributorData data = restContributorDataList.get(0);

                // Add that element to the List of Top Contributors
                topContributorDataList.add(data);

                // Remove the Top Contributor from List of Rest Contributors
                restContributorDataList.remove(0);
            }

            // Print top contributors to logcat
            for (ContributorData data : topContributorDataList)
            {
                Log.d(TAG + " TOP", data.toString());
            }

            // Print rest of contributors to logcat
            for (ContributorData data : restContributorDataList)
            {
                Log.d(TAG + " REST", data.toString());
            }

            setLayoutReady();
            runLayoutAnimation(topRecyclerView);
            runLayoutAnimation(restRecyclerView);
        } catch (JSONException e)
        {
            e.printStackTrace();
            setLayoutError();
        }
    }
    
    /**
     * Runs layout animation for items in the RecyclerView (passed as argument)
     * @param recyclerView RecyclerView for which the animation should be run
     */
    private void runLayoutAnimation(final RecyclerView recyclerView)
    {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Invoke this when data is being loaded. Displays progress bar.
     */
    private void setLayoutLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        contentLayout.setVisibility(View.GONE);
    }

    /**
     * Invoke this when everything is OK. Displays complete, ready-to-use layout.
     */
    private void setLayoutReady()
    {
        textView_contributorLabel.setText(String.valueOf(allContributorsCount));
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        contentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Invoke this when everything something went wrong. Displays error message and Try Again button.
     */
    private void setLayoutError()
    {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);

        contentLayout.setVisibility(View.GONE);
    }
}
