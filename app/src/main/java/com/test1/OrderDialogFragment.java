package com.test1;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionListenerAdapter;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.test1.databinding.FragmentOrderFormBinding;
import com.test1.databinding.LayoutFormOrderStep1Binding;
import com.test1.databinding.LayoutFormOrderStep2Binding;
import com.test1.databinding.LayoutOrderConfirmationBinding;
import com.test1.model.OrderSelectionValue;
import com.test1.model.Product;
import com.test1.model.SizeBean;
import com.test1.recycler.OnItemSelectedListener;
import com.test1.recycler.ProductAdapter;
import com.test1.recycler.ProductItemPaddingDecoration;
import com.test1.recycler.SizeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDialogFragment extends BottomSheetDialogFragment {

    public static enum STEPS { STEP1 , STEP2 }

    private static final String ARG_PRODUCT = "product";

    private static final String ARG_ORDER_SELECTION_VALUE = "orderSelectionValue";

    private static final String ID_COLOR_SUFFIX = "img_color";
    private static final String ID_SIZE_SUFFIX = "txt_size";
    private static final String ID_DATE_SUFFIX = "txt_date";
    private static final String ID_TIME_SUFFIX = "txt_time";

    private List<View> sizeclonedViewList = new ArrayList<View>();
    private List<View> colorclonedViewList = new ArrayList<View>();
    private List<View> dateClonedViewList = new ArrayList<View>();
    private List<View> timeClonedViewList = new ArrayList<View>();

//    private View lastSizeSelected;

    private Product product;
    private FragmentOrderFormBinding binding;

    private Transition selectedViewTransition;

    private OnFragmentInteractionListener mListener;

    private OrderSelectionValue orderSelectionValue;

    private List<SizeBean> sizeBeanList = new ArrayList<SizeBean>();

    private SizeAdapter sizeAdapter;

    public OrderDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrderDialogFragment.
     */
    public static OrderDialogFragment newInstance(Product product , OrderSelectionValue orderSelectionValue) {
        OrderDialogFragment fragment = new OrderDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        args.putSerializable(ARG_ORDER_SELECTION_VALUE , orderSelectionValue);
        orderSelectionValue.setSteps(STEPS.STEP1);
        orderSelectionValue.setProduct( product );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
            orderSelectionValue = (OrderSelectionValue)getArguments().getSerializable( ARG_ORDER_SELECTION_VALUE );
        }

        String[] sizeArray = getResources().getStringArray(R.array.sizesValue);
        sizeBeanList = new ArrayList<SizeBean>();
        for( String size : sizeArray )
        {
            SizeBean sizeBean = new SizeBean();
            sizeBean.setSize( size );
            sizeBeanList.add( sizeBean );
        }

        sizeAdapter = new SizeAdapter( sizeBeanList , step1Listener , orderSelectionValue.getSize() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderFormBinding.inflate( inflater ,container , false  );

        if( product != null )
            return binding.getRoot();
        else
            return new View( getContext() );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if( product == null )
            return;

        binding.setProduct( product );
        binding.imgProduct.setImageResource( product.image );
//        binding.btnGo.setBackground( new ColorDrawable(ContextCompat.getColor( getContext() , product.color ) ) );
        binding.btnGo.setBackgroundColor( ContextCompat.getColor( getContext() , product.color ) );

        selectedViewTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.transition_selected_view);
        selectedViewTransition.setDuration( getResources().getInteger( android.R.integer.config_shortAnimTime ) );


        LayoutFormOrderStep1Binding layoutFormOrderStep1Binding = binding.layoutStep1;
        initStep1( layoutFormOrderStep1Binding );

        if( orderSelectionValue != null && orderSelectionValue.getSize() != 0 )
        {
            TextView sizeTextView = new TextView( getContext() , null , R.attr.selectedTextStyle );
            sizeTextView.setText( orderSelectionValue.getSize() + "" );

            int width = (int)getResources().getDimension( R.dimen.product_color_size );
            int height = (int)getResources().getDimension( R.dimen.product_color_size );
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams( width , height );

            View txtLabelSizeView = binding.txtLabelSize;
            layoutParams.topToTop = txtLabelSizeView.getId();
            layoutParams.bottomToBottom = txtLabelSizeView.getId();
            layoutParams.startToEnd = txtLabelSizeView.getId();
            layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);

            sizeTextView.setLayoutParams( layoutParams );

            ((ViewGroup)binding.frameLayout).addView( sizeTextView );

            sizeclonedViewList.add( sizeTextView );
        }
    }

    private void initStep2(LayoutFormOrderStep2Binding layoutFormOrderStep2Binding )
    {
        binding.txtAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutOrderConfirmationBinding layoutOrderConfirmationBinding = LayoutOrderConfirmationBinding.inflate( LayoutInflater.from( OrderDialogFragment.this.getContext() ) , binding.frameLayout , false );

                binding.frameLayout.removeAllViews();
                binding.frameLayout.addView( layoutOrderConfirmationBinding.getRoot() );
            }
        });

        binding.txtLabelSize.setText( getResources().getString( R.string.txt_label_date ) );

        binding.txtLabelColour.setText( getResources().getString( R.string.txt_label_time ) );

//        Step2Listener step2Listener = new Step2Listener() {
//            @Override
//            public void onDateSelected(final View v) {
//
//                final View copiedView = duplicateView( v , R.attr.selectedTextStyleNoStyle );
//
//                if( copiedView == null )
//                    return;
//
//                ((ViewGroup)binding.frameLayout).addView( copiedView );
//
//                final View view = binding.txtLabelSize;
//
//                copiedView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        v.setSelected( true );
//
//                        TransitionSet transitionSet = new TransitionSet();
//                        selectedViewTransition.addTarget( copiedView );
//
//                        if( dateClonedViewList != null && dateClonedViewList.size() >= 1 ) {
//                            Fade fade = new Fade();
//                            fade.setInterpolator( new AccelerateInterpolator() );
//                            fade.setDuration(100);
//                            for( View dateClonedView : dateClonedViewList )
//                            {
//                                if( dateClonedView.getVisibility() == View.VISIBLE )
//                                    fade.addTarget( dateClonedView );
//
//                                fade.addListener(new TransitionListenerAdapter() {
//                                    @Override
//                                    public void onTransitionEnd(@NonNull Transition transition) {
//                                        for( View targetView : transition.getTargets() ) {
//                                            ((ViewGroup) binding.frameLayout).removeView(targetView);
//                                        }
//                                    }
//                                });
//                            }
//                            transitionSet.addTransition( fade );
//                        }
//                        transitionSet.addTransition( selectedViewTransition );
//
//                        TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );
//
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
//                        layoutParams.topToTop = view.getId();
//                        layoutParams.bottomToBottom = view.getId();
//                        layoutParams.startToEnd = view.getId();
//                        layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
//                        copiedView.setLayoutParams( layoutParams );
//
//                        if( dateClonedViewList != null && dateClonedViewList.size() >= 1 ) {
//                            for( View dateSlonedView : dateClonedViewList )
//                            {
//                                if( dateSlonedView.getVisibility() == View.VISIBLE )
//                                    dateSlonedView.setVisibility( View.GONE );
//                            }
//                        }
//
//                        dateClonedViewList.add( copiedView );
//                    }
//                } );
//
//            }
//
//            @Override
//            public void onTimeSelected(View v)
//            {
//
//                final View copiedView = duplicateView( v , R.attr.selectedTextStyleNoStyle );
//
//                if( copiedView == null )
//                    return;
//
//                ((ViewGroup)binding.frameLayout).addView( copiedView );
//
//                //1.Get Target View
//                //2.Run Transaction Animation
//                final View view = binding.txtLabelColour;
//
//                copiedView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        TransitionSet transitionSet = new TransitionSet();
//                        selectedViewTransition.addTarget( copiedView );
//
//                        if( timeClonedViewList != null && timeClonedViewList.size() >= 1 ) {
//                            Fade fade = new Fade();
//                            fade.setInterpolator( new AccelerateInterpolator() );
//                            fade.setDuration(100);
//                            for( View timeClonedView : timeClonedViewList )
//                            {
//                                if( timeClonedView.getVisibility() == View.VISIBLE )
//                                    fade.addTarget( timeClonedView );
//
//                                fade.addListener(new TransitionListenerAdapter() {
//                                    @Override
//                                    public void onTransitionEnd(@NonNull Transition transition) {
//                                        for( View targetView : transition.getTargets() ) {
//                                            ((ViewGroup) binding.frameLayout).removeView(targetView);
//                                        }
//                                    }
//                                });
//                            }
//                            transitionSet.addTransition( fade );
//                        }
//                        transitionSet.addTransition( selectedViewTransition );
//
//
//                        TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );
//
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
//                        layoutParams.topToTop = view.getId();
//                        layoutParams.bottomToBottom = view.getId();
//                        layoutParams.startToEnd = view.getId();
//                        layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
//                        copiedView.setLayoutParams( layoutParams );
//
//                        if( timeClonedViewList != null && timeClonedViewList.size() >= 1 ) {
//                            for( View timeClonedView : timeClonedViewList )
//                            {
//                                if( timeClonedView.getVisibility() == View.VISIBLE )
//                                    timeClonedView.setVisibility( View.GONE );
//                            }
//                        }
//
//                        timeClonedViewList.add( copiedView );
//                    }
//                } );
//
//            }
//        };

        layoutFormOrderStep2Binding.setListener( step2Listener );

    }

    private void initSizeRecycler(RecyclerView sizeRecycler)
    {
//        String[] sizeArray = getResources().getStringArray(R.array.sizesValue);
//        List<SizeBean> sizeBeanList = new ArrayList<SizeBean>();
//        for( String size : sizeArray )
//        {
//            SizeBean sizeBean = new SizeBean();
//            sizeBean.setSize( size );
//            sizeBeanList.add( sizeBean );
//        }
        sizeRecycler.setAdapter( sizeAdapter );

        LinearLayoutManager manager = new LinearLayoutManager(this.getContext() );
        //设置为横向滑动
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置
        sizeRecycler.setLayoutManager(manager);

        sizeRecycler.addItemDecoration(new ProductItemPaddingDecoration(this.getContext()));
//        sizeRecycler.addOnItemTouchListener(new OnItemSelectedListener(this) {
//            @Override
//            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {
//                OrderDialogFragment.newInstance(fakeProducts.get(position)).show(getSupportFragmentManager(), null);
//            }
//        });
    }

    private void initStep1( LayoutFormOrderStep1Binding layoutFormOrderStep1Binding )
    {
        binding.txtAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.txtAction.setText(R.string.action_book);

                binding.switcher.setDisplayedChild(1);

                for( View sizeclonedView : sizeclonedViewList ) {
                    ((ViewGroup) binding.frameLayout).removeView(sizeclonedView);
                }
                sizeclonedViewList.clear();


                for( View colorclonedView : colorclonedViewList ) {
                    ((ViewGroup) binding.frameLayout).removeView(colorclonedView);
                }
                colorclonedViewList.clear();

                initStep2( binding.layoutStep2 );

                orderSelectionValue.setSteps(STEPS.STEP2);
            }
        });

        binding.txtLabelSize.setText( getResources().getString( R.string.label_size ) );

        binding.txtLabelColour.setText( getResources().getString( R.string.label_color ) );

        initSizeRecycler( layoutFormOrderStep1Binding.sizeRecycler );

//        Step1Listener step1Listener = new Step1Listener() {
//            @Override
//            public void onSizeSelected(final View v) {
//                final View copiedView = duplicateView( v , R.attr.selectedTextStyle );
//
//                if( copiedView == null )
//                    return;
//
////                for( Iterator it = sizeclonedViewList.iterator() ; it.hasNext() ; ) {
////                    View sizeclonedView = (View)it.next();
////                    if( sizeclonedView.getVisibility() == View.GONE ) {
////                        ((ViewGroup) binding.frameLayout).removeView(sizeclonedView);
////                        it.remove();
////                    }
////                }
//
//                ((ViewGroup)binding.frameLayout).addView( copiedView );
//
//                final View view = binding.txtLabelSize;
//
//                copiedView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        v.setSelected( true );
//
//                        TransitionSet transitionSet = new TransitionSet();
//                        selectedViewTransition.addTarget( copiedView );
//
//                        if( sizeclonedViewList != null && sizeclonedViewList.size() >= 1 ) {
//                            Fade fade = new Fade();
//                            fade.setInterpolator( new AccelerateInterpolator() );
//                            fade.setDuration(100);
//                            for( View sizeclonedView : sizeclonedViewList )
//                            {
//                                if( sizeclonedView.getVisibility() == View.VISIBLE )
//                                    fade.addTarget( sizeclonedView );
//
//                                fade.addListener(new TransitionListenerAdapter() {
//                                    @Override
//                                    public void onTransitionEnd(@NonNull Transition transition) {
//                                        for( View targetView : transition.getTargets() ) {
//                                            ((ViewGroup) binding.frameLayout).removeView(targetView);
//                                        }
//                                    }
//                                });
//                            }
//                            transitionSet.addTransition( fade );
//                        }
//                        transitionSet.addTransition( selectedViewTransition );
//
//                        TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );
//
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
//                        layoutParams.topToTop = view.getId();
//                        layoutParams.bottomToBottom = view.getId();
//                        layoutParams.startToEnd = view.getId();
//                        layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
//                        copiedView.setLayoutParams( layoutParams );
//
//                        if( sizeclonedViewList != null && sizeclonedViewList.size() >= 1 ) {
//                            for( View sizeclonedView : sizeclonedViewList )
//                            {
//                                if( sizeclonedView.getVisibility() == View.VISIBLE )
//                                    sizeclonedView.setVisibility( View.GONE );
//                            }
//                        }
//
//                        sizeclonedViewList.add( copiedView );
//                    }
//                } );
//
//            }
//
//            @Override
//            public void onColorSelected(View v) {
//                final View copiedView = duplicateView( v , R.attr.selectedTextStyle );
//
//                if( copiedView == null )
//                    return;
//
//                ((ViewGroup)binding.frameLayout).addView( copiedView );
//
//                //1.Get Target View
//                //2.Run Transaction Animation
//                final View view = binding.txtLabelColour;
//
//                copiedView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        TransitionSet transitionSet = new TransitionSet();
//                        selectedViewTransition.addTarget( copiedView );
//
//                        if( colorclonedViewList != null && colorclonedViewList.size() >= 1 ) {
//                            Fade fade = new Fade();
//                            fade.setInterpolator( new AccelerateInterpolator() );
//                            fade.setDuration(100);
//                            for( View colorclonedView : colorclonedViewList )
//                            {
//                                if( colorclonedView.getVisibility() == View.VISIBLE )
//                                    fade.addTarget( colorclonedView );
//
//                                fade.addListener(new TransitionListenerAdapter() {
//                                    @Override
//                                    public void onTransitionEnd(@NonNull Transition transition) {
//                                        for( View targetView : transition.getTargets() ) {
//                                            ((ViewGroup) binding.frameLayout).removeView(targetView);
//                                        }
//                                    }
//                                });
//                            }
//                            transitionSet.addTransition( fade );
//                        }
//                        transitionSet.addTransition( selectedViewTransition );
//
//
//                        TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );
//
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
//                        layoutParams.topToTop = view.getId();
//                        layoutParams.bottomToBottom = view.getId();
//                        layoutParams.startToEnd = view.getId();
//                        layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
//                        copiedView.setLayoutParams( layoutParams );
//
//                        if( colorclonedViewList != null && colorclonedViewList.size() >= 1 ) {
//                            for( View colorclonedView : colorclonedViewList )
//                            {
//                                if( colorclonedView.getVisibility() == View.VISIBLE )
//                                    colorclonedView.setVisibility( View.GONE );
//                            }
//                        }
//
//                        colorclonedViewList.add( copiedView );
//                    }
//                } );
//
//            }
//        };

        layoutFormOrderStep1Binding.setListener( step1Listener );
    }

    private void getTargetView()
    {

    }

    private View duplicateView(View sourceView , int defStyleAttr)
    {
        //1.Create new TextView
        //2.Set Shape, Text, Layout
        //3.Set position

        String entryName = this.getResources().getResourceEntryName(sourceView.getId());
        this.getResources().getResourceName(sourceView.getId());
        this.getResources().getResourceTypeName(sourceView.getId());

        View resultView = null;

        if( entryName.startsWith( ID_COLOR_SUFFIX ) ) {
            ImageView imageView = new CircleImageView( this.getContext() );

            final ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(
                            sourceView.getWidth(),
                            sourceView.getHeight());

            layoutParams.topToTop = ((ViewGroup)sourceView.getParent().getParent()).getId();
            layoutParams.leftToLeft = ((ViewGroup)sourceView.getParent().getParent()).getId();
            layoutParams.setMargins( (int)sourceView.getX() , (int)sourceView.getY() , 0 , 0);

            imageView.setLayoutParams( layoutParams );
            imageView.setImageDrawable( ((ImageView)sourceView).getDrawable() );
            resultView = imageView;
        }
        else if( entryName.startsWith( ID_SIZE_SUFFIX ) || entryName.startsWith( ID_DATE_SUFFIX ) || entryName.startsWith( ID_TIME_SUFFIX ) )
        {
            TextView textView = new TextView( this.getContext() , null , defStyleAttr );

            if( entryName.startsWith( ID_DATE_SUFFIX ) || entryName.startsWith( ID_TIME_SUFFIX ) )
            {
                final ConstraintLayout.LayoutParams layoutParams =
                        new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.topToTop = ((ViewGroup) sourceView.getParent().getParent()).getId();
                layoutParams.leftToLeft = ((ViewGroup) sourceView.getParent().getParent()).getId();
                layoutParams.setMargins((int) sourceView.getX(), (int) sourceView.getY(), 0, 0);

                textView.setLayoutParams(layoutParams);
            }
            else {
                final ConstraintLayout.LayoutParams layoutParams =
                        new ConstraintLayout.LayoutParams(
                                sourceView.getWidth(),
                                sourceView.getHeight());

                layoutParams.topToTop = ((ViewGroup) sourceView.getParent().getParent().getParent().getParent()).getId();
                layoutParams.leftToLeft = ((ViewGroup) sourceView.getParent().getParent().getParent().getParent()).getId();

                int[] originalPos = new int[2];
                sourceView.getLocationInWindow(originalPos);
                //or view.getLocationOnScreen(originalPos)
                int x = originalPos[0];
                int y = originalPos[1];

                layoutParams.setMargins(x, (int) sourceView.getY() + (int)((View)sourceView.getParent().getParent()).getY() , 0, 0);
                //layoutParams.setMargins((int) sourceView.getX() + (int)((View)sourceView.getParent().getParent()).getX(), (int) sourceView.getY() + (int)((View)sourceView.getParent().getParent()).getY() , 0, 0);

                textView.setLayoutParams(layoutParams);
            }

            textView.setText( ((TextView)sourceView).getText().toString().replace("\n"," ") );
//            textView.setBackground( ContextCompat.getDrawable( getContext(), R.style.Widget_Size ) );
            resultView = textView;
        }

        return resultView;
    }

    private Drawable createProductImageDrawable(Product product) {
        final ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape());
        background.getPaint().setColor(ContextCompat.getColor(getContext(), product.color));

        final BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), product.image));

        final LayerDrawable layerDrawable = new LayerDrawable
                (new Drawable[]{background, bitmapDrawable});

        final int padding = (int) getResources().getDimension(R.dimen.spacing_huge);
        layerDrawable.setLayerInset(1, padding, padding, padding, padding);

        return layerDrawable;
    }

    @BindingAdapter("app:spanOffset")
    public static void setItemSpan(View v, int spanOffset) {
        final String itemText = ((TextView) v).getText().toString();
        final SpannableString sString = new SpannableString(itemText);

        sString.setSpan(new RelativeSizeSpan(1.65f), itemText.length() - spanOffset, itemText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) v).setText(sString);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    Step1Listener step1Listener = new Step1Listener() {
        @Override
        public void onSizeSelected(final View v) {
            final View copiedView = duplicateView( v , R.attr.selectedTextStyle );

            if( copiedView == null )
                return;

            for( SizeBean sizeBean : sizeBeanList )
            {
                if( sizeBean.getSize().equalsIgnoreCase( ((TextView) v).getText().toString() ) )
                    sizeBean.setSelected( true );
                else
                    sizeBean.setSelected( false );
            }

//                for( Iterator it = sizeclonedViewList.iterator() ; it.hasNext() ; ) {
//                    View sizeclonedView = (View)it.next();
//                    if( sizeclonedView.getVisibility() == View.GONE ) {
//                        ((ViewGroup) binding.frameLayout).removeView(sizeclonedView);
//                        it.remove();
//                    }
//                }

            orderSelectionValue.setSize( Integer.parseInt(((TextView)v).getText().toString()) );
            ((ViewGroup)binding.frameLayout).addView( copiedView );

            final View view = binding.txtLabelSize;

            copiedView.post(new Runnable() {
                @Override
                public void run() {

                    sizeAdapter.notifyDataSetChanged();

//                    Log.i(  "onSizeSelected" , "lastSizeSelected1-" + ( lastSizeSelected==null?"":((TextView) lastSizeSelected).getText().toString() ) );
//
//                    if( lastSizeSelected != null )
//                    {
//                        lastSizeSelected.setSelected( false );
//                        for( SizeBean sizeBean : sizeBeanList )
//                        {
//                            if( sizeBean.getSize().equalsIgnoreCase( ((TextView) lastSizeSelected).getText().toString() ) )
//                            {
//                                sizeBean.setSelected( false );
//                            }
//                        }
//                    }
//
//                    lastSizeSelected = v;
//                    v.setSelected( true );
//
//                    Log.i(  "onSizeSelected" , "lastSizeSelected2-" + ( lastSizeSelected==null?"":((TextView) lastSizeSelected).getText().toString() ) );

                    TransitionSet transitionSet = new TransitionSet();
                    selectedViewTransition.addTarget( copiedView );

                    if( sizeclonedViewList != null && sizeclonedViewList.size() >= 1 ) {
                        Fade fade = new Fade();
                        fade.setInterpolator( new AccelerateInterpolator() );
                        fade.setDuration(100);
                        for( View sizeclonedView : sizeclonedViewList )
                        {
                            if( sizeclonedView.getVisibility() == View.VISIBLE )
                                fade.addTarget( sizeclonedView );

                            fade.addListener(new TransitionListenerAdapter() {
                                @Override
                                public void onTransitionEnd(@NonNull Transition transition) {
                                    for( View targetView : transition.getTargets() ) {
                                        ((ViewGroup) binding.frameLayout).removeView(targetView);
                                    }
                                }
                            });
                        }
                        transitionSet.addTransition( fade );
                    }
                    transitionSet.addTransition( selectedViewTransition );

                    TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
                    layoutParams.topToTop = view.getId();
                    layoutParams.bottomToBottom = view.getId();
                    layoutParams.startToEnd = view.getId();
                    layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
                    copiedView.setLayoutParams( layoutParams );

                    if( sizeclonedViewList != null && sizeclonedViewList.size() >= 1 ) {
                        for( View sizeclonedView : sizeclonedViewList )
                        {
                            if( sizeclonedView.getVisibility() == View.VISIBLE )
                                sizeclonedView.setVisibility( View.GONE );
                        }
                    }

                    sizeclonedViewList.add( copiedView );
                }
            } );

        }

        @Override
        public void onColorSelected(View v) {
            final View copiedView = duplicateView( v , R.attr.selectedTextStyle );

            if( copiedView == null )
                return;

            ((ViewGroup)binding.frameLayout).addView( copiedView );

            //1.Get Target View
            //2.Run Transaction Animation
            final View view = binding.txtLabelColour;

            copiedView.post(new Runnable() {
                @Override
                public void run() {


                    TransitionSet transitionSet = new TransitionSet();
                    selectedViewTransition.addTarget( copiedView );

                    if( colorclonedViewList != null && colorclonedViewList.size() >= 1 ) {
                        Fade fade = new Fade();
                        fade.setInterpolator( new AccelerateInterpolator() );
                        fade.setDuration(100);
                        for( View colorclonedView : colorclonedViewList )
                        {
                            if( colorclonedView.getVisibility() == View.VISIBLE )
                                fade.addTarget( colorclonedView );

                            fade.addListener(new TransitionListenerAdapter() {
                                @Override
                                public void onTransitionEnd(@NonNull Transition transition) {
                                    for( View targetView : transition.getTargets() ) {
                                        ((ViewGroup) binding.frameLayout).removeView(targetView);
                                    }
                                }
                            });
                        }
                        transitionSet.addTransition( fade );
                    }
                    transitionSet.addTransition( selectedViewTransition );


                    TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
                    layoutParams.topToTop = view.getId();
                    layoutParams.bottomToBottom = view.getId();
                    layoutParams.startToEnd = view.getId();
                    layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
                    copiedView.setLayoutParams( layoutParams );

                    if( colorclonedViewList != null && colorclonedViewList.size() >= 1 ) {
                        for( View colorclonedView : colorclonedViewList )
                        {
                            if( colorclonedView.getVisibility() == View.VISIBLE )
                                colorclonedView.setVisibility( View.GONE );
                        }
                    }

                    colorclonedViewList.add( copiedView );
                }
            } );

        }
    };

    Step2Listener step2Listener = new Step2Listener() {
        @Override
        public void onDateSelected(final View v) {

            final View copiedView = duplicateView( v , R.attr.selectedTextStyleNoStyle );

            if( copiedView == null )
                return;

            ((ViewGroup)binding.frameLayout).addView( copiedView );

            final View view = binding.txtLabelSize;

            copiedView.post(new Runnable() {
                @Override
                public void run() {

                    v.setSelected( true );

                    TransitionSet transitionSet = new TransitionSet();
                    selectedViewTransition.addTarget( copiedView );

                    if( dateClonedViewList != null && dateClonedViewList.size() >= 1 ) {
                        Fade fade = new Fade();
                        fade.setInterpolator( new AccelerateInterpolator() );
                        fade.setDuration(100);
                        for( View dateClonedView : dateClonedViewList )
                        {
                            if( dateClonedView.getVisibility() == View.VISIBLE )
                                fade.addTarget( dateClonedView );

                            fade.addListener(new TransitionListenerAdapter() {
                                @Override
                                public void onTransitionEnd(@NonNull Transition transition) {
                                    for( View targetView : transition.getTargets() ) {
                                        ((ViewGroup) binding.frameLayout).removeView(targetView);
                                    }
                                }
                            });
                        }
                        transitionSet.addTransition( fade );
                    }
                    transitionSet.addTransition( selectedViewTransition );

                    TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
                    layoutParams.topToTop = view.getId();
                    layoutParams.bottomToBottom = view.getId();
                    layoutParams.startToEnd = view.getId();
                    layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
                    copiedView.setLayoutParams( layoutParams );

                    if( dateClonedViewList != null && dateClonedViewList.size() >= 1 ) {
                        for( View dateSlonedView : dateClonedViewList )
                        {
                            if( dateSlonedView.getVisibility() == View.VISIBLE )
                                dateSlonedView.setVisibility( View.GONE );
                        }
                    }

                    dateClonedViewList.add( copiedView );
                }
            } );

        }

        @Override
        public void onTimeSelected(View v)
        {

            final View copiedView = duplicateView( v , R.attr.selectedTextStyleNoStyle );

            if( copiedView == null )
                return;

            ((ViewGroup)binding.frameLayout).addView( copiedView );

            //1.Get Target View
            //2.Run Transaction Animation
            final View view = binding.txtLabelColour;

            copiedView.post(new Runnable() {
                @Override
                public void run() {


                    TransitionSet transitionSet = new TransitionSet();
                    selectedViewTransition.addTarget( copiedView );

                    if( timeClonedViewList != null && timeClonedViewList.size() >= 1 ) {
                        Fade fade = new Fade();
                        fade.setInterpolator( new AccelerateInterpolator() );
                        fade.setDuration(100);
                        for( View timeClonedView : timeClonedViewList )
                        {
                            if( timeClonedView.getVisibility() == View.VISIBLE )
                                fade.addTarget( timeClonedView );

                            fade.addListener(new TransitionListenerAdapter() {
                                @Override
                                public void onTransitionEnd(@NonNull Transition transition) {
                                    for( View targetView : transition.getTargets() ) {
                                        ((ViewGroup) binding.frameLayout).removeView(targetView);
                                    }
                                }
                            });
                        }
                        transitionSet.addTransition( fade );
                    }
                    transitionSet.addTransition( selectedViewTransition );


                    TransitionManager.beginDelayedTransition( (ViewGroup) binding.getRoot() , transitionSet );

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)copiedView.getLayoutParams();
                    layoutParams.topToTop = view.getId();
                    layoutParams.bottomToBottom = view.getId();
                    layoutParams.startToEnd = view.getId();
                    layoutParams.setMargins( (int)getResources().getDimension( R.dimen.spacing_medium ) , 0 , 0 , 0);
                    copiedView.setLayoutParams( layoutParams );

                    if( timeClonedViewList != null && timeClonedViewList.size() >= 1 ) {
                        for( View timeClonedView : timeClonedViewList )
                        {
                            if( timeClonedView.getVisibility() == View.VISIBLE )
                                timeClonedView.setVisibility( View.GONE );
                        }
                    }

                    timeClonedViewList.add( copiedView );
                }
            } );

        }
    };


    public interface Step1Listener {
        void onSizeSelected(View v);

        void onColorSelected(View v);
    }

    public interface Step2Listener {
        void onDateSelected(View v);

        void onTimeSelected(View v);
    }

    public static class OrderSelection {
        public int size = 0;
        public int color = 0;
        public String date = "";
        public String time = "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
