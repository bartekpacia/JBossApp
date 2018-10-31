package baftek.pl.jbossapp;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public final class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
{
    private List<RepoData> repoDataList;
    private Context context;

    public MyRecyclerViewAdapter(Context context, List<RepoData> repoDataList)
    {
        this.context = context;
        this.repoDataList = repoDataList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textView_repoName;
        TextView textView_stars;

        public ViewHolder(View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            textView_repoName = itemView.findViewById(R.id.textView_name);
            textView_stars = itemView.findViewById(R.id.textView_starCount);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        final String url = repoDataList.get(position).getUrl();

        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(i);
            }
        });
        holder.textView_repoName.setText(repoDataList.get(position).getName());

        //it needs to be converted to String, otherwise framework will try to use int as ResId and exception will be thrown
        holder.textView_stars.setText(Integer.toString(repoDataList.get(position).getStars()));
    }

    @Override
    public int getItemCount()
    {
        return repoDataList.size();
    }
}