package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Button extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Setting";
    String sql;
    int dbCount;
    Cursor resultset;

    LinearLayout p_button, p_button2, cam_screen_off;

    WebView web, web_w;

    TextView n_date, n_time, b_shutter, b_back, b_info_save, t_direction;

    ImageView b_forward, b_left, b_right, b_rear, b_ccw, b_cw;

    String now_date, now_time, temp_direction;
    String db_main_adr, db_port;
    String url = "-";
    String url_b = "-";
    int db_cam_mode, db_data_save, db_s_term, db_term_unit;

    long now;

    int touch_loc[] = new int[2];
    int x[] = new int[2];
    int y[] = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.button);

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
        web_w.loadUrl(url_b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motion) {
        int touch_count = motion.getPointerCount();

        temp_direction = "";

        if (touch_count > 2)
            touch_count = 2;

        switch (motion.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touch_loc[0] = motion.getPointerId(0);
                x[0] = (int)motion.getX(0);
                y[0] = (int)motion.getY(0);

                DirectionCheck(x, y, touch_count);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i=0; i<touch_count; i++) {
                    touch_loc[i] = motion.getPointerId(i);
                    x[i] = (int)motion.getX(i);
                    y[i] = (int)motion.getY(i);
                }

                DirectionCheck(x, y, touch_count);

                break;
            case MotionEvent.ACTION_MOVE:
                for (int i=0; i<touch_count; i++) {
                    touch_loc[i] = motion.getPointerId(i);
                    x[i] = (int)motion.getX(i);
                    y[i] = (int)motion.getY(i);
                }

                DirectionCheck(x, y, touch_count);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                touch_loc[0] = motion.getPointerId(0);
                x[0] = (int)motion.getX(0);
                y[0] = (int)motion.getY(0);

                DirectionCheck(x, y, touch_count);

                break;
            case MotionEvent.ACTION_UP:
                t_direction.setText("정지");
                web_w.loadUrl("javascript:Stop()");

                break;
        }

        return super.onTouchEvent(motion);
    }

    protected void DirectionCheck(int i[], int j[], int count) {
        // 왼쪽   X 80,260 / Y 190,390
        // 오른쪽 X 80,260 / Y 660,850
        // 앞쪽 X 1820,2020 / Y 190,390
        // 뒤쪽 X 1820,2020 / Y 660,850
        // 시계 반대방향 X 105,255 / Y 440,590
        // 시계 방향 X 1840,2000 / Y 440,590

        if (count == 2) {
            if (((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 80 && i[1] < 260) && (j[1] > 660 && j[1] < 850))) {
                t_direction.setText("정지"); // 왼, 오른쪽 동시 입력 방지 케이스 1
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850))) {
                t_direction.setText("정지"); // 왼, 오른쪽 동시 입력 방지 케이스 2
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 1820 && i[1] < 2020) && (j[1] > 660 && j[1] < 850))) {
                t_direction.setText("정지"); // 앞, 뒤쪽 동시 입력 방지 케이스 1
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[1] > 1820 && i[1] < 2020) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850))) {
                t_direction.setText("정지"); // 앞, 뒤쪽 동시 입력 방지 케이스 2
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590)) && ((i[1] > 1840 && i[1] < 2000) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("정지"); // 360도 회전 동시 입력 방지 케이스 1
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[1] > 105 && i[1] < 255) && (j[1] > 440 && j[1] < 590)) && ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("정지"); // 360도 회전 동시 입력 방지 케이스 2
                web_w.loadUrl("javascript:Stop()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 1820 && i[1] < 2020) && (j[1] > 190 && j[1] < 390))) {
                t_direction.setText("왼쪽 앞 대각선 이동");
                web_w.loadUrl("javascript:FLeft()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390))) {
                t_direction.setText("왼쪽 앞 대각선 이동");
                web_w.loadUrl("javascript:FLeft()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 1820 && i[1] < 2020) && (j[1] > 190 && j[1] < 390))) {
                t_direction.setText("오른쪽 앞 대각선 이동");
                web_w.loadUrl("javascript:FRight()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390))) {
                t_direction.setText("오른쪽 앞 대각선 이동");
                web_w.loadUrl("javascript:FRight()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 1820 && i[1] < 2020) && (j[1] > 660 && j[1] < 850))) {
                t_direction.setText("왼쪽 뒤 대각선 이동");
                web_w.loadUrl("javascript:BLeft()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850))) {
                t_direction.setText("왼쪽 뒤 대각선 이동");
                web_w.loadUrl("javascript:BLeft()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 1820 && i[1] < 2020) && (j[1] > 660 && j[1] < 850))) {
                t_direction.setText("오른쪽 뒤 대각선 이동");
                web_w.loadUrl("javascript:BRight()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850))) {
                t_direction.setText("오른쪽 뒤 대각선 이동");
                web_w.loadUrl("javascript:BRight()");
            }
            else if ((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390) && ((i[1] > 105 && i[1] < 255) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("앞으로 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if ((i[1] > 1820 && i[1] < 2020) && (j[1] > 190 && j[1] < 390) && ((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("앞으로 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 1840 && i[1] < 2000) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("앞으로 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[1] > 1820 && i[1] < 2020) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("앞으로 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 105 && i[1] < 255) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("뒤로 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[1] > 1820 && i[1] < 2020) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("뒤로 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 1840 && i[1] < 2000) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("뒤로 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[1] > 1820 && i[1] < 2020) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("뒤로 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 105 && i[1] < 255) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("왼쪽 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("왼쪽 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) && ((i[1] > 1840 && i[1] < 2000) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("왼쪽 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 190 && j[1] < 390)) && ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("왼쪽 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 105 && i[1] < 255) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("오른쪽 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("오른쪽 이동 중 360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if (((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850)) && ((i[1] > 1840 && i[1] < 2000) && (j[1] > 440 && j[1] < 590))) {
                t_direction.setText("오른쪽 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
            else if (((i[1] > 80 && i[1] < 260) && (j[1] > 660 && j[1] < 850)) && ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590))) {
                t_direction.setText("오른쪽 이동 중 360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
        } else if (count == 1) {
            if ((i[0] > 80 && i[0] < 260) && (j[0] > 190 && j[0] < 390)) {
                t_direction.setText("왼쪽 이동");
                web_w.loadUrl("javascript:Left()");
            }
            else if ((i[0] > 80 && i[0] < 260) && (j[0] > 660 && j[0] < 850)) {
                t_direction.setText("오른쪽 이동");
                web_w.loadUrl("javascript:Right()");
            }
            else if ((i[0] > 1820 && i[0] < 2020) && (j[0] > 190 && j[0] < 390)) {
                t_direction.setText("앞으로 이동");
                web_w.loadUrl("javascript:Forward()");
            }
            else if ((i[0] > 1820 && i[0] < 2020) && (j[0] > 660 && j[0] < 850)) {
                t_direction.setText("뒤로 이동");
                web_w.loadUrl("javascript:Backward()");
            }
            else if ((i[0] > 105 && i[0] < 255) && (j[0] > 440 && j[0] < 590)) {
                t_direction.setText("360도 역회전");
                web_w.loadUrl("javascript:RRotate()");
            }
            else if ((i[0] > 1840 && i[0] < 2000) && (j[0] > 440 && j[0] < 590)) {
                t_direction.setText("360도 회전");
                web_w.loadUrl("javascript:Rotate()");
            }
        }
    }

    private void TimeSetting() {
        now = System.currentTimeMillis();

        Date date = new Date (now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
        now_date = sdf.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("kk:mm:ss");
        now_time = sdf2.format(date);

        n_date.setText(now_date);
        n_time.setText(now_time);
    }

    protected void ButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_shutter:
                Toast.makeText(getApplicationContext(), "촬영 테스트", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_back:
                finish();
                break;
            case R.id.button_plate:
                p_button.setVisibility(View.GONE);
                p_button2.setVisibility(View.VISIBLE);
                break;
            case R.id.button_plate2:
                p_button.setVisibility(View.VISIBLE);
                p_button2.setVisibility(View.GONE);
                break;
            case R.id.info_save:
                web_w.loadUrl("javascript:Sensor()");
                Toast.makeText(getApplicationContext(), "저장 테스트", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void init() {
        p_button = (LinearLayout)findViewById(R.id.button_plate);
        p_button2 = (LinearLayout)findViewById(R.id.button_plate2);
        cam_screen_off = (LinearLayout)findViewById(R.id.camera_screen_off);

        n_date = (TextView)findViewById(R.id.now_date);
        n_time = (TextView)findViewById(R.id.now_time);
        b_shutter = (TextView)findViewById(R.id.button_shutter);
        b_back = (TextView)findViewById(R.id.button_back);
        b_info_save = (TextView)findViewById(R.id.info_save);
        t_direction = (TextView)findViewById(R.id.direction);

        b_forward = (ImageView)findViewById(R.id.button_forward);
        b_left = (ImageView)findViewById(R.id.button_left);
        b_right = (ImageView)findViewById(R.id.button_right);
        b_rear = (ImageView)findViewById(R.id.button_rear);
        b_ccw = (ImageView)findViewById(R.id.button_ccw);
        b_cw = (ImageView)findViewById(R.id.button_cw);

        web = (WebView)findViewById(R.id.camera_screen);
        web_w = (WebView)findViewById(R.id.webiopi_webview);

        now = System.currentTimeMillis();

        Date date = new Date (now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
        now_date = sdf.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("kk:mm:ss");
        now_time = sdf2.format(date);

        n_date.setText(now_date);
        n_time.setText(now_time);
    }

    public void DBcheck() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "SELECT main_adr, port, cam_mode, data_save, s_term, term_unit FROM " + tableName + ";";
            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

            for (int i=0; i<dbCount; i++) {
                resultset.moveToNext();

                String t_main_adr = resultset.getString(0);
                String t_port = resultset.getString(1);
                int t_cam_mode = resultset.getInt(2);
                int t_data_save = resultset.getInt(3);
                int t_s_term = resultset.getInt(4);
                int t_term_unit = resultset.getInt(5);

                db_main_adr = t_main_adr;
                db_port = t_port;
                db_cam_mode = t_cam_mode;
                db_data_save = t_data_save;
                db_s_term = t_s_term;
                db_term_unit = t_term_unit;

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

    public void Camera() {
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setDefaultTextEncodingName("UTF-8");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.setWebChromeClient(new WebChromeClient());
        web.getSettings().setUserAgentString("Android WebView");
        web.getSettings().setDomStorageEnabled(true);
        web.loadUrl(url);
    }
}
