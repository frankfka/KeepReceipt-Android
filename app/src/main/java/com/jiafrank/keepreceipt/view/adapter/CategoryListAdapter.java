package com.jiafrank.keepreceipt.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiafrank.keepreceipt.Constants;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.TextFormatService;
import com.jiafrank.keepreceipt.view.ViewReceiptActivity;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryItemViewHolder> {

    // The data to display
    private RealmResults<Category> categories;
    private boolean isEditing;
    private Category selectedCategory;

    /**
     * This adapter takes in RealmResults containing a set of categories to display
     */
    public CategoryListAdapter(RealmResults<Category> categories, boolean isEditing, Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        this.categories = categories;
        this.isEditing = isEditing;

        // Listen for changes & update views if necessary
        categories.addChangeListener(new RealmChangeListener<RealmResults<Category>>() {
            @Override
            public void onChange(RealmResults<Category> categories) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(final CategoryItemViewHolder holder, int position) {

        // Get References to UI elements
        TextView categoryName = holder.rootViewContainer.findViewById(R.id.categoryNameTextView);
        ImageButton deleteCategoryButton = holder.rootViewContainer.findViewById(R.id.deleteCategoryButton);
        ImageView selectedCheckmark = holder.rootViewContainer.findViewById(R.id.selectedCategoryCheckmark);

        // Get the receipt to show the data for
        final Category category = categories.get(position);

        // Update UI
        if(isEditing) {
            // We're editing categories

            // Load up UI
            selectedCheckmark.setVisibility(View.GONE);
            deleteCategoryButton.setVisibility(View.VISIBLE);
            categoryName.setText(category.getName());

            // Give delete button an on-click listener
            deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete from realm
                    try (Realm realm = Realm.getDefaultInstance()) {
                        category.deleteFromRealm();
                    }
                }
            });

        } else {
            // We're just viewing/selecting categories

            // Load up UI
            deleteCategoryButton.setVisibility(View.GONE);
            categoryName.setText(category.getName());
            if(null != selectedCategory && selectedCategory.equals(category)) {
                selectedCheckmark.setVisibility(View.VISIBLE);
            } else {
                selectedCheckmark.setVisibility(View.GONE);
            }

            // Give the entire cell a selection listener
            holder.rootViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCategory = category;
                    notifyDataSetChanged();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public CategoryListAdapter.CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create each list item based on receipt_list_item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_list_item, parent, false);
        CategoryItemViewHolder vh = new CategoryItemViewHolder(v);
        return vh;
    }

    public static class CategoryItemViewHolder extends RecyclerView.ViewHolder {
        public View rootViewContainer;
        public Context context;

        public CategoryItemViewHolder(View v) {
            super(v);
            rootViewContainer = v;
            context = v.getContext();
        }
    }

}
