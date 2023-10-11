package com.example.unquote;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class VerticalBarView extends View {
    private int numSections = 4;  // Number of sections in the vertical bar
    private int spacing = 20;     // Spacing between rectangles and circles
    private int[] rectangleColors = {Color.GRAY, Color.GRAY, Color.GRAY,
            Color.GRAY, Color.GRAY, Color.GRAY,
            Color.GRAY, Color.GRAY};
    private int[] circleColors = {Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY};
    private int barFillColor = ContextCompat.getColor(getContext(),R.color.selectedButtonColor);
    private int firstMultColor = ContextCompat.getColor(getContext(),R.color.lightBlue);
    private int secondtMultColor = ContextCompat.getColor(getContext(),R.color.purple);
    private int thirdMultColor = ContextCompat.getColor(getContext(),R.color.orange);
    private int fourthMultColor = ContextCompat.getColor(getContext(),R.color.wrongButtonColor);
    private Paint paint;

    // Array to store the current heights of the colored regions
    private int[] coloredRegionHeights = {0, 0, 0, 0};

    // Set this flag to true when an animation is in progress
    private boolean isAnimating = false;

    public VerticalBarView(Context context) {
        super(context);
        init();
    }

    public VerticalBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    // Call this method to update the number of consecutively correct answers
    public void setConsecutiveCorrectAnswers(int consecutiveCorrectAnswers) {
        for (int i = 0; i < rectangleColors.length; i++) {
            if (consecutiveCorrectAnswers > i) {
                startColorAnimation(barFillColor,rectangleColors,i);
            }
        }

        if (consecutiveCorrectAnswers == 0) {
            Arrays.fill(circleColors,Color.GRAY);
            Arrays.fill(rectangleColors, Color.GRAY);
            invalidate();
            return;
        }

        if (consecutiveCorrectAnswers >= 2) {
            startColorAnimationDelayed(firstMultColor, circleColors, 0);
        }
        if (consecutiveCorrectAnswers >= 4) {
            startColorAnimationDelayed(secondtMultColor, circleColors, 1);
        }
        if (consecutiveCorrectAnswers >= 6) {
            startColorAnimationDelayed(thirdMultColor, circleColors, 2);
        }
        if (consecutiveCorrectAnswers >= 8) {
            startColorAnimationDelayed(fourthMultColor, circleColors, 3);
        }

    }

    private void startColorAnimationDelayed(final int targetColor, final int[] targetArray, final int index) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startColorAnimation(targetColor, targetArray, index);
            }
        }, 500); // Delay in milliseconds
    }

    private void startColorAnimation(final int targetColor, final int[] targetArray, final int index) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(targetArray[index], targetColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                targetArray[index] = (int) animator.getAnimatedValue();
                invalidate();
            }
        });

        // Set animation duration and start the animation
        colorAnimation.setDuration(1000); // You can adjust the duration as needed
        colorAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int sectionHeight = height / (numSections * 3);


        for (int i = 0; i < numSections; i++) {
            // Calculate positions starting from the bottom
            int rectBottom = height - (i * 3) * sectionHeight;
            int rectTop = rectBottom - sectionHeight;
            int circleY = rectTop - spacing;

            // Draw the first rectangle
            RectF rect1 = new RectF((width * 3) / 8, rectTop, (width * 5) / 8, rectBottom);
            paint.setColor(rectangleColors[i * 2]);
            canvas.drawRect(rect1, paint);

            // Draw the second rectangle above the first with a small gap
            int rect2Top = rectTop - sectionHeight - spacing;
            int rect2Bottom = rectBottom - sectionHeight - spacing;
            RectF rect2 = new RectF((width * 3) / 8, rect2Top, (width * 5) / 8, rect2Bottom);
            paint.setColor(rectangleColors[i * 2 + 1]);
            canvas.drawRect(rect2, paint);

            // Draw a circle above the second rectangle with a small gap
            int circleRadius = sectionHeight / 2 - (spacing);
            int circleTop = rect2Top - circleRadius - spacing / 2;
            int circleX = width / 2;
            paint.setColor(circleColors[i]);
            canvas.drawCircle(circleX, circleTop, circleRadius, paint);
        }
    }

}
