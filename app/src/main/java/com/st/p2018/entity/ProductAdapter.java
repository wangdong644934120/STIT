package com.st.p2018.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.st.p2018.stit.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/6.
 */

public class ProductAdapter extends BaseAdapter {

    static class ViewHolder {
        LinearLayout layout;
        TextView pp;
        TextView gg;
        TextView yxq;
        TextView yxts;
        TextView cfwz;
        HashMap<String, TextView> parView = new HashMap<String, TextView>();
    }

    private List<HashMap<String, String>> mData;
    private Context mContext;

    private Drawable border = null;

    public ProductAdapter(Context context, List<HashMap<String, String>> data) {
        this.mContext = context;
        mData = data;
        border = context.getResources().getDrawable(R.drawable.border);
    }

    // 适配器根据getCount()函数来确定要加载多少项
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int paramInt) {

        return mData.get(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {
        return paramInt;
    }

    /*
     * 当列表里的每一项显示到界面时，都会调用这个方法一次，并返回一个view 所以方法里面尽量要简单，不要做没必要的动作(non-Javadoc)
     * 使用holder优化
     */
    @Override
    public View getView(int index, View paramView, ViewGroup paramViewGroup) {
        ViewHolder holder = null;
        if (paramView == null) {// view为null则创建，优化性能
            holder = new ViewHolder();
            paramView = LayoutInflater.from(mContext).inflate(R.layout.item_product ,null);
            // 通过view来得到Item中的每个控件的操作权
            LinearLayout layout = (LinearLayout) paramView.findViewById(R.id.item_div);
            holder.pp = (TextView) paramView.findViewById(R.id.item_pp);
            holder.gg = (TextView) paramView.findViewById(R.id.item_gg);
            holder.yxq = (TextView) paramView.findViewById(R.id.item_yxq);
            holder.yxts = (TextView) paramView.findViewById(R.id.item_yxts);
            holder.cfwz = (TextView) paramView.findViewById(R.id.item_cfwz);
            holder.layout = layout;
            paramView.setTag(holder);
        }
        holder = (ViewHolder) paramView.getTag();
        HashMap<String, String> data = mData.get(index);
        // holder.layout.setBackgroundColor(Color.parseColor("#F0F8FC"));
        holder.pp.setText(data.get("pp") == null ?"": data.get("pp"));
        holder.gg.setText(data.get("gg") == null ? "" : data.get("gg"));
        holder.yxq.setText(data.get("yxq") == null ? "" : data.get("yxq"));
        holder.yxts.setText(data.get("yxts") == null ? "" : data.get("yxts"));
        holder.cfwz.setText(data.get("cfwz") == null ? "" : data.get("cfwz"));
        return paramView;
    }

    @SuppressLint("NewApi")
    private TextView createTextView() {
        TextView text = new TextView(mContext);
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT);
        text.setLayoutParams(layoutParam);
        text.setSingleLine();
        text.setGravity(Gravity.CENTER);
        text.setTextColor(Color.parseColor("#3E648B"));
        text.setTextSize(20);
        text.setBackground(border);
        return text;
    }
}
