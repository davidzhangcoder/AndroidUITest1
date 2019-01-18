package com.test1.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SearchPanelBehavior extends CoordinatorLayout.Behavior<TextView>
{

    public SearchPanelBehavior() {
    }

    public SearchPanelBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TextView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type)
    {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TextView child, @NonNull View target,
                                  int dx, int dy, @NonNull int[] consumed, int type) {
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);

        Log.i( "SearchPanelBehavior" , dy+"" );

        int height = child.getHeight();
        float currentTranslationY = child.getTranslationY();

        if( dy >= 0 ) {
            if ( Math.abs(currentTranslationY) + dy < height) {
                child.setTranslationY(-dy);
                consumed[1] = dy;
            } else {
                child.setTranslationY(-height);
                consumed[1] = height - (int) Math.abs(currentTranslationY);
            }
        }
        else
        {
            if ( Math.abs(currentTranslationY) + Math.abs(dy) < height) {
                child.setTranslationY(Math.abs(dy));
                consumed[1] = dy;
            } else {
                child.setTranslationY(height);
                consumed[1] = height - (int) Math.abs(currentTranslationY);
            }
        }

    }
}
