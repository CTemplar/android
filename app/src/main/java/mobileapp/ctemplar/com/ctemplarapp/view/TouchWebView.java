package mobileapp.ctemplar.com.ctemplarapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class TouchWebView extends WebView {
    public TouchWebView(Context context) {
        super(context);
    }

    public TouchWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
//        if (event.getAction() != MotionEvent.ACTION_MOVE) {
//            requestDisallowInterceptTouchEvent(true);
//            return false;
//        }

        if (event.getPointerCount() > 1 && event.getAction() != MotionEvent.ACTION_MOVE) {
            requestDisallowInterceptTouchEvent(true);
        }

        return super.onTouchEvent(event);
    }
}
