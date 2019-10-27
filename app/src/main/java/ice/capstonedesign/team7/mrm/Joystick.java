package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Joystick extends Activity {
    WebView web;

    TextView n_date, n_time, b_back;

    String now_date, now_time;

    long now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.joystick);

        init();

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    while (isInterrupted() == false) {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TimeSetting();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        th.start();

        Camera();
    }

    public void init() {
        n_date = (TextView)findViewById(R.id.now_date);
        n_time = (TextView)findViewById(R.id.now_time);
        b_back = (TextView)findViewById(R.id.button_back);
        web = (WebView)findViewById(R.id.camera_screen_joystick);

        now = System.currentTimeMillis();

        Date date = new Date (now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
        now_date = sdf.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("    " + "kk:mm:ss");
        now_time = sdf2.format(date);

        n_date.setText(now_date);
        n_time.setText(now_time);
    }

    private void TimeSetting() {
        now = System.currentTimeMillis();

        Date date = new Date (now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
        now_date = sdf.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("    " + "kk:mm:ss");
        now_time = sdf2.format(date);

        n_date.setText(now_date);
        n_time.setText(now_time);
    }

    protected void ButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
        }
    }

    protected void Camera() {
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setDefaultTextEncodingName("UTF-8");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.setWebChromeClient(new WebChromeClient());
        web.getSettings().setUserAgentString("app");
        web.getSettings().setDomStorageEnabled(true);
        web.loadUrl("https://www.naver.com");
    }
}
