package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DataAll extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Data";
    String sql;
    Cursor resultset;
    int dbCount;

    CustomList customList;

    ListView list;

    String date[] = {};
    String temperature[] = {};
    String humidity[] = {};
    String distance[] = {};
    String obstacle[] = {};

    String seData, start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_all);

        seData = getIntent().getStringExtra("Data");
        start = seData.split("/")[0];
        end = seData.split("/")[1];

        DBcheck();

        customList = new CustomList(DataAll.this);
        list = (ListView)findViewById(R.id.listview2);
        list.setAdapter(customList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Dialog dialog = new Dialog(DataAll.this);

                dialog.setContentView(R.layout.dialog_data_check);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();

                TextView data_check_delete = (TextView)dialog.findViewById(R.id.data_check_delete);
                TextView data_check_cancel = (TextView)dialog.findViewById(R.id.data_check_cancel);

                TextView temp_date = (TextView)dialog.findViewById(R.id.data_check_name);
                temp_date.setText("'" + date[i] + "' 에 기록");

                EditText temp_temperature = (EditText)dialog.findViewById(R.id.data_check_temperature);
                temp_temperature.setText(temperature[i]);

                EditText temp_humidity = (EditText)dialog.findViewById(R.id.data_check_humidity);
                temp_humidity.setText(humidity[i]);

                EditText temp_distance = (EditText)dialog.findViewById(R.id.data_check_distance);
                temp_distance.setText(distance[i]);

                EditText temp_obstacle = (EditText)dialog.findViewById(R.id.data_check_obstacle);
                temp_obstacle.setText(obstacle[i]);

                data_check_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                        try {
                            sql = "DELETE FROM " + tableName + " WHERE date='" + date[i] + "';";
                            db.execSQL(sql);

                            dialog.dismiss();
                            finish();
                            Intent intent = new Intent(DataAll.this, DataAll.class);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "데이터가 삭제되었습니다", Toast.LENGTH_SHORT).show();

                            db.close();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });

                data_check_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.custom_list2, date);
            this.context = context;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View result = inflater.inflate(R.layout.custom_list2, null, true);

            TextView temp_date = (TextView)result.findViewById(R.id.list_date);
            temp_date.setText(date[pos]);

            TextView temp_temperature = (TextView)result.findViewById(R.id.list_temperature);
            temp_temperature.setText(temperature[pos] + "℃");

            TextView temp_humidity = (TextView)result.findViewById(R.id.list_humidity);
            temp_humidity.setText(humidity[pos] + "%");

            TextView temp_dust = (TextView)result.findViewById(R.id.list_dust);
            temp_dust.setText(distance[pos]);

            TextView temp_obstacle = (TextView)result.findViewById(R.id.list_obstacle);
            temp_obstacle.setText(obstacle[pos]);

            return result;
        }
    }

    public void DBcheck() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "SELECT date, tem, humi, distance, obstacle FROM " + tableName + ";";

            if (start.equals("-") == false && end.equals("-") == false) {
                if (start.equals(end) == true)
                    sql = "SELECT date, tem, humi, distance, obstacle FROM " + tableName + " WHERE date LIKE '" + start + "%';";
                else
                    sql = "SELECT date, tem, humi, distance, obstacle FROM " + tableName + " WHERE date LIKE '" + start + "%' BETWEEN '" + end + "%';";
            }

            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

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

    public void DataAllButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_data_back:
                finish();
                break;
        }
    }
}
