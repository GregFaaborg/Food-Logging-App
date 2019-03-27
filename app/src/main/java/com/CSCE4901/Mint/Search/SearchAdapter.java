package com.CSCE4901.Mint.Search;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.CSCE4901.Mint.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<SearchItem> mItems;
    Context mContext;
    public SearchAdapter(ArrayList itemList) {
        mItems = itemList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        mContext = parent.getContext();
        SearchViewHolder holder = new SearchViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {

        //get item and concatenize with their appropriate option
        holder.mTitle.setText(mItems.get(position).title);
        holder.mDescription.setText(mItems.get(position).description);
        holder.mDate.setText(mItems.get(position).date);

        String FlagHolder = mItems.get(position).flag; //get flag string from database stored in FlagHolder

        //Toast.makeText(mContext, FlagHolder, Toast.LENGTH_SHORT).show();

        if(FlagHolder.equals("1")) {
            //MAKE FLAG STAR YELLOW
            holder.mFlag.setColorFilter(Color.parseColor("#CCCC00"));
        }
        else {
            //MAKE FLAG STAR TO NORMAL DEFAULT COLOR
            holder.mFlag.setColorFilter(Color.parseColor("#696969"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d", position + 1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
