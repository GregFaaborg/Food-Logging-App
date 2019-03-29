package com.CSCE4901.Mint.Search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.CSCE4901.Mint.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView mTitle;
    public TextView mCategory;
    public TextView mDate;
    public TextView mDescription;
    public ImageView mFlag;
    public SearchViewHolder(View itemView) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mCategory = itemView.findViewById(R.id.category);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mDescription = (TextView) itemView.findViewById(R.id.description);
        mFlag = (ImageView) itemView.findViewById(R.id.flag);

    }
}

