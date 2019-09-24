package wtf.g4s8.htmllayout.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import wtf.g4s8.htmllayout.HtmlInflater;

import android.os.Bundle;
import android.view.LayoutInflater;

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
