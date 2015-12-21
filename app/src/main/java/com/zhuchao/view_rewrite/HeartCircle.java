package com.zhuchao.view_rewrite;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuchao.mobilehealth.R;
import com.zhuchao.utils.Utils;

/**
 * Created by zhuchao on 7/23/15.
 */
public class HeartCircle extends CustomerView {
    private int circleColor_inside;

    private int circleColor_outside;

    private int textColor;

    private int textSize;

    private TextView heartRate;
    public HeartCircle(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        setAttributes(attributeSet);
    }
    public void setHeartRate(String rate){
        heartRate.setText(rate);
        invalidate();
    }
    @Override
    void setAttributes(AttributeSet attributes) {
        setBackgroundResource(android.R.color.transparent);

        TypedArray array=getContext().obtainStyledAttributes(attributes, R.styleable.HeartCircle);

        circleColor_inside=array.getColor(R.styleable.HeartCircle_circle_color_inside, 0);

        circleColor_outside = array.getColor(R.styleable.HeartCircle_circle_color_outside, 0);

        textSize = array.getDimensionPixelSize(R.styleable.HeartCircle_text_size, 0);

        textColor = array.getColor(R.styleable.HeartCircle_text_color, 0);

        heartRate=new TextView(getContext());
        RelativeLayout.LayoutParams speed_params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        speed_params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        speed_params.topMargin=10;
        heartRate.setLayoutParams(speed_params);
        heartRate.setTextSize(textSize - 10);
        heartRate.setTextColor(textColor);
        heartRate.setText("--");
        addView(heartRate);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(circleColor_outside);
        paint.setStrokeWidth(Utils.dpToPx(2, getResources()));
        temp.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);

        Paint paint1 = new Paint();
        paint1.setColor(getResources().getColor(android.R.color.transparent));
        paint1.setAntiAlias(true);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        temp.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - Utils.dpToPx(4, getResources()), paint1);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());
    }

    public int getCircleColor_outside() {
        return circleColor_outside;
    }

    public void setCircleColor_outside(int circleColor_outside) {
        this.circleColor_outside = circleColor_outside;
    }

    public int getCircleColor_inside() {
        return circleColor_inside;
    }

    public void setCircleColor_inside(int circleColor_inside) {
        this.circleColor_inside = circleColor_inside;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
