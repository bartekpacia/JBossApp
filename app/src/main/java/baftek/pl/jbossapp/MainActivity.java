package baftek.pl.jbossapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setHasFixedSize(true);
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
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    private void processResponse(String response)
    {
        Log.d(TAG, "Got response!");
        Toast.makeText(MainActivity.this, "got", Toast.LENGTH_SHORT).show();

        JSONArray jsonArray;
        try
        {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String repoName = jsonObject.getString("name");
                int stars = jsonObject.getInt("watchers");

                RepoData data = new RepoData(repoName);
                repoDataList.add(data);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, "Finished processResponse()");
        Toast.makeText(MainActivity.this, "final success", Toast.LENGTH_SHORT).show();

    }
}
