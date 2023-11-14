package com.mahdshahzad.triviam;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class VerticalBarView extends View {
    private int numSections = 4;  // Number of sections in the vertical bar
    private int spacing = 20;     // Spacing between rectangles and circles
    private int[] rectangleColors = new int[8];
    private float[] rectangleFillPercent = new float[8];
    private int[] circleColors = new int[4];
    private int barFillColor = ContextCompat.getColor(getContext(),R.color.selectedButtonColor);
    private int firstMultColor = ContextCompat.getColor(getContext(),R.color.lightBlue);
    private int secondtMultColor = ContextCompat.getColor(getContext(),R.color.purple);
    private int thirdMultColor = ContextCompat.getColor(getContext(),R.color.orange);
    private int fourthMultColor = ContextCompat.getColor(getContext(),R.color.wrongButtonColor);
    private Paint paint;
    private static final int BASE_ALPHA = 70;  // 0 is fully transparent, 255 is fully opaque.



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
        Arrays.fill(rectangleColors, barFillColor);
        Arrays.fill(circleColors, Color.TRANSPARENT);
        Arrays.fill(rectangleFillPercent, 0f);
    }

    public void setAsSkeleton() {
        Arrays.fill(circleColors,Color.argb(BASE_ALPHA, Color.red(Color.GRAY), Color.green(Color.GRAY), Color.blue(Color.GRAY)));
        Arrays.fill(rectangleColors, Color.argb(BASE_ALPHA, Color.red(Color.GRAY), Color.green(Color.GRAY), Color.blue(Color.GRAY)));
        Arrays.fill(rectangleFillPercent, 1f);
        invalidate();
    }

    // Call this method to update the number of consecutively correct answers
    public void setConsecutiveCorrectAnswers(int consecutiveCorrectAnswers) {
        if (consecutiveCorrectAnswers > 8) {
            return;
        }

        if (consecutiveCorrectAnswers == 0) {
            for (int i = 0; i < rectangleColors.length; i++){
                startFillAnimation(0f,i,3000);
            }
            for (int i = 0; i < circleColors.length; i++){
                startColorAnimation(Color.TRANSPARENT, circleColors, i,3000);
            }
            return;
        }

        rectangleColors[consecutiveCorrectAnswers-1] = Color.BLACK;
        startColorAnimation(barFillColor,rectangleColors,consecutiveCorrectAnswers-1);
        startFillAnimation(1f,consecutiveCorrectAnswers-1);


        if (consecutiveCorrectAnswers == 8) {
            startColorAnimationDelayed(fourthMultColor, circleColors, 3);
        } else if (consecutiveCorrectAnswers == 6) {
            startColorAnimationDelayed(thirdMultColor, circleColors, 2);
        } else if (consecutiveCorrectAnswers == 4) {
            startColorAnimationDelayed(secondtMultColor, circleColors, 1);
        } else if (consecutiveCorrectAnswers == 2) {
            startColorAnimationDelayed(firstMultColor, circleColors, 0);
        }

    }

    private void startColorAnimationDelayed(final int targetColor, final int[] targetArray, final int index) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startColorAnimation(targetColor, targetArray, index, 500);
                for (int i = 0; i <= index; i++) {
                    if (i != index) {
                        startColorAnimation(targetColor, targetArray, i,6000);
                    }
                    startColorAnimation(targetColor,rectangleColors,i*2,6000);
                    startColorAnimation(targetColor,rectangleColors,i*2+1,6000);
                }
            }
        }, 1150); // Delay in milliseconds
    }


    private void startColorAnimation(final int targetColor, final int[] targetArray, final int index ) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(targetArray[index], targetColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                targetArray[index] = (int) animator.getAnimatedValue();
                invalidate();
            }
        });

        // Set animation duration and start the animation
        colorAnimation.setDuration(1500); // You can adjust the duration as needed
        colorAnimation.start();
    }


    private void startColorAnimation(final int targetColor, final int[] targetArray, final int index, final int duration) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(targetArray[index], targetColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                targetArray[index] = (int) animator.getAnimatedValue();
                invalidate();
            }
        });

        // Set animation duration and start the animation
        colorAnimation.setDuration(duration); // You can adjust the duration as needed
        colorAnimation.start();
    }
    private void startFillAnimation(final float targetPercent, final int index) {
        ValueAnimator fillAnimation = ValueAnimator.ofFloat(rectangleFillPercent[index], targetPercent);
        fillAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rectangleFillPercent[index] = (float) animator.getAnimatedValue();
                invalidate();
            }
        });

        // Set animation duration and start the animation
        fillAnimation.setDuration(1500); // Adjust duration as needed
        fillAnimation.start();
    }

    private void startFillAnimation(final float targetPercent, final int index, final int duration) {
        ValueAnimator fillAnimation = ValueAnimator.ofFloat(rectangleFillPercent[index], targetPercent);
        fillAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rectangleFillPercent[index] = (float) animator.getAnimatedValue();
                invalidate();
            }
        });

        // Set animation duration and start the animation
        fillAnimation.setDuration(duration); // Adjust duration as needed
        fillAnimation.start();
    }

    private void flashAnimation(int targetColor, int[] targetArray, int index, int timeBetweenFlashes, int numberOfFlashes) {
        ValueAnimator flashAnimator = ValueAnimator.ofArgb(targetColor, Color.TRANSPARENT, targetColor);
        flashAnimator.setDuration(timeBetweenFlashes);
        flashAnimator.setRepeatCount(numberOfFlashes - 1); // minus 1 because it already animates once by default
        flashAnimator.setRepeatMode(ValueAnimator.REVERSE);

        flashAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Here you might need to modify how you update the color of your orb with the new value
                // This is just an example of how you might do it:
                int flashColor = (int) animation.getAnimatedValue();
                targetArray[index] = flashColor;
                invalidate();
            }
        });


        flashAnimator.start();
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
            float fillHeight1 = sectionHeight * rectangleFillPercent[i * 2];
            RectF rect1 = new RectF((width * 3) / 8, rectBottom - fillHeight1, (width * 5) / 8, rectBottom);
            paint.setColor(rectangleColors[i * 2]);
            canvas.drawRect(rect1, paint);

            // Draw the second rectangle above the first with a small gap
            int rect2Top = rectTop - sectionHeight - spacing;
            int rect2Bottom = rectBottom - sectionHeight - spacing;
            float fillHeight2 = sectionHeight * rectangleFillPercent[i * 2 + 1];
            RectF rect2 = new RectF((width * 3) / 8, rect2Bottom - fillHeight2, (width * 5) / 8, rect2Bottom);
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
