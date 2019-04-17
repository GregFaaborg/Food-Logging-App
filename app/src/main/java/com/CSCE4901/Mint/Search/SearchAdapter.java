package com.CSCE4901.Mint.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.CSCE4901.Mint.update_entry;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<SearchItem> mItems;
    private Context mContext;
    FirebaseAuth firebaseAuth; //auth decleration
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    String TITLE;
    String CAT;
    String DES;
    String FLAG;

    int POS;
    //String DATE;
    boolean editCHECK=false;

    public SearchAdapter(ArrayList itemList) {
        //mAdapter = adapter;
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
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {


        //get item and concatenize with their appropriate option
        String titleHolder="Title: "+mItems.get(position).title;
        holder.mTitle.setText(titleHolder);//holder.mTitle.setText(mItems.get(position).title);

        //holder.mCat.setText(mItems.get(position).category);
        String catHolder="Category: "+mItems.get(position).category;
        holder.mCat.setText(catHolder);

        String desHolder="Description:\n"+mItems.get(position).description;
        holder.mDescription.setText(desHolder);

        String dateHolder="Date: "+mItems.get(position).date;
        holder.mDate.setText(dateHolder);

        //String FlagHolder = mItems.get(position).flag; //get flag string from database stored in FlagHolder

        //GET AND SET THE FLAG FROM THE DATABASE DIRECTLY
        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        //get email of signed in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String currentEmail = currentUser.getEmail();
        String docID= mItems.get(position).id;
        DocumentReference FLAG = db.collection(currentEmail).document(docID);
        FLAG.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        String FlagHolder=document.getString("flag");
                        if(FlagHolder.equals("1")) {
                            //MAKE FLAG STAR YELLOW

                            holder.mFlag.setColorFilter(Color.parseColor("#CCCC00"));
                        }
                        else {
                            //MAKE FLAG STAR TO NORMAL DEFAULT COLOR
                            holder.mFlag.setColorFilter(Color.parseColor("#696969"));
                        }
                    }
                }
            }
        });


        /*if(FlagHolder.equals("1")) {
            //MAKE FLAG STAR YELLOW

            holder.mFlag.setColorFilter(Color.parseColor("#CCCC00"));
        }
        else {
            //MAKE FLAG STAR TO NORMAL DEFAULT COLOR
            holder.mFlag.setColorFilter(Color.parseColor("#696969"));
        }*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Toast.makeText(mContext, String.format("%d", position + 1), Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, String.format("Color: "+mItems.get(position).flag), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mOPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gone options button
                holder.mOPT.setVisibility(View.INVISIBLE);
                //variables visible XML
                holder.mEdit.setVisibility(View.VISIBLE);
                holder.mDel.setVisibility(View.VISIBLE);
                holder.mX.setVisibility(View.VISIBLE);


            }

            });

        holder.mX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables gone XML
                holder.mEdit.setVisibility(View.INVISIBLE);
                holder.mDel.setVisibility(View.INVISIBLE);
                holder.mX.setVisibility(View.INVISIBLE);


                //visible options button
                holder.mOPT.setVisibility(View.VISIBLE);

            }
        });

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize Firebase Auth instance
                firebaseAuth = FirebaseAuth.getInstance();

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                editor.putInt("CHECK",0); //set as false aka not finished
                editor.apply();

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();

                final String ID= mItems.get(position).id;
                final String date = mItems.get(position).date;


                //get all the information in a HashMap
                HashMap<String, String> data = new HashMap<>();
                data.put("title", mItems.get(position).title);
                data.put("category", mItems.get(position).category);
                data.put("description", mItems.get(position).description);
                data.put("flag", mItems.get(position).flag);
                data.put("date", mItems.get(position).date);
                data.put("ID",mItems.get(position).id);
                data.put("email", UserEmail);


                //Intent newIntent = new Intent(SearchAdapter.this, );
                Intent goIntent = new Intent(mContext, update_entry.class);
                goIntent.putExtra("key", data); //send data hashMap
                mContext.startActivity(goIntent);



            }
        });

        holder.mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize Firebase Auth instance
                firebaseAuth = FirebaseAuth.getInstance();

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();


                SharedPreferences.Editor editor = mContext.getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                editor.putInt("CHECK",0); //set as false aka not finished
                editor.apply();

                String ID= mItems.get(position).id;

                //remove from algolia
                Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");
                assert UserEmail != null;
                Index index = client.getIndex(UserEmail);
                index.deleteObjectAsync(ID, null);

                //remove from recycler
                mItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mItems.size());

                //delete entry
                db.collection(UserEmail).document(ID).delete();

                Toast.makeText(mContext, String.format("Deleting Entry"), Toast.LENGTH_SHORT).show();
            }
        });

        /*int CHECK=go(position);
        while(CHECK!=1)
        {
            if(CHECK==1)
            {
                Toast.makeText(mContext, String.format("CHECK: "+CHECK), Toast.LENGTH_SHORT).show();
            }
            else {
                CHECK=go(position);
            }
        }*/


    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }


}
