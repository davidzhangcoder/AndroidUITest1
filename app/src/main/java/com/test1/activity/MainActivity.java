package com.test1.activity;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
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
import com.test1.fragment.MainLeftPanelFragment;
import com.test1.model.OrderSelectionValue;
import com.test1.model.Product;
import com.test1.recycler.OnItemSelectedListener;
import com.test1.recycler.ProductAdapter;
import com.test1.recycler.ProductItemPaddingDecoration;


import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OrderDialogFragment.OnFragmentInteractionListener,
        MainLeftPanelFragment.OnFragmentInteractionListener
{
    private static final String ARG_ORDER_SELECTION_VALUE = "ARG_ORDER_SELECTION_VALUE";

    private final List<Product> fakeProducts = Product.createFakeProducts();

    private MainLeftPanelFragment mainLeftPanelFragment;
    private OrderDialogFragment orderDialogFragment;

    private OrderSelectionValue orderSelectionValue;

    private ActivityMainBinding binding;

    private Transition sharedElementProductTransition;

    private boolean isTabletOrLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTabletOrLand = getResources().getBoolean(R.bool.isTabletOrLand);

        binding = DataBindingUtil.setContentView( this , R.layout.activity_main );

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if( savedInstanceState != null && savedInstanceState.getSerializable(ARG_ORDER_SELECTION_VALUE) != null )
            orderSelectionValue = (OrderSelectionValue)savedInstanceState.getSerializable(ARG_ORDER_SELECTION_VALUE);
        else
            orderSelectionValue = new OrderSelectionValue();

        if( isTabletOrLand ) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            mainLeftPanelFragment = MainLeftPanelFragment.newInstance();
            fragmentTransaction.replace(R.id.leftFragment, mainLeftPanelFragment);
            fragmentTransaction.commit();
        }

//        initRecycler( binding.productsRecycler , orderSelectionValue );

    }

    private void initRecycler( RecyclerView productsRecycler , final OrderSelectionValue orderSelectionValue ) {

        productsRecycler.setAdapter(new ProductAdapter(fakeProducts));
        productsRecycler.addItemDecoration(new ProductItemPaddingDecoration(this));
        productsRecycler.addOnItemTouchListener(new OnItemSelectedListener(this) {
            @Override
            public void onItemSelected(RecyclerView.ViewHolder holder, int position)
            {

                if( !isTabletOrLand )
                {
                    orderDialogFragment = OrderDialogFragment.newInstance(fakeProducts.get(position),orderSelectionValue);
                    orderDialogFragment.show(getSupportFragmentManager(), null);
                }
                else {

                    ProductAdapter.ProductViewHolder productViewHolder = (ProductAdapter.ProductViewHolder)holder;

                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();

                    int[] location = new int[2];
                    productViewHolder.getBinding().imgProduct.getLocationOnScreen( location );
                    int width = productViewHolder.getBinding().imgProduct.getWidth();
                    int height = productViewHolder.getBinding().imgProduct.getHeight();
                    int left = location[0];
                    int top = location[1];
                    orderDialogFragment = OrderDialogFragment.newInstance(fakeProducts.get(position),orderSelectionValue,width,height,left,top);

                    fragmentTransaction.replace(R.id.fragment, orderDialogFragment);
                    fragmentTransaction.commit();

                }
            }
        });

        if( orderSelectionValue.getSteps() != null && orderSelectionValue.getProduct() != null )
        {

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

//        R.transition.transition_shared_element_product
        sharedElementProductTransition = TransitionInflater.from( this ).inflateTransition( android.R.transition.move );
        sharedElementProductTransition.setDuration( 1000 );

        if( !isTabletOrLand )
            initRecycler( binding.productsRecycler , orderSelectionValue );
        else
            initRecycler( mainLeftPanelFragment.getBinding().productsRecyclerTablet , orderSelectionValue );
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
