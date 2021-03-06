package baftek.pl.jbossapp.internal;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import baftek.pl.jbossapp.R;
import baftek.pl.jbossapp.RepositoryActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReposRecyclerViewAdapter extends RecyclerView.Adapter<ReposRecyclerViewAdapter.ViewHolder>
{
    private List<RepoData> repoDataList;
    private Context context;

    public ReposRecyclerViewAdapter(Context context, List<RepoData> repoDataList)
    {
        this.context = context;
        this.repoDataList = repoDataList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textView_repoName;
        TextView textView_starCount;
        TextView textView_forksCount;
        TextView textView_language;
        TextView textView_description;

        public ViewHolder(View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            textView_repoName = itemView.findViewById(R.id.textView_name);
            textView_starCount = itemView.findViewById(R.id.textView_starCount);
            textView_forksCount = itemView.findViewById(R.id.textView_forkCount);
            textView_language = itemView.findViewById(R.id.textView_language);
            textView_description = itemView.findViewById(R.id.textView_repoDescription);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_repository, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        //for easier access
        final RepoData repoData = repoDataList.get(holder.getAdapterPosition());

        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO Refactor to use Bundle and Parcelable
                Intent i = new Intent(context, RepositoryActivity.class);
                i.putExtra("name", repoData.getName());
                i.putExtra("url", repoData.getUrl());
                i.putExtra("stars", repoData.getStars());
                i.putExtra("forks", repoData.getForks());
                i.putExtra("language", repoData.getLanguage());
                i.putExtra("description", repoData.getDescription());
                i.putExtra("contributors_url", repoData.getContributorsUrl());
                i.putExtra("license", repoData.getLicense());
                context.startActivity(i);
            }
        });
        holder.textView_repoName.setText(repoData.getName());

        //it needs to be converted to String, otherwise framework will try to use int as ResId and exception will be thrown
        holder.textView_starCount.setText(Integer.toString(repoData.getStars()));
        holder.textView_forksCount.setText(Integer.toString(repoData.getForks()));
        holder.textView_language.setText(repoData.getLanguage());
        holder.textView_description.setText(repoData.getDescription());
    }

    @Override
    public int getItemCount()
    {
        return repoDataList.size();
    }
}