package com.example.invoce.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoce.InvoiceItem;
import com.example.invoce.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;


public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {


    private final List<InvoiceItem> items;

    private final Consumer<Integer> onItemClick;

    private final Consumer<Double> onTotalChanged;

    private int selectedPosition = -1;

    private final DecimalFormat df =
            new DecimalFormat("#.##");



    public InvoiceAdapter(
            List<InvoiceItem> items,
            Consumer<Integer> onItemClick,
            Consumer<Double> onTotalChanged
    ) {

        this.items = items;
        this.onItemClick = onItemClick;
        this.onTotalChanged = onTotalChanged;
    }



    public class InvoiceViewHolder extends RecyclerView.ViewHolder {


        TextView tvNumber;
        TextView tvName;
        TextView tvPrice;
        EditText etQuantity;
        TextView tvSum;



        public InvoiceViewHolder(@NonNull View view) {

            super(view);


            tvNumber =
                    view.findViewById(R.id.tvInvoiceNumber);

            tvName =
                    view.findViewById(R.id.tvInvoiceName);

            tvPrice =
                    view.findViewById(R.id.tvInvoicePrice);

            etQuantity =
                    view.findViewById(R.id.tvInvoiceQuantity);

            tvSum =
                    view.findViewById(R.id.tvInvoiceSum);



            tvName.setOnClickListener(v -> {

                int pos = getAdapterPosition();

                if (pos != RecyclerView.NO_POSITION) {

                    selectedPosition = pos;

                    if (onItemClick != null) {
                        onItemClick.accept(pos);
                    }

                    notifyDataSetChanged();
                }
            });
        }
    }




    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {


        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_invoice,
                                parent,
                                false
                        );


        return new InvoiceViewHolder(view);
    }





    @Override
    public int getItemCount() {

        return items.size();
    }





    @Override
    public void onBindViewHolder(
            @NonNull InvoiceViewHolder holder,
            int position
    ) {


        InvoiceItem item =
                items.get(position);


        holder.tvNumber.setText(
                String.valueOf(position + 1)
        );


        holder.tvName.setText(
                item.getProduct().displayNameLength()
        );


        holder.tvPrice.setText(
                item.getProduct().displayPrice()
        );


        holder.tvSum.setText(
                df.format(
                        item.getQuantity()
                                * item.getProduct().getPrice()
                )
        );



        Object tag =
                holder.etQuantity.getTag();


        if (tag instanceof TextWatcher) {

            holder.etQuantity.removeTextChangedListener(
                    (TextWatcher) tag
            );
        }



        holder.etQuantity.setText(
                String.valueOf(item.getQuantity())
        );



        TextWatcher watcher =
                new TextWatcher() {


                    @Override
                    public void afterTextChanged(Editable s) {


                        int pos =
                                holder.getAdapterPosition();


                        if (pos == RecyclerView.NO_POSITION) {
                            return;
                        }


                        int newQty = 0;


                        try {

                            newQty =
                                    Integer.parseInt(
                                            s.toString()
                                    );

                        } catch (Exception ignored) {

                        }



                        if (newQty != items.get(pos).getQuantity()) {


                            items.get(pos)
                                    .setQuantity(newQty);



                            holder.tvSum.setText(
                                    df.format(
                                            newQty *
                                                    items.get(pos)
                                                            .getProduct()
                                                            .getPrice()
                                    )
                            );


                            recalcTotal();
                        }
                    }



                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {}



                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {}
                };



        holder.etQuantity.addTextChangedListener(watcher);

        holder.etQuantity.setTag(watcher);



        holder.etQuantity.setImeOptions(6);

        holder.etQuantity.setInputType(2);



        holder.etQuantity.setOnEditorActionListener(
                (v, actionId, event) -> {


                    if (actionId == 6) {


                        holder.etQuantity.clearFocus();


                        InputMethodManager imm =
                                (InputMethodManager)
                                        holder.itemView
                                                .getContext()
                                                .getSystemService(
                                                        Context.INPUT_METHOD_SERVICE
                                                );


                        imm.hideSoftInputFromWindow(
                                holder.etQuantity.getWindowToken(),
                                0
                        );


                        return true;
                    }


                    return false;
                }
        );



        if (position == selectedPosition) {

            holder.itemView.setBackgroundColor(
                    0xFFE0E0E0
            );

        } else {

            holder.itemView.setBackgroundColor(0);
        }
    }





    public void addItem(InvoiceItem item) {

        items.add(item);

        notifyItemInserted(
                items.size() - 1
        );

        recalcTotal();
    }





    public void removeItem(int position) {

        if (position >= 0 &&
                position < items.size()) {

            items.remove(position);

            selectedPosition = -1;

            notifyItemRemoved(position);

            notifyItemRangeChanged(
                    position,
                    items.size() - position
            );

            notifyDataSetChanged();

            recalcTotal();
        }
    }





    public void clearAll() {

        int size = items.size();

        items.clear();

        selectedPosition = -1;

        notifyItemRangeRemoved(0, size);

        recalcTotal();
    }





    public int getSelectedPosition() {

        return selectedPosition;
    }





    public List<InvoiceItem> getCurrentItems() {

        return items;
    }





    public void recalcTotal() {


        double total = 0;


        for (InvoiceItem item : items) {


            total +=
                    item.getQuantity()
                            *
                            item.getProduct()
                                    .getPrice();
        }



        if (onTotalChanged != null) {

            onTotalChanged.accept(total);
        }
    }
}