package com.example.personel_takip.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personel_takip.databinding.ItemContainerPartsBinding;
import com.example.personel_takip.listeners.PartsListener;
import com.example.personel_takip.model.Parts;

import java.util.List;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.PartsViewHolder>{

    private List<Parts> parts;
    private final PartsListener partsListener;

    public PartsAdapter(List<Parts> parts, PartsListener partsListener){
        this.parts = parts;
        this.partsListener = partsListener;

    }

    @NonNull
    @Override
    public PartsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerPartsBinding itemContainerPartsBinding = ItemContainerPartsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PartsViewHolder(itemContainerPartsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PartsViewHolder holder, int position) {
        holder.setPartsData(parts.get(position));
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    class PartsViewHolder extends RecyclerView.ViewHolder {

        ItemContainerPartsBinding binding;
        public PartsViewHolder(@NonNull ItemContainerPartsBinding itemContainerPartsBinding ) {
            super(itemContainerPartsBinding.getRoot());
            binding = itemContainerPartsBinding;
        }
        void setPartsData(Parts parts){
            binding.textName.setText(parts.partsName);
            binding.textBarcode.setText(parts.barcodeNo);
//            binding.textPartsPrice.setText(parts.partsPrice.toString());
            //binding.textPartsAmount.setText(parts.partsAmount);

            binding.getRoot().setOnClickListener(v-> partsListener.onPartsClicked(parts));
        }
    }
}
