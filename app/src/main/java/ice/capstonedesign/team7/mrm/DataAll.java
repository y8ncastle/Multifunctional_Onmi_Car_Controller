package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    CustomList customList;

    ListView list;

    String date[] = {"2019-04-18 16:40:00", "2019-04-19 16:41:00"};
    String temperature[] = {"10", "20"};
    String humidity[] = {"50", "60"};
    String dust[] = {"27", "12"};
    String location[] = {"411.555˚243.555'121.232N 223.123˚105.283'264.532E", "31˚25'13.2S 1˚11'26N"};
    String obstacle[] = {"O", "X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_all);

        customList = new CustomList(DataAll.this);
        list = (ListView)findViewById(R.id.listview2);
        list.setAdapter(customList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Dialog dialog = new Dialog(DataAll.this);

                dialog.setContentView(R.layout.dialog_data_check);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();

                TextView data_check_modify = (TextView)dialog.findViewById(R.id.data_check_modify);
                TextView data_check_delete = (TextView)dialog.findViewById(R.id.data_check_delete);
                TextView data_check_cancel = (TextView)dialog.findViewById(R.id.data_check_cancel);

                TextView temp_date = (TextView)dialog.findViewById(R.id.data_check_name);
                temp_date.setText("'" + date[i] + "' 에 기록된 정보");

                EditText temp_temperature = (EditText)dialog.findViewById(R.id.data_check_temperature);
                temp_temperature.setText(temperature[i] + "℃");

                EditText temp_humidity = (EditText)dialog.findViewById(R.id.data_check_humidity);
                temp_humidity.setText(humidity[i] + "%");

                EditText temp_dust = (EditText)dialog.findViewById(R.id.data_check_dust);
                temp_dust.setText(dust[i] + "㎍/m³");

                EditText temp_location = (EditText)dialog.findViewById(R.id.data_check_location);
                temp_location.setText(location[i]);

                EditText temp_obstacle = (EditText)dialog.findViewById(R.id.data_check_obstacle);
                temp_obstacle.setText(obstacle[i]);

                data_check_modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "수정 기능 테스트", Toast.LENGTH_SHORT).show();
                    }
                });

                data_check_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "삭제 기능 테스트", Toast.LENGTH_SHORT).show();
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

            TextView temp_number = (TextView)result.findViewById(R.id.list_number);
            String temp_num = String.valueOf(pos + 1);
            temp_number.setText(temp_num);

            TextView temp_date = (TextView)result.findViewById(R.id.list_date);
            temp_date.setText(date[pos]);

            TextView temp_temperature = (TextView)result.findViewById(R.id.list_temperature);
            temp_temperature.setText(temperature[pos] + "℃");

            TextView temp_humidity = (TextView)result.findViewById(R.id.list_humidity);
            temp_humidity.setText(humidity[pos] + "%");

            TextView temp_dust = (TextView)result.findViewById(R.id.list_dust);
            temp_dust.setText(dust[pos] + "㎍/m³");

            TextView temp_location = (TextView)result.findViewById(R.id.list_location);
            temp_location.setText(location[pos]);

            TextView temp_obstacle = (TextView)result.findViewById(R.id.list_obstacle);
            temp_obstacle.setText(obstacle[pos]);

            return result;
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
