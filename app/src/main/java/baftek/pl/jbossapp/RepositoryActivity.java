package baftek.pl.jbossapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoryActivity extends AppCompatActivity
{
    private String name;
    private String url;
    private int stars;
    private String description;

    @BindView(R.id.textView_description) TextView textView_description;
    @BindView(R.id.button_github) Button button_github;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        ButterKnife.bind(this);
        processBundleData();

        textView_description.setText(description);
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

    /**
     * Assigns variables from the Bundle
     */
    private void processBundleData()
    {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        url = bundle.getString("url");
        stars = bundle.getInt("stars");
        description = bundle.getString("description");
    }
}
