package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Data extends Activity {
    CustomList customList;
    ListView list;

    LinearLayout b_data_back;

    ImageView b_data_search, b_data_all, b_data_analysis;

    String data[] = {"2019.04.17\n16:04:04",
            "2019.04.17\n16:04:36"};
    String set[] = {"온도, 습도, 미세먼지, 기울기, GPS",
            "온도, 습도, 미세먼지"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_manage);

        init();

        customList = new CustomList(Data.this);
        list = (ListView)findViewById(R.id.listview);
        list.setAdapter(customList);
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.custom_list, data);
            this.context = context;
        }

        @Override
        public View getView(final int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View result = inflater.inflate(R.layout.custom_list, null, true);

            TextView temp_data = (TextView)result.findViewById(R.id.data_date);
            TextView temp_set = (TextView)result.findViewById(R.id.data_set);
            temp_data.setText(data[pos]);
            temp_set.setText(set[pos]);

            LinearLayout temp_click = (LinearLayout)result.findViewById(R.id.data_click);

            temp_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "클릭 테스트", Toast.LENGTH_SHORT).show();
                }
            });

            return result;
        }
    }

    public void init() {
        list = (ListView)findViewById(R.id.listview);

        b_data_back = (LinearLayout)findViewById(R.id.button_data_back);

        b_data_search = (ImageView)findViewById(R.id.button_data_search);
        b_data_all = (ImageView)findViewById(R.id.button_data_all);
        b_data_analysis = (ImageView)findViewById(R.id.button_data_analysis);
    }

    protected void DataButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.button_data_back:
                finish();
                break;
            case R.id.button_data_search:
                Toast.makeText(getApplicationContext(), "데이터 검색 테스트", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_data_all:
                Intent intent = new Intent(Data.this, DataAll.class);
                startActivity(intent);
                break;
            case R.id.button_data_analysis:
                Intent intent2 = new Intent(Data.this, Analysis.class);
                startActivity(intent2);
                break;
        }
    }
}
