package ice.capstonedesign.team7.mrm;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class Analysis extends Activity {
    SQLiteDatabase db;
    String dbName = "OMNI";
    String tableName = "Data";
    String sql;
    Cursor resultset;
    int dbCount;

    String date_s[] = {};
    String temperature[] = {};
    String humidity[] = {};

    String seData, start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.data_analysis);

        seData = getIntent().getStringExtra("Data");
        start = seData.split("/")[0];
        end = seData.split("/")[1];

        DBcheck();
        ChartMake_t();
        ChartMake_h();
    }

    public void ChartMake_t() {
        LineChart chart = (LineChart)findViewById(R.id.chart);

        ArrayList<Entry> data = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();

        if (dbCount > 0) {
            for (int i=0; i<dbCount; i++) {
                data.add(new Entry(Float.parseFloat(temperature[i]), i));
                date.add(date_s[i]);
            }
        }

        LineDataSet dataset = new LineDataSet(data, "# 온도 (℃)");

        LineData ld = new LineData(date, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setDrawCircles(true);
        dataset.setDrawCubic(true);

        chart.setData(ld);
        chart.animateY(1000);
    }

    public void ChartMake_h() {
        LineChart chart2 = (LineChart)findViewById(R.id.chart2);

        ArrayList<Entry> data = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();

        if (dbCount > 0) {
            for (int i=0; i<dbCount; i++) {
                data.add(new Entry(Float.parseFloat(humidity[i]), i));
                date.add(date_s[i]);
            }
        }

        LineDataSet dataset = new LineDataSet(data, "# 습도 (%)");

        LineData ld = new LineData(date, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setDrawCircles(true);

        chart2.setData(ld);
        chart2.animateY(1000);
    }

    public void DBcheck() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            sql = "SELECT date, tem, humi FROM " + tableName + ";";

            if (start.equals("-") == false && end.equals("-") == false) {
                if (start.equals(end) == true)
                    sql = "SELECT date, tem, humi FROM " + tableName + " WHERE date LIKE '" + start + "%';";
                else
                    sql = "SELECT date, tem, humi FROM " + tableName + " WHERE date LIKE '" + start + "%' BETWEEN '" + end + "%';";
            }

            resultset = db.rawQuery(sql, null);
            dbCount = resultset.getCount();

            date_s = new String[dbCount];
            temperature = new String[dbCount];
            humidity = new String[dbCount];

            for (int i=0; i<dbCount; i++) {
                resultset.moveToNext();

                String t_date = resultset.getString(0);
                String t_tem = resultset.getString(1);
                String t_humi = resultset.getString(2);

                date_s[i] = t_date.substring(6);
                temperature[i] = t_tem;
                humidity[i] = t_humi;
            }

            db.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
