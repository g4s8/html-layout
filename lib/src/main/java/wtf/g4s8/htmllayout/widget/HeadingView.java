package wtf.g4s8.htmllayout.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class HeadingView extends AppCompatTextView {

    public HeadingView(Context context) {
        this(context, null);
    }

    public HeadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
