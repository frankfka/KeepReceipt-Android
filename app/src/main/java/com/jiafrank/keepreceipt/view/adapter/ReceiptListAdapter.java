package com.jiafrank.keepreceipt.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListAdapter.ReceiptItemViewHolder> {

    // The data to display
    private RealmResults<Receipt> receipts;
    private ImageService imageService = new ImageService();

    /**
     * This adapter takes in RealmResults containing a set of receipts to display
     *
     * @param receipts self-updating RealmResults
     */
    public ReceiptListAdapter(RealmResults<Receipt> receipts) {
        this.receipts = receipts;

        // Listen for changes & update views if necessary
        receipts.addChangeListener(new RealmChangeListener<RealmResults<Receipt>>() {
            @Override
            public void onChange(RealmResults<Receipt> receipts) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(ReceiptItemViewHolder holder, int position) {

        // Get References to UI elements
        TextView receiptText = holder.rootViewContainer.findViewById(R.id.receiptRetailerText);
        ImageView receiptImage = holder.rootViewContainer.findViewById(R.id.receiptImage);

        // Get the receipt to show the data for
        Receipt receipt = receipts.get(position);

        // Update UI
        receiptText.setText(receipt.getVendor());
        // Get a scaled image so we're not passing around full-size images within memory
        receiptImage.setImageBitmap(imageService.getImageFile(receipt.getReceiptId(), holder.rootViewContainer.getContext(), 72, 72));
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    @Override
    public ReceiptListAdapter.ReceiptItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create each list item based on receipt_list_item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_list_item, parent, false);
        ReceiptItemViewHolder vh = new ReceiptItemViewHolder(v);
        return vh;
    }

    public static class ReceiptItemViewHolder extends RecyclerView.ViewHolder {
        public View rootViewContainer;

        public ReceiptItemViewHolder(View v) {
            super(v);
            rootViewContainer = v;
        }
    }

}
