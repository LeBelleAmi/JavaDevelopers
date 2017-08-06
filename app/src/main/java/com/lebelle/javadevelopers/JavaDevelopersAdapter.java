package com.lebelle.javadevelopers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omawumi Eyekpimi on 02-Jul-17.
 */

public class JavaDevelopersAdapter extends RecyclerView.Adapter<JavaDevelopersAdapter.JavaDevelopersViewHolder> implements Filterable {

    private Context context;
    private List<JavaDevelopers> javaDevelopers;
    private List<JavaDevelopers> mJavaDevelopersFiltered;
    private List<JavaDevelopers> searchArrayList;
    ValueFilter valueFilter;


    public static class JavaDevelopersViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lv;
        CircularImageView avatar;
        TextView username;
        TextView profile_url;
        TextView url;


        public JavaDevelopersViewHolder(View itemView) {
            super(itemView);
            lv = (LinearLayout) itemView.findViewById(R.id.text_container);
            avatar = (CircularImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            profile_url = (TextView) itemView.findViewById(R.id.profile_url);
            url = (TextView) itemView.findViewById(R.id.url);

        }
    }

    public JavaDevelopersAdapter(Context applicationContext, List<JavaDevelopers> javaDevelopersArrayList) {
        this.context = applicationContext;
        this.javaDevelopers = javaDevelopersArrayList;
        this.mJavaDevelopersFiltered = javaDevelopersArrayList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public JavaDevelopersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        JavaDevelopersViewHolder javaDevelopersViewHolder = new JavaDevelopersViewHolder(view);
        return javaDevelopersViewHolder;
    }

    @Override
    public void onBindViewHolder(final JavaDevelopersViewHolder javaDevelopersViewHolder, int i) {
        javaDevelopersViewHolder.username.setText(javaDevelopers.get(i).getUsername());
        javaDevelopersViewHolder.profile_url.setText(javaDevelopers.get(i).getProfileUrl());
        javaDevelopersViewHolder.url.setText(javaDevelopers.get(i).getUrl());
        Glide.with(context)
                .load(javaDevelopers.get(i).getAvatar())
                .asBitmap()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.user_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(javaDevelopersViewHolder.avatar);


        // Convert the String URL into a URI object (to pass into the Intent constructor)
        javaDevelopersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = javaDevelopersViewHolder.getAdapterPosition();
                MainActivity.lastPosition = pos;
                JavaDevelopers items = javaDevelopers.get(pos);
                Bundle args = new Bundle();
                args.putString("username", javaDevelopers.get(pos).getUsername());
                args.putString("profile_url", javaDevelopers.get(pos).getProfileUrl());
                args.putString("url", javaDevelopers.get(pos).getUrl());
                args.putString("avatar", javaDevelopers.get(pos).getAvatar());
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("items", args);
                context.startActivity(intent);
                //Toast.makeText(context, "you've clicked on " +  items.getUsername(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public int getItemCount() {

        if (javaDevelopers == null) {
            return 0;
        } else {
            return javaDevelopers.size();
        }
    }

    public void clear() {
        javaDevelopers = null;
        notifyDataSetChanged();

    }

    public void addAll(List<JavaDevelopers> newJavaDevelopers) {
        javaDevelopers = newJavaDevelopers;
        searchArrayList = javaDevelopers;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence){
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    mJavaDevelopersFiltered = searchArrayList;
                }else {
                    List<JavaDevelopers> filteredList = new ArrayList<>();
                    for (JavaDevelopers javaDevelopers1 : searchArrayList){
                        if (javaDevelopers1.getUsername().contains(charString)||javaDevelopers1.getAvatar().contains(charString)
                                ||javaDevelopers1.getUrl().contains(charString)||javaDevelopers1.getProfileUrl().contains(charString)){
                            filteredList.add(javaDevelopers1);
                        }
                    }
                    mJavaDevelopersFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mJavaDevelopersFiltered;
                filterResults.count = mJavaDevelopersFiltered.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults){
                mJavaDevelopersFiltered = (List<JavaDevelopers>)filterResults.values;
                javaDevelopers = mJavaDevelopersFiltered;
                notifyDataSetChanged();
            }
        }
    }





