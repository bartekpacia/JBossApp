package baftek.pl.jbossapp;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
        TextView textView_repoName;
        //TextView textView_stars;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textView_repoName = itemView.findViewById(R.id.textView_name);
            //textView_stars = itemView.findViewById(R.id.textView_starCount);
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
        holder.textView_repoName.setText(repoDataList.get(position).getName());
        //holder.textView_stars.setText(repoDataList.get(position).getStars());
    }

    @Override
    public int getItemCount()
    {
        return repoDataList.size();
    }
}