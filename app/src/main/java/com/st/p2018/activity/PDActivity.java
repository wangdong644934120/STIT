package com.st.p2018.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.ExpportDataBeExcel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PDActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private List<Integer> numbers = new ArrayList<>();
    private int number;
    private RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pd);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        Cache.getHCCS=0;
        initView();
    }

    private void initView(){



        rl=(RelativeLayout)findViewById(R.id.layoutpd);
        RelativeLayout.LayoutParams params ;
        //操作员图片

        params = new RelativeLayout.LayoutParams(100, 80);
        params.setMargins(10, 150, 0, 0);
        TextView tv1 = new TextView(this);
        tv1.setText("第一层");
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextColor(Color.BLACK);
        tv1.setTextSize(20);
        tv1.setLayoutParams(params);
        rl.addView(tv1);

        params = new RelativeLayout.LayoutParams(130, 80);
        params.setMargins(100, 150, 0, 0);
        TextView tv1y = new TextView(this);
        tv1y.setBackgroundColor(Color.RED);
        tv1y.setText("0");
        tv1y.setGravity(Gravity.CENTER);
        tv1y.setTextColor(Color.BLACK);
        tv1y.setTextSize(20);
        tv1y.setLayoutParams(params);
        rl.addView(tv1y);

        params = new RelativeLayout.LayoutParams(130, 80);
        params.setMargins(230, 150, 0, 0);
        TextView tv2y = new TextView(this);
        tv2y.setBackgroundColor(Color.YELLOW);
        tv2y.setText("0");
        tv2y.setTextColor(Color.BLACK);
        tv2y.setGravity(Gravity.CENTER);
        tv2y.setTextSize(20);
        tv2y.setLayoutParams(params);
        rl.addView(tv2y);

        params = new RelativeLayout.LayoutParams(130, 80);
        params.setMargins(360, 150, 0, 0);
        TextView tv3y = new TextView(this);
        tv3y.setBackgroundColor(Color.BLUE);
        tv3y.setGravity(Gravity.CENTER);
        tv3y.setText("0");
        tv3y.setTextColor(Color.BLACK);
        tv3y.setTextSize(20);
        tv3y.setLayoutParams(params);
        rl.addView(tv3y);

        params = new RelativeLayout.LayoutParams(130, 80);
        params.setMargins(490, 150, 0, 0);
        TextView tv4y = new TextView(this);
        tv4y.setText("102");
        tv4y.setGravity(Gravity.CENTER);
        tv4y.setTextColor(Color.BLACK);
        tv4y.setTextSize(20);
        tv4y.setLayoutParams(params);
        rl.addView(tv4y);
    }

    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {

                case R.id.fh:
                    PDActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    class PDEntity {
        private int ygq;
        private int jxq;
        private int yxq;

        public int getYgq() {
            return ygq;
        }

        public void setYgq(int ygq) {
            this.ygq = ygq;
        }

        public int getJxq() {
            return jxq;
        }

        public void setJxq(int jxq) {
            this.jxq = jxq;
        }

        public int getYxq() {
            return yxq;
        }

        public void setYxq(int yxq) {
            this.yxq = yxq;
        }
    }
}
