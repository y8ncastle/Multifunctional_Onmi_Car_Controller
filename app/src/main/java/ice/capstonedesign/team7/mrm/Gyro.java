package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gyro extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Setting";
    String sql;
    int dbCount;
    Cursor resultset;

    private SensorManager sm = null; // 가속도계, 자이로스코프
    private SensorEventListener sel;
    private Sensor sensor = null; // 자이로스코프 사용 (위 포함)
    private double x; // Roll (가로화면 기준 왼쪽, 오른쪽 / 단위 : r)
    private double y; // Pitch (가로화면 기준 위쪽, 아래쪽 / 단위 : r)
    private double z; // Yaw (화면 관통 기울기)
    private double time;
    private double dt; // 센서가 상태를 감지하는 시간 간격

    LinearLayout cam_screen_off;

    WebView web, web_w;

    TextView n_date, n_time, b_shutter, b_back, b_info_save;
    TextView gyro_print;

    String now_date, now_time;
    String url = "-", url_b = "-";
    String db_main_adr, db_port;
    int db_cam_mode, db_data_save, db_s_term, db_term_unit;

    long now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.gyro);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("자이로스코프 센서 작동 방식 사용 안내");
        builder.setMessage("조작 버튼을 누른 상태에서 스마트폰을 상, 하, 좌, 우로 기울이세요.\n" +
                "특정 방향으로 45~90도 정도로 돌리면 움직이며, 조작 버튼에서 손을 뗄 경우 정지합니다.\n" +
                "(360도 회전은 스마트폰 화면을 위로한 상태에서 시계, 반시계 방향으로 돌리면 됩니다.)");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

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

        sm = (SensorManager)getSystemService(getApplication().SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sel = new GyroscopeListener();

        findViewById(R.id.button_shutter2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = y = z = 0;
                        sm.registerListener(sel, sensor, sm.SENSOR_DELAY_UI);
                        break;
                    case MotionEvent.ACTION_UP:
                        sm.unregisterListener(sel);
                        x = y = z = 0;
                        gyro_print.setText("회전축 값\n출력화면");
                        break;
                }

                return false;
            }
        });

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
    public void onPause() {
        super.onPause();
        sm.unregisterListener(sel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(sel);
    }

    private class GyroscopeListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent se) {
            double p_x = se.values[0];
            double p_y = se.values[1];
            double p_z = se.values[2]; // 축의 각속도 측정

            dt = (se.timestamp - time) * (1.0f / 1000000000.0f); // 회전각 계산 (각속도 적분)
            time = se.timestamp;

            if (dt - time * (1.0f / 1000000000.0f) != 0) {
                x += p_x + dt;
                y += p_y + dt;
                z += p_z + dt;

                gyro_print.setText("[기준회전축X]\n" + String.format("%.1f", Math.toDegrees(x)) + "\n\n[기준회전축Y]\n" + String.format("%.1f", Math.toDegrees(y)) +
                        "\n\n[기준회전축Z]\n" + String.format("%.1f", Math.toDegrees(z)));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {

        }
    }

    public void init() {
        cam_screen_off = (LinearLayout)findViewById(R.id.camera_screen_off3);

        n_date = (TextView)findViewById(R.id.now_date);
        n_time = (TextView)findViewById(R.id.now_time);
        b_shutter = (TextView)findViewById(R.id.button_shutter2);
        b_back = (TextView)findViewById(R.id.button_back2);
        b_info_save = (TextView)findViewById(R.id.info_save2);
        gyro_print = (TextView)findViewById(R.id.gyro_print);

        web = (WebView)findViewById(R.id.camera_screen3);
        web_w = (WebView)findViewById(R.id.webiopi_webview3);

        now = System.currentTimeMillis();

        Date date = new Date (now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
        now_date = sdf.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("kk:mm:ss");
        now_time = sdf2.format(date);

        n_date.setText(now_date);
        n_time.setText(now_time);
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

    protected void ButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.info_save2:
                Toast.makeText(getApplicationContext(), "저장 테스트", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_back2:
                finish();
                break;
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