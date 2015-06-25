package com.test.app.maptestapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class MySupportMapFragment extends SupportMapFragment {
    private View mOriginalContentView;
    private TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }


    public void addSupportMapListener(SupportMapListener supportMapListener) {
        mTouchView.setSupportMapListener(supportMapListener);
    }

    public class TouchableWrapper extends FrameLayout {
        private SupportMapListener supportMapListener;
        public boolean isMoving = false;

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (supportMapListener != null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        supportMapListener.onActionDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        supportMapListener.onActionUp();
                        if (isMoving) {
                            isMoving = false;
                            supportMapListener.onMoveEnd();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMoving = true;
                        break;
                }
            }

            return super.dispatchTouchEvent(event);
        }

        public void setSupportMapListener(SupportMapListener supportMapListener) {
            this.supportMapListener = supportMapListener;
        }

    }

    public interface SupportMapListener {
        /**
         * Method is invoked when user ends moving map
         */
        abstract void onMoveEnd();

        /**
         * Method is invoked when user has taken hands off the map
         */
        abstract void onActionUp();

        /**
         * Method is invoked when user has touch the map
         */
        abstract void onActionDown();
    }
}