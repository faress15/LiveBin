package org.bihe.livebin.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.databinding.ItemReceivedBinding;

import java.util.List;

public class ReceivedAdapter extends RecyclerView.Adapter<ReceivedAdapter.ViewHolder> {

    private final Context context;
    private List<Received> receipts;
    private final LayoutInflater layoutInflater;


    public ReceivedAdapter(Context context, List<Received> receipts) {
        this.context = context;
        this.receipts = receipts;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void updateReports(List<Received> newReceipts) {
        this.receipts = newReceipts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReceivedBinding binding = ItemReceivedBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    public void itemInsertedOnTop() {
        notifyItemInserted(0);
        notifyItemRangeChanged(0, receipts.size() - 1);
    }

    public void update() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemReceivedBinding binding;

        public ViewHolder(@NonNull ItemReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(int position) {
            Received received = receipts.get(position);
            binding.checkDetails.setVisibility(View.GONE);
            binding.posDetails.setVisibility(View.GONE);
            if (received.getBank().equals("") && received.getTrackingCode().equals("") && received.getCheckCode().equals("")) {
                binding.customerNameTv.setText(received.getCustomerName());
                binding.amountTv.setText(received.getAmountReceived());
                binding.typeOfReceivedTv.setText(received.getTypeReceived());
            } else if (!received.getBank().equals("") || !received.getCheckCode().equals("")) {
                binding.checkDetails.setVisibility(View.VISIBLE);
                binding.customerNameTv.setText(received.getCustomerName());
                binding.amountTv.setText(received.getAmountReceived());
                binding.typeOfReceivedTv.setText(received.getTypeReceived());
                binding.bankTv.setText(received.getBank());
                binding.checkCodeTv.setText(received.getCheckCode());
            } else if (!received.getTrackingCode().equals("")) {
                binding.posDetails.setVisibility(View.VISIBLE);
                binding.customerNameTv.setText(received.getCustomerName());
                binding.amountTv.setText(received.getAmountReceived());
                binding.typeOfReceivedTv.setText(received.getTypeReceived());
                binding.trackingCodeTv.setText(received.getTrackingCode());
            }


        }
    }
}
