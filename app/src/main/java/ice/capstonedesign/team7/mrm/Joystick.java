package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Joystick extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Setting";
    String sql;
    int dbCount;
    Cursor resultset;

    WebView web, web_w;

    LinearLayout j_plate, j_plate2, cam_screen_off;

    TextView n_date, n_time, j_shutter, j_back, t_direction;
    TextView j_temp, j_humi;

    String db_main_adr, db_port, url, url_b;
    String now_date, now_time;

    String temp_d[];

    int db_cam_mode, db_data_save;
    int touch_loc, x, y;

    long now;

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.joystick);

        init();
        DBcheck();

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

        if (db_cam_mode == 1)
            Camera();

        web_w.setWebViewClient(new WebViewClient());
        web_w.getSettings().setDefaultTextEncodingName("UTF-8");
        web_w.getSettings().setJavaScriptEnabled(true);
        web_w.getSettings().setBuiltInZoomControls(true);
        web_w.getSettings().setSupportZoom(true);
        web_w.getSettings().setLoadWithOverviewMode(true);
        web_w.getSettings().setUseWideViewPort(true);
        web_w.setWebChromeClient(new WebChromeClient());
        web_w.getSettings().setUserAgentString("Android WebView");
        web_w.getSettings().setDomStorageEnabled(true);
        web_w.addJavascriptInterface(new MyJavaScriptInterface(), "JS");
        web_w.loadUrl(url_b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motion) {
        int touch_count = motion.getPointerCount();

        if (touch_count > 1)
            touch_count = 1;

        switch (motion.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touch_loc = motion.getPointerId(0);
                x = (int)motion.getX(0);
                y = (int)motion.getY(0);
                DirectionCheck(x, y);

                break;
            case MotionEvent.ACTION_MOVE:
                for (int i=0; i<touch_count; i++) {
                    touch_loc = motion.getPointerId(i);
                    x = (int)motion.getX(i);
                    y = (int)motion.getY(i);
                    DirectionCheck(x, y);
                    image.setX(x - 80);
                    image.setY(y - 80);
                }

                break;
            case MotionEvent.ACTION_UP:
                image.setX(227);
                image.setY(382);
                DirectionCheck(x, y);
                web_w.loadUrl("javascript:Stop()");
                t_direction.setText("정지");

                break;
        }

        return super.onTouchEvent(motion);
    }

    public void init() {
        n_date = (TextView)findViewById(R.id.now_date);
        n_time = (TextView)findViewById(R.id.now_time);
        j_back = (TextView)findViewById(R.id.button_back);
        t_direction = (TextView)findViewById(R.id.j_direction);
        j_shutter = (TextView)findViewById(R.id.joystick_shutter);
        j_temp = (TextView)findViewById(R.id.j_temp);
        j_humi = (TextView)findViewById(R.id.j_humi);

        web = (WebView)findViewById(R.id.camera_screen2);
        web_w = (WebView)findViewById(R.id.webiopi_webview2);

        j_plate = (LinearLayout)findViewById(R.id.joystick_plate);
        j_plate2 = (LinearLayout)findViewById(R.id.joystick_plate2);
        cam_screen_off = (LinearLayout)findViewById(R.id.camera_screen_off2);

        image = (ImageView)findViewById(R.id.button);

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
            case R.id.joystick_shutter:
                Toast.makeText(getApplicationContext(), "강제 정지", Toast.LENGTH_SHORT).show();
                break;
            case R.id.joystick_plate:
                j_plate.setVisibility(View.GONE);
                j_plate2.setVisibility(View.VISIBLE);
                break;
            case R.id.joystick_plate2:
                j_plate.setVisibility(View.VISIBLE);
                j_plate2.setVisibility(View.GONE);
                break;
            case R.id.j_ccw:
                web_w.loadUrl("javascript:RRotate()");
                t_direction.setText("360도 역회전");
                break;
            case R.id.j_cw:
                web_w.loadUrl("javascript:Rotate()");
                t_direction.setText("360도 회전");
                break;
            case R.id.j_info_save:
                web_w.loadUrl("javascript:Sensor()");
                Toast.makeText(getApplicationContext(), "온/습도 데이터를 불러오는 중입니다", Toast.LENGTH_SHORT).show();
                break;
            case R.id.joystick_back:
                finish();
                break;
        }
    }

    protected void DirectionCheck(int a, int b) {
        if (a < 127) x = 127;
        else if (a > 470) x = 470;

        if (b < 288) y = 288;
        else if (b > 628) y = 628;

        if ((a >= 127 && a <= 227) && (b >= 528 && b <= 628)) {
            t_direction.setText("왼쪽 뒤 대각선 이동");
            web_w.loadUrl("javascript:BLeft()");
        }
        else if ((a >= 257 && a <= 337) && (b >= 528 && b <= 628)) {
            t_direction.setText("뒤로 이동");
            web_w.loadUrl("javascript:Backward()");
        }
        else if ((a >= 368 && a <= 470) && (b >= 528 && b <= 628)) {
            t_direction.setText("오른쪽 뒤 대각선 이동");
            web_w.loadUrl("javascript:BRight()");
        }
        else if ((a >= 368 && a <= 470) && (b >= 418 && b <= 498)) {
            t_direction.setText("오른쪽 이동");
            web_w.loadUrl("javascript:Right()");
        }
        else if ((a >= 368 && a <= 470) && (b >= 288 && b <= 388)) {
            t_direction.setText("오른쪽 앞 대각선 이동");
            web_w.loadUrl("javascript:FRight()");
        }
        else if ((a >= 257 && a <= 337) && (b >= 288 && b <= 388)) {
            t_direction.setText("앞으로 이동");
            web_w.loadUrl("javascript:Forward()");
        }
        else if ((a >= 127 && a <= 227) && (b >= 288 && b <= 388)) {
            t_direction.setText("왼쪽 앞 대각선 이동");
            web_w.loadUrl("javascript:FLeft()");
        }
        else if ((a >= 127 && a <= 227) && (b >= 418 && b <= 498)) {
            t_direction.setText("왼쪽 이동");
            web_w.loadUrl("javascript:Left()");
        }
        else if ((a >= 227 && a <= 368) && (b >= 388 && b <= 528)) {
            t_direction.setText("정지");
            web_w.loadUrl("javascript:Stop()");
        }
    }

    public void DBcheck() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "SELECT main_adr, port, cam_mode, data_save FROM " + tableName + ";";
            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

            for (int i=0; i<dbCount; i++) {
                resultset.moveToNext();

                String t_main_adr = resultset.getString(0);
                String t_port = resultset.getString(1);
                int t_cam_mode = resultset.getInt(2);
                int t_data_save = resultset.getInt(3);

                db_main_adr = t_main_adr;
                db_port = t_port;
                db_cam_mode = t_cam_mode;
                db_data_save = t_data_save;

                int temp_port = Integer.parseInt(t_port) + 1;
                url = "http://" + db_main_adr + ":" + String.valueOf(temp_port) + "/?action=stream";
                url_b = "http://" + db_main_adr + ":" + String.valueOf(temp_port-1) + "/app/MRM";
            }

            if (db_cam_mode == 1) {
                web.setVisibility(View.VISIBLE);
                cam_screen_off.setVisibility(View.GONE);
            } else if (db_cam_mode == 2) {
                cam_screen_off.setVisibility(View.VISIBLE);
                web.setVisibility(View.GONE);
            }

            db.close();
        } catch (Exception e) {
            e.getStackTrace();
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
        web.loadUrl(url);
    }

    final class MyJavaScriptInterface {
        MyJavaScriptInterface() {

        }

        @JavascriptInterface
        public void callAndroid(final String str) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    temp_d = str.split(",");

                    float temp_t = Float.parseFloat(temp_d[0]);
                    float temp_h = Float.parseFloat(temp_d[1]);

                    temp_d[0] = String.format("%.2f", temp_t);
                    temp_d[1] = String.format("%.2f", temp_h);

                    j_temp.setText(temp_d[0] + " ℃");
                    j_humi.setText(temp_d[1] + " %");
                }
            });
        }
    }
}
