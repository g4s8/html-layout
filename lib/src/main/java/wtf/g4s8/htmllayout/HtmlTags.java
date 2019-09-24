package wtf.g4s8.htmllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import wtf.g4s8.htmllayout.widget.HeadingView;

final class HtmlTags {

    private static final Set<String> HEADINGS = new HashSet<>(Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6"));

    private static final int TAG = R.id.html_tag_id;

    View inflate(XmlPullParser parser, View parent, Context context, AttributeSet attrs) throws IOException, XmlPullParserException {
        final String name = parser.getName();
        if ("html".equals(name)) {
            final LinearLayout view = new LinearLayout(withTheme(context, name), attrs);
            view.setOrientation(LinearLayout.VERTICAL);
            view.setTag(TAG, name);
            return view;
        }
        if (HEADINGS.contains(name)) {
            final HeadingView view = new HeadingView(withTheme(context, name), attrs);
            final String text = parser.nextText();
            view.setText(text);
            view.setTag(TAG, name);
            return view;
        }
        if ("p".equals(name)) {
            final HeadingView view = new HeadingView(withTheme(context, name), attrs);
            final String text = parser.nextText();
            view.setText(text);
            view.setTag(TAG, name);
            return view;
        }
        if ("ol".equals(name) || "ul".equals(name)) {
            final LinearLayout view = new LinearLayout(withTheme(context, name), attrs);
            view.setOrientation(LinearLayout.VERTICAL);
            view.setTag(TAG, name);
            return view;
        }
        if ("li".equals(name)) {
            final HeadingView view = new HeadingView(withTheme(context, name), attrs);
            final String text = parser.nextText();
            view.setText(text);
            view.setTag(TAG, name);
            return view;
        }
        return null;
    }

    public ViewGroup.LayoutParams layoutParams(final View view, final ViewGroup parent) {
        final String name = (String) view.getTag(TAG);
        switch (name) {
            case "html":
                return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
                return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            case "p":
                return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            default:
                return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private static Context withTheme(Context origin, String name) {
        @StyleRes int style;
        switch (name) {
            case "h1":
                style = R.style.HeadingView_H1;
                break;
            case "h2":
                style = R.style.HeadingView_H2;
                break;
            case "h3":
                style = R.style.HeadingView_H3;
                break;
            case "h4":
                style = R.style.HeadingView_H4;
                break;
            case "h5":
                style = R.style.HeadingView_H5;
                break;
            case "h6":
                style = R.style.HeadingView_H6;
                break;
            case "li":
                style = R.style.ItemLists;
                break;
            case "html":
                style = R.style.HtmlBody;
                break;
            default:
                style = -1;
        }
        return style != -1 ? new ContextThemeWrapper(origin, style) : origin;
    }
}
