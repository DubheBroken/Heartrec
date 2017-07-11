package com.example.zero.memorandum.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.AlphabeticIndex;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zero.memorandum.R;
import com.example.zero.memorandum.activity.Main_activity;
import com.example.zero.memorandum.activity.Record_activity;
import com.example.zero.memorandum.custom.MySeekBar;
import com.example.zero.memorandum.entity.Paint_Entity;
import com.example.zero.memorandum.entity.Record_Entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer on 2017/7/3.
 */

public class Record_Adapter extends BaseAdapter {

    private Context context;
    private List<Record_Entity> list;
    private List<ViewHolder> viewHolderList;

    public Record_Adapter(Main_activity mainActivity, List<Record_Entity> list) {
        this.context = mainActivity;
        this.list = list;
        this.viewHolderList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<ViewHolder> getViewHolderList(){
        return this.viewHolderList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.record_item_layout, null);
            //实例化对象
            holder.layout = (LinearLayout) view.findViewById(R.id.record_layout_item);
            holder.time = (TextView) view.findViewById(R.id.record_item_time);
            holder.image = (TextView) view.findViewById(R.id.record_item_play);

            viewHolderList.add(holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

//        处理时间字符串
        String time = list.get(i).getTime();
        time = time.substring(6, time.length());
        Log.i("---time---", time);
        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        time = timeFormatter(time, oldFormatter, newFormatter);


        //给控件赋值
        holder.time.setText(time);

        final ViewHolder finalHolder = holder;
        return view;
    }

    public class ViewHolder {
        public LinearLayout layout;
        public TextView time;//总时间
        public TextView image;//播放按钮
    }

    /**
     * 将String型格式化,比如想要将2011-11-11格式化成2011年11月11日,就StringPattern("2011-11-11","yyyy-MM-dd","yyyy年MM月dd日").
     *
     * @param time       String 想要格式化的日期
     * @param oldPattern String 想要格式化的日期的现有格式
     * @param newPattern String 想要格式化成什么格式
     * @return String
     */
    public final String timeFormatter(String time, SimpleDateFormat oldPattern, SimpleDateFormat newPattern) {
        if (time == null || oldPattern == null || newPattern == null)
            return "";
        SimpleDateFormat sdf1 = oldPattern;        // 旧格式
        SimpleDateFormat sdf2 = newPattern;        // 新格式
        Date d = null;
        try {
            d = sdf1.parse(time);   // 将给定的字符串中的日期提取出来
        } catch (Exception e) {            // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace();       // 打印异常信息
        }
        return sdf2.format(d);
    }


}


