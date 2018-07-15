package com.test1.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;

import com.test1.OrderDialogFragment;
import com.test1.R;
import com.test1.databinding.ActivityMainBinding;
import com.test1.model.Product;
import com.test1.recycler.OnItemSelectedListener;
import com.test1.recycler.ProductAdapter;
import com.test1.recycler.ProductItemPaddingDecoration;


import java.util.List;

public class MainActivity extends AppCompatActivity implements OrderDialogFragment.OnFragmentInteractionListener
{

    private final List<Product> fakeProducts = Product.createFakeProducts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView( this , R.layout.activity_main );

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initRecycler( binding.productsRecycler );

    }

    private void initRecycler(RecyclerView productsRecycler) {
        productsRecycler.setAdapter(new ProductAdapter(fakeProducts));

        productsRecycler.addItemDecoration(new ProductItemPaddingDecoration(this));
        productsRecycler.addOnItemTouchListener(new OnItemSelectedListener(this) {
            @Override
            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {
                OrderDialogFragment.newInstance(fakeProducts.get(position)).show(getSupportFragmentManager(), null);
            }
        });
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
