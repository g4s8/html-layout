package wtf.g4s8.htmllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;

public class HtmlInflater extends LayoutInflater {

    private static final Field fConstructorArgs;
    //    private static final Method rInflate;
    private static final Method createViewFromTag;
    private static final Method mParseInclude;

    static {
        try {
            fConstructorArgs = LayoutInflater.class.getDeclaredField("mConstructorArgs");
            createViewFromTag = LayoutInflater.class.getDeclaredMethod("createViewFromTag",
                    View.class, String.class, Context.class, AttributeSet.class);
            mParseInclude = LayoutInflater.class.getDeclaredMethod("parseInclude", XmlPullParser.class, Context.class, View.class, AttributeSet.class);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        fConstructorArgs.setAccessible(true);
        createViewFromTag.setAccessible(true);
        mParseInclude.setAccessible(true);
    }

    public HtmlInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new HtmlInflater(this, newContext);
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        final Object[] mConstructorArgs;
        try {
            mConstructorArgs = (Object[]) fConstructorArgs.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        synchronized (mConstructorArgs) {

            final Context inflaterContext = getContext();
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            Context lastContext = (Context) mConstructorArgs[0];
            mConstructorArgs[0] = inflaterContext;
            View result = root;

            try {
                // Look for the root node.
                int type;
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }

                if (type != XmlPullParser.START_TAG) {
                    throw new InflateException(parser.getPositionDescription()
                            + ": No start tag found!");
                }

                final String name = parser.getName();


                if ("merge".equals(name)) {
                    if (root == null || !attachToRoot) {
                        throw new InflateException("<merge /> can be used only with a valid "
                                + "ViewGroup root and attachToRoot=true");
                    }

                    rInflate(parser, root, inflaterContext, attrs, false);
                } else {
                    // Temp is the root view that was found in the xml
                    final View temp = (View) createViewFromTag.invoke(this, root, name, inflaterContext, attrs);

                    ViewGroup.LayoutParams params = null;

                    if (root != null) {
                        // Create layout params that match root, if supplied
                        params = root.generateLayoutParams(attrs);
                        if (!attachToRoot) {
                            // Set the layout params for temp if we are not
                            // attaching. (If we are, we use addView, below)
                            temp.setLayoutParams(params);
                        }
                    }


                    // Inflate all children under temp against its context.
                    rInflateChildren(parser, temp, attrs, true);


                    // We are supposed to attach all the views we found (int temp)
                    // to root. Do that now.
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }

                    // Decide whether to return the root that was passed in or the
                    // top view found in xml.
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
                }

            } catch (XmlPullParserException e) {
                final InflateException ie = new InflateException(e.getMessage(), e);
                throw ie;
            } catch (Exception e) {
                final InflateException ie = new InflateException(parser.getPositionDescription()
                        + ": " + e.getMessage(), e);
                throw ie;
            } finally {
                // Don't retain static reference on context.
                mConstructorArgs[0] = lastContext;
                mConstructorArgs[1] = null;
            }

            return result;
        }
    }

    private HtmlTags htmlTags = new HtmlTags();

    void rInflate(XmlPullParser parser, View parent, Context context,
                  AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {

        final int depth = parser.getDepth();
        int type;
        boolean pendingRequestFocus = false;

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            final View html = htmlTags.inflate(parser, parent, context, attrs);
            if (html != null) {
                final ViewGroup viewGroup = (ViewGroup) parent;
                if (html instanceof ViewGroup) {
                    rInflateChildren(parser, html, attrs, true);
                }
                viewGroup.addView(html, htmlTags.layoutParams(html, viewGroup));
            } else if ("requestFocus".equals(name)) {
                pendingRequestFocus = true;
                consumeChildElements(parser);
            } else if ("tag".equals(name)) {
                parseViewTag(parser, parent, attrs);
            } else if ("include".equals(name)) {
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, context, parent, attrs);
            } else if ("merge".equals(name)) {
                throw new InflateException("<merge /> must be the root element");
            } else {
                final View view;
                try {
                    view = (View) createViewFromTag.invoke(this, parent, name, context, attrs);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                rInflateChildren(parser, view, attrs, true);
                viewGroup.addView(view, params);
            }
        }

        // TODO: uncomment it
        if (pendingRequestFocus) {
//            parent.restoreDefaultFocus();
        }

        if (finishInflate) {
            try {
                final Method mOnFinishInflate = View.class.getDeclaredMethod("onFinishInflate");
                mOnFinishInflate.setAccessible(true);
                mOnFinishInflate.invoke(parent);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void parseInclude(XmlPullParser parser, Context context, View parent,
                              AttributeSet attrs) throws XmlPullParserException, IOException {
        try {
            mParseInclude.invoke(this, parser, context, parent, attrs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private void parseViewTag(XmlPullParser parser, View view, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        throw new IllegalStateException("Not implemented");
    }

    private void rInflateChildren(XmlPullParser parser, View parent, AttributeSet attrs,
                                  boolean finishInflate) throws XmlPullParserException, IOException {
        rInflate(parser, parent, parent.getContext(), attrs, finishInflate);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        try {
            Class.forName("android.widget." + name);
            return createView(name, "android.widget.", attrs);
        } catch (ClassNotFoundException e) {
            return createView(name, "android.view.", attrs);
        }
    }

    private static void consumeChildElements(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int type;
        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }
}
