package com.test1.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test1.OrderDialogFragment;
import com.test1.databinding.SizeBinding;
import com.test1.model.SizeBean;

import java.util.ArrayList;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder>
{
    private List<SizeBean> sizeBeanList = new ArrayList<SizeBean>();

    private OrderDialogFragment.Step1Listener step1Listener;

    public SizeAdapter(List<SizeBean> sizeBeanList , OrderDialogFragment.Step1Listener step1Listener)
    {
        this.sizeBeanList = sizeBeanList;
        this.step1Listener = step1Listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SizeBinding sizeBinding = SizeBinding.inflate( LayoutInflater.from( parent.getContext() ) , parent , false );
        sizeBinding.setListener( step1Listener );
        SizeViewHolder sizeViewHolder = new SizeViewHolder( sizeBinding );
        return sizeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        holder.binding( sizeBeanList.get( position ).getSize() );

        holder.sizeBinding.txtSize1.setSelected( false );

        if( sizeBeanList.get( position ).isSelected() )
        {
            holder.sizeBinding.txtSize1.setSelected( true );
        }
    }

    @Override
    public int getItemCount() {
        return sizeBeanList.size();
    }

    class SizeViewHolder extends RecyclerView.ViewHolder
    {
        private SizeBinding sizeBinding;

        public SizeViewHolder(SizeBinding sizeBinding) {
            super(sizeBinding.getRoot());
            this.sizeBinding = sizeBinding;
        }

        public void binding(String size)
        {
            this.sizeBinding.setSize( size );
        }

    }

}
