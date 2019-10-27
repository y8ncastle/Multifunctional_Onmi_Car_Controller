package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class Analysis extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_analysis);
    }
}
