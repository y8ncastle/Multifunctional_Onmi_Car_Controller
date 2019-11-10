package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Main extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Setting";
    String sql;
    int dbCount;
    Cursor resultset;

    TextView ip_info, b_start, d_ipset, b_data, b_exit;

    String db_main_adr, db_port;
    int db_op_mode, db_cam_mode, db_data_save;

    private final long interval = 2000;
    private long backPressed;

    String url = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        init();
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        long time = now - backPressed;

        if (time >= 0 && interval >= time)
            super.onBackPressed();
        else {
            backPressed = now;

            Toast.makeText(getApplicationContext(),
                    "한번 더 뒤로가기를 누를 경우 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void init() {
        ip_info = (TextView)findViewById(R.id.ip_info);
        b_start = (TextView)findViewById(R.id.button_start);
        d_ipset = (TextView)findViewById(R.id.dialog_ipset);
        b_data = (TextView)findViewById(R.id.data_manage);
        b_exit = (TextView)findViewById(R.id.exit);

        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "main_adr VARCHAR2(15), " +
                    "port VARCHAR2(9), " +
                    "op_mode INTEGER, " +
                    "cam_mode INTEGER, " +
                    "data_save INTEGER);";
            db.execSQL(sql);

            sql = "SELECT main_adr, port, op_mode, cam_mode, data_save FROM " + tableName + ";";
            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

            if (dbCount != 0) {
                resultset.moveToNext();

                db_main_adr = resultset.getString(0);
                db_port = resultset.getString(1);
                db_op_mode = resultset.getInt(2);
                db_cam_mode = resultset.getInt(3);
                db_data_save = resultset.getInt(4);

                url = "http://" + db_main_adr + ":" + db_port;
            } else {
                sql = "INSERT INTO " + tableName + " VALUES (" +
                        "'-', '-', 1, 1, 1);";
                db.execSQL(sql);

                db_main_adr = "-";
                db_port = "-";
                db_op_mode = 1;
                db_cam_mode = 1;
                db_data_save = 1;
            }

            if (db_main_adr.equals("-") == false)
                ip_info.setText("설정한 주소  :  " + db_main_adr + "  (" + db_port + ")");

            db.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void DialogRegister() {
        final Dialog dialog = new Dialog(Main.this);

        dialog.setContentView(R.layout.dialog_ipset);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        TextView dialog_register = (TextView)dialog.findViewById(R.id.ip_dialog_register);
        TextView dialog_cancel = (TextView)dialog.findViewById(R.id.ip_dialog_cancel);

        final EditText ip_address = (EditText)dialog.findViewById(R.id.ip_address);
        final EditText port_number = (EditText)dialog.findViewById(R.id.port_number);

        if (db_main_adr.equals("-") == false) {
            ip_address.setText(db_main_adr);
            port_number.setText(db_port);
        }

        dialog_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp_main_adr = ip_address.getText().toString();
                String temp_port = port_number.getText().toString();

                if (temp_main_adr.length() == 0)
                    Toast.makeText(getApplicationContext(), "라즈베리파이 메인 IP 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                else if (temp_port.length() == 0)
                    Toast.makeText(getApplicationContext(), "포트 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                else {
                    ip_info.setText("설정한 주소  :  " + temp_main_adr + "  (" + temp_port + ")");

                    db_main_adr = temp_main_adr;
                    db_port = temp_port;

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET main_adr='" + temp_main_adr + "';";
                        db.execSQL(sql);

                        sql = "UPDATE " + tableName + " SET port='" + temp_port + "';";
                        db.execSQL(sql);

                        Toast.makeText(getApplicationContext(), "아이피 정보가 변경되었습니다", Toast.LENGTH_SHORT).show();

                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                    dialog.dismiss();
                }
            }
        });

        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void DialogSettings() {
        final Dialog dialog = new Dialog(Main.this);

        dialog.setContentView(R.layout.dialog_settings);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final RadioButton b_four_way = (RadioButton) dialog.findViewById(R.id.four_way_button);
        final RadioButton b_joystick = (RadioButton) dialog.findViewById(R.id.joystick_button);
        final RadioButton b_gyroscope = (RadioButton) dialog.findViewById(R.id.gyroscope_button);

        final ToggleButton b_camera = (ToggleButton) dialog.findViewById(R.id.camera_toggle_button);
        final ToggleButton b_data_save = (ToggleButton)dialog.findViewById(R.id.data_save_toggle_button);

        final TextView b_status = (TextView) dialog.findViewById(R.id.button_status);

        Toast.makeText(getApplicationContext(), "모든 설정은 클릭하는 순간 적용되며\n환경설정 창 밖을 클릭했을 때 창이 사라집니다", Toast.LENGTH_LONG).show();

        if (db_op_mode == 1) {
            b_four_way.setChecked(true);
            b_status.setText("버튼 클릭 방식");
        } else if (db_op_mode == 2) {
            b_joystick.setChecked(true);
            b_status.setText("조이스틱 방식");
        } else if (db_op_mode == 3) {
            b_gyroscope.setChecked(true);
            b_status.setText("자이로스코프 센서 이용 방식");
        }

        b_four_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_op_mode != 1) {
                    db_op_mode = 1;
                    b_four_way.setChecked(true);
                    b_status.setText("버튼 클릭 방식");
                    Toast.makeText(getApplicationContext(), "작동모드가 '버튼 클릭 방식'으로 변경되었습니다", Toast.LENGTH_SHORT).show();

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET op_mode=1;";
                        db.execSQL(sql);

                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        });

        b_joystick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_op_mode != 2) {
                    db_op_mode = 2;
                    b_joystick.setChecked(true);
                    b_status.setText("조이스틱 방식");
                    Toast.makeText(getApplicationContext(), "작동모드가 '조이스틱 방식'으로 변경되었습니다", Toast.LENGTH_SHORT).show();

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET op_mode=2;";
                        db.execSQL(sql);

                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        });

        b_gyroscope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_op_mode != 3) {
                    db_op_mode = 3;
                    b_gyroscope.setChecked(true);
                    b_status.setText("자이로스코프 센서 이용 방식");
                    Toast.makeText(getApplicationContext(), "작동모드가 '자이로스코프 센서 이용\n방식'으로 변경되었습니다", Toast.LENGTH_SHORT).show();

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET op_mode=3;";
                        db.execSQL(sql);

                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        });

        if (db_cam_mode == 1)
            b_camera.setChecked(true);
        else if (db_cam_mode == 2)
            b_camera.setChecked(false);

        b_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_cam_mode == 1) {
                    db_cam_mode = 2;
                    b_camera.setChecked(false);

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET cam_mode=2;";
                        db.execSQL(sql);

                        db.close();

                        Toast.makeText(getApplicationContext(), "카메라 기능을 껐습니다", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                } else if (db_cam_mode == 2) {
                    db_cam_mode = 1;
                    b_camera.setChecked(true);

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET cam_mode=1;";
                        db.execSQL(sql);

                        db.close();

                        Toast.makeText(getApplicationContext(), "카메라 기능을 켰습니다", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        });

        if (db_data_save == 1)
            b_data_save.setChecked(false);
        else if (db_data_save == 2)
            b_data_save.setChecked(true);

        b_data_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_data_save == 1) {
                    db_data_save = 2;

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET data_save=2;";
                        db.execSQL(sql);

                        Toast.makeText(getApplicationContext(), "데이터 자동 저장 기능을 켰습니다", Toast.LENGTH_SHORT).show();

                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
                else if (db_data_save == 2) {
                    db_data_save = 1;

                    db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try {
                        sql = "UPDATE " + tableName + " SET data_save=1;";
                        db.execSQL(sql);

                        Toast.makeText(getApplicationContext(), "데이터 자동 저장 기능을 껐습니다", Toast.LENGTH_SHORT).show();
                        db.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        });
    }

    protected void MainButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                if (db_op_mode == 1) {
                    Intent intent = new Intent(Main.this, Button.class);
                    startActivity(intent);
                } else if (db_op_mode == 2) {
                    Intent intent2 = new Intent(Main.this, Joystick.class);
                    startActivity(intent2);
                } else if (db_op_mode == 3) {
                    Intent intent3 = new Intent(Main.this, Gyro.class);
                    startActivity(intent3);
                }
                break;
            case R.id.dialog_ipset:
                DialogRegister();
                break;
            case R.id.data_manage:
                Intent intent2 = new Intent(Main.this, Data.class);
                startActivity(intent2);
                break;
            case R.id.dialog_settings:
                DialogSettings();
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}