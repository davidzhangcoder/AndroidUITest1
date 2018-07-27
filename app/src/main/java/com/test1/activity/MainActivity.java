package com.test1.activity;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.test1.OrderDialogFragment;
import com.test1.R;
import com.test1.databinding.ActivityMainBinding;
import com.test1.model.OrderSelectionValue;
import com.test1.model.Product;
import com.test1.recycler.OnItemSelectedListener;
import com.test1.recycler.ProductAdapter;
import com.test1.recycler.ProductItemPaddingDecoration;


import java.util.List;

public class MainActivity extends AppCompatActivity implements OrderDialogFragment.OnFragmentInteractionListener
{
    private static final String ARG_ORDER_SELECTION_VALUE = "ARG_ORDER_SELECTION_VALUE";

    private final List<Product> fakeProducts = Product.createFakeProducts();

    private OrderDialogFragment orderDialogFragment;

    private OrderSelectionValue orderSelectionValue;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView( this , R.layout.activity_main );

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if( savedInstanceState != null && savedInstanceState.getSerializable(ARG_ORDER_SELECTION_VALUE) != null )
            orderSelectionValue = (OrderSelectionValue)savedInstanceState.getSerializable(ARG_ORDER_SELECTION_VALUE);
        else
            orderSelectionValue = new OrderSelectionValue();

//        initRecycler( binding.productsRecycler , orderSelectionValue );

    }

    private void initRecycler(RecyclerView productsRecycler , final OrderSelectionValue orderSelectionValue) {
        productsRecycler.setAdapter(new ProductAdapter(fakeProducts));

        productsRecycler.addItemDecoration(new ProductItemPaddingDecoration(this));
        productsRecycler.addOnItemTouchListener(new OnItemSelectedListener(this) {
            @Override
            public void onItemSelected(RecyclerView.ViewHolder holder, int position)
            {
                boolean isTabletOrLand = getResources().getBoolean(R.bool.isTabletOrLand);

                if( !isTabletOrLand )
                {

//                    BottomSheetDialog dialog = new BottomSheetDialog( MainActivity.this );
//                    View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_dialog, null);
//
//
//                    dialog.setContentView(view);
//                    dialog.show();

                    orderDialogFragment = OrderDialogFragment.newInstance(fakeProducts.get(position),orderSelectionValue);
                    orderDialogFragment.show(getSupportFragmentManager(), null);

                }
                else {

                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    orderDialogFragment = OrderDialogFragment.newInstance(fakeProducts.get(position),orderSelectionValue);
                    fragmentTransaction.replace(R.id.fragment, orderDialogFragment);
                    fragmentTransaction.commit();

                }
            }
        });

        if( orderSelectionValue.getSteps() != null && orderSelectionValue.getProduct() != null )
        {
            boolean isTabletOrLand = getResources().getBoolean(R.bool.isTabletOrLand);

            if( !isTabletOrLand )
            {
                orderDialogFragment = OrderDialogFragment.newInstance(orderSelectionValue.getProduct(),orderSelectionValue);
                orderDialogFragment.show(getSupportFragmentManager(), null);

            }
            else {

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                orderDialogFragment = OrderDialogFragment.newInstance(orderSelectionValue.getProduct(),orderSelectionValue);
                fragmentTransaction.replace(R.id.fragment, orderDialogFragment);
                fragmentTransaction.commit();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( orderDialogFragment != null )
            orderDialogFragment.dismiss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initRecycler( binding.productsRecycler , orderSelectionValue );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if( orderSelectionValue != null )
            outState.putSerializable(ARG_ORDER_SELECTION_VALUE,orderSelectionValue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
