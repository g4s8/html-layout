# Usage

Override `LayoutInflater` to enabled HTML tags in your layout (see `./example` project):
```java
import wtf.g4s8.htmllayout.HtmlInflater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (name.equals(LAYOUT_INFLATER_SERVICE)) {
            return new HtmlInflater((LayoutInflater) super.getSystemService(name), getApplicationContext());
        }
        return super.getSystemService(name);
    }
}
```

That's all - now you can push HTML tags to XML layout:
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <html>
        <h1>Html tags example</h1>
        <p>This is example of HTML tags right in Android layout XML file.</p>
        <h3>Unordered list</h3>
        <ul>
            <li>One</li>
            <li>Two</li>
            <li>Three</li>
        </ul>
    </html>
</FrameLayout>
```
and get it rendered:
![screenshot](/.pic/example.png)

Supported tags:
 - `html` - root tag for HTML elements
 - `h1`, `h2`, `h3`, `h4`, `h5`, `h6` - all heading tags
 - `p` - paragraph tag
 - `ol`, `ul` - lists ordered and unordered
 - `li` - list item

TODO:
 - style customization via theme attributes


I'm not sure if this library can be useful, just wanted to abuse layout inflater service.