package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Data extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Data";
    String sql;
    Cursor resultset;
    int dbCount;

    CustomList customList;
    ListView list;

    LinearLayout b_data_back;

    ImageView b_data_search, b_data_all, b_data_analysis;

    TextView data_count;

    String date[] = {};
    String temperature[] = {};
    String humidity[] = {};
    String distance[] = {};
    String obstacle[] = {};

    String temp_date;
    String start_date = "-";
    String end_date = "-";

    private DatePickerDialog.OnDateSetListener date_p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_manage);

        init();
        DBcheck();

        customList = new CustomList(Data.this);
        list = (ListView)findViewById(R.id.listview);
        list.setAdapter(customList);
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.custom_list, date);
            this.context = context;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View result = inflater.inflate(R.layout.custom_list, null, true);

            TextView temp_data = (TextView)result.findViewById(R.id.data_date);
            temp_data.setText(date[pos]);

            TextView temp_info = (TextView)result.findViewById(R.id.data_info);
            temp_info.setText("온도 (" + temperature[pos] + "℃), 습도 (" + humidity[pos] + "%)");

            return result;
        }
    }

    public void init() {
        list = (ListView)findViewById(R.id.listview);

        b_data_back = (LinearLayout)findViewById(R.id.button_data_back);

        b_data_search = (ImageView)findViewById(R.id.button_data_search);
        b_data_all = (ImageView)findViewById(R.id.button_data_all);
        b_data_analysis = (ImageView)findViewById(R.id.button_data_analysis);

        data_count = (TextView)findViewById(R.id.data_text);
    }

    public void DBcheck() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "SELECT date, tem, humi, distance, obstacle FROM " + tableName + " ORDER BY date DESC;";
            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

            if (dbCount > 0)
                data_count.setText(String.valueOf(dbCount) + "  개");

            if (dbCount > 5)
                dbCount = 5;

            date = new String[dbCount];
            temperature = new String[dbCount];
            humidity = new String[dbCount];
            distance = new String[dbCount];
            obstacle = new String[dbCount];

            for (int i=0; i<dbCount; i++) {
                resultset.moveToNext();

                String t_date = resultset.getString(0);
                String t_tem = resultset.getString(1);
                String t_humi = resultset.getString(2);
                String t_distance = resultset.getString(3);
                String t_obstacle = resultset.getString(4);

                date[i] = t_date;
                temperature[i] = t_tem;
                humidity[i] = t_humi;
                distance[i] = t_distance;
                obstacle[i] = t_obstacle;
            }

            db.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    protected void DataButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_data_back:
                finish();
                break;
            case R.id.button_data_search:
                DateCheck();
                break;
            case R.id.button_data_all:
                Intent intent = new Intent(Data.this, DataAll.class);
                intent.putExtra("Data", start_date + "/" + end_date);
                startActivity(intent);
                break;
            case R.id.button_data_analysis:
                Intent intent2 = new Intent(Data.this, Analysis.class);
                intent2.putExtra("Data", start_date + "/" + end_date);
                startActivity(intent2);
                break;
        }
    }

    public void DatePicker() {
        Context context;
        DatePickerDialog dp;
        Calendar calendar = Calendar.getInstance();

        context = new ContextThemeWrapper(Data.this, android.R.style.Theme_DeviceDefault_Light);

        dp = new DatePickerDialog(context, dpListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    public DatePickerDialog.OnDateSetListener dpListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            temp_date = i + "년 " + (i1 + 1) + "월 " + i2 + "일";

            if (temp_date.equals("-") == false && start_date.equals("-") == true)
                start_date = temp_date;
            else if ((temp_date.equals("-") == false && end_date.equals("-") == true)) {
                end_date = temp_date;

                Toast.makeText(getApplicationContext(), "임시 날짜 범위가 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "날짜 범위가 잘못되었습니다", Toast.LENGTH_SHORT).show();
        }
    };

    public void DateCheck() {
        temp_date = "-";

        if (start_date.equals("-") == true) {
            Toast.makeText(getApplicationContext(), "시작 날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
            DatePicker();
        }
        else if (end_date.equals("-") == true) {
            Toast.makeText(getApplicationContext(), "종료 날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
            DatePicker();
        }
        else
            Toast.makeText(getApplicationContext(), "설정된 날짜 범위는\n" + start_date + "부터\n" + end_date + "까지 입니다", Toast.LENGTH_LONG).show();
    }
}