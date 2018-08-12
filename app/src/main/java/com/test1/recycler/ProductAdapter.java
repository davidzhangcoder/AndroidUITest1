package com.test1.recycler;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test1.R;
import com.test1.databinding.ItemProductBinding;
import com.test1.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>
{
    private final List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull

    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ItemProductBinding binding = ItemProductBinding.inflate( LayoutInflater.from(parent.getContext()), parent, false );
        ProductViewHolder productViewHolder= new ProductViewHolder(binding);
        return productViewHolder;
    }


    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind( products.get( position ) );

        holder.binding.imgProduct.setTransitionName( String.valueOf(position) + "_image" );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder
    {
        private ItemProductBinding binding;

        public ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind( Product product )
        {
            binding.setProduct( product );
            binding.imgProduct.setBackground( createProductBackground( product ) );
            binding.imgProduct.setImageResource( product.image );
        }

        @NonNull
        private GradientDrawable createProductBackground(Product product) {
            final GradientDrawable gradientDrawable = (GradientDrawable) ContextCompat.getDrawable(
                    itemView.getContext(), R.drawable.bg_product);

            gradientDrawable.setColor(ContextCompat.getColor(
                    itemView.getContext(), product.color));

            gradientDrawable.setSize(itemView.getWidth(), getDrawableHeight());
            gradientDrawable.mutate();
            return gradientDrawable;
        }

        private int getDrawableHeight() {
            final Context context = itemView.getContext();

            return getAdapterPosition() % 2 == 0
                    ? context.getResources().getDimensionPixelOffset(R.dimen.product_regular_height)
                    : context.getResources().getDimensionPixelOffset(R.dimen.product_large_height);
        }

        public ItemProductBinding getBinding() {
            return binding;
        }
    }
}
