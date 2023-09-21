package com.example.unquote;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private Resources resources;

    // Constructor
    public CategoryAdapter(List<Category> categoryList, Resources resources) {
        this.categoryList = categoryList;
        this.resources = resources;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_category, parent, false);

        return new CategoryViewHolder(itemView);
    }
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        Button includeButton;
        ImageView categoryImageView;
        CardView categoryCardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            includeButton = itemView.findViewById(R.id.includeCategoryButton);
            categoryImageView = itemView.findViewById(R.id.categoryImageView);
            categoryCardView = itemView.findViewById(R.id.categoryCardView);

        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.categoryNameTextView.setText(category.categoryName);
        holder.categoryNameTextView.setTextColor(resources.getColor(category.textColourId));
        holder.includeButton.setBackgroundColor(resources.getColor(category.cardColourId));
        holder.categoryImageView.setImageResource(category.imageResourceId);
        holder.categoryCardView.setAlpha(category.included? 1.0f : 0.2f);
        holder.includeButton.setOnClickListener(view -> {
            // Handle button click to include/exclude category
            category.included = (!category.included);
            holder.categoryCardView.setAlpha(category.included? 1.0f : 0.2f);
        });

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
