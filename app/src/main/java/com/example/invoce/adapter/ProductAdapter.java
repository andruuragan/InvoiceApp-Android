package com.example.invoce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoce.R;
import com.example.invoce.model.Product;

import java.util.List;
import java.util.function.Consumer;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> items;
    private final Consumer<Product> onClick;

    private int selectedPosition = -1;

    public ProductAdapter(List<Product> items, Consumer<Product> onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameLength;
        TextView tvPrice;

        public ProductViewHolder(@NonNull View view) {
            super(view);

            tvNameLength = view.findViewById(R.id.tvNameLength);
            tvPrice = view.findViewById(R.id.tvPrice);

            view.setOnClickListener(v -> {

                int pos = getAdapterPosition();

                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }

                selectedPosition = pos;

                onClick.accept(items.get(pos));

                notifyDataSetChanged();
            });
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder holder,
            int position
    ) {

        Product product = items.get(position);

        holder.tvNameLength.setText(product.displayNameLength());
        holder.tvPrice.setText(product.displayPrice());

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(0xFFE0E0E0);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Product> newList){

        items.clear();

        items.addAll(newList);

        notifyDataSetChanged();

    }

}