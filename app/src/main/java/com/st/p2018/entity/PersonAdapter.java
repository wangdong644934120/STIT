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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.st.p2018.stit.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/6.
 */

public class PersonAdapter extends BaseAdapter {

    static class ViewHolder {
        LinearLayout layout;
        TextView id;
        TextView code;
        TextView name;
        TextView card;
        TextView tzz;

        HashMap<String, TextView> parView = new HashMap<String, TextView>();
    }

    private List<HashMap<String, String>> mData;
    private Context mContext;

    private Drawable border = null;

    public PersonAdapter(Context context, List<HashMap<String, String>> data) {
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
            paramView = LayoutInflater.from(mContext).inflate(R.layout.item_person ,null);
            // 通过view来得到Item中的每个控件的操作权
            LinearLayout layout = (LinearLayout) paramView.findViewById(R.id.item_div);
            holder.id = (TextView) paramView.findViewById(R.id.item_id);
            holder.name = (TextView) paramView.findViewById(R.id.item_name);
            holder.code = (TextView) paramView.findViewById(R.id.item_code);
            holder.card=(TextView) paramView.findViewById(R.id.item_card);
            holder.tzz = (TextView) paramView.findViewById(R.id.item_tzz);
            holder.layout = layout;
            paramView.setTag(holder);
        }
        holder = (ViewHolder) paramView.getTag();
        HashMap<String, String> data = mData.get(index);
        // holder.layout.setBackgroundColor(Color.parseColor("#F0F8FC"));
        holder.id.setText(data.get("id") == null ?"": data.get("id"));
        holder.name.setText(data.get("name") == null ? "" : data.get("name"));
        holder.code.setText(data.get("code") == null ? "" : data.get("code"));
        holder.card.setText(data.get("card") == null ? "" : data.get("card"));
        holder.tzz.setText(data.get("tzz") == null ? "" : data.get("tzz"));
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
