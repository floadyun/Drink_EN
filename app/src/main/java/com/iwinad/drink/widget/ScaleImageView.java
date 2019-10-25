package com.iwinad.drink.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/*
 * @copyright : yixf
 *
 * @author : yixf
 *
 * @version :1.0
 *
 * @creation date: 2019/9/11
 *
 * @description:个人中心
 */
public class ScaleImageView extends AppCompatImageView {
    private int strokeWidth = 0;

    public boolean isPressed;

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = (int) getTranslationX();
        setTranslationX(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(drawable != null &&isPressed){
            int left = drawable.getBounds().left;
            int top = drawable.getBounds().top;
            int right = drawable.getBounds().right;
            int bottom = drawable.getBounds().bottom;
            drawable.setBounds(strokeWidth,strokeWidth,getWidth() - strokeWidth,getHeight() - strokeWidth);
            drawable.draw(canvas);
            drawable.setBounds(left,top,right,bottom);
        }else{
            super.onDraw(canvas);
        }
    }
}
