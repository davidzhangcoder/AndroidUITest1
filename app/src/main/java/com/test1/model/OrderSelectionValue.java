package com.test1.model;

import com.test1.OrderDialogFragment;

import java.io.Serializable;

public class OrderSelectionValue implements Serializable
{
    public OrderDialogFragment.STEPS steps;

    public Product product;

    public int size = 0;

    public int color = 0;


    public OrderDialogFragment.STEPS getSteps() {
        return steps;
    }

    public void setSteps(OrderDialogFragment.STEPS steps) {
        this.steps = steps;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
