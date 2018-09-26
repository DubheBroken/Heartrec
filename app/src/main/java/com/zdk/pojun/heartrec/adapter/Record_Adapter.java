package com.zdk.pojun.heartrec.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.activity.Main_activity;
import com.zdk.pojun.heartrec.entity.Record_Entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer on 2017/7/3.
 */

public class Record_Adapter extends RecyclerView.Adapter<Record_Adapter.ViewHolder> {

    private Context context;
    private List<Record_Entity> list;
    private List<ViewHolder> viewHolderList;
    private Record_Adapter.onRecyclerItemClickerListener clickerListener;

    public Record_Adapter(Main_activity mainActivity, List<Record_Entity> list) {
        this.context = mainActivity;
        this.list = list;
        this.viewHolderList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(Record_Adapter.ViewHolder holder, int position) {
        //        处理时间字符串
        String time = list.get(position).getTime();
        time = time.substring(6, time.length());
        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        time = timeFormatter(time, oldFormatter, newFormatter);

        //给控件赋值
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(Record_Adapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public Record_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_item_layout, parent, false);
        return new Record_Adapter.ViewHolder(view);
    }

    public void setItemListener(Record_Adapter.onRecyclerItemClickerListener listener) {
        this.clickerListener = listener;
    }

    /**
     * 点击监听回调接口
     */
    public interface onRecyclerItemClickerListener {
        void onRecyclerItemClick(View view, Object data, int position);
    }

    private View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickerListener && null != v) {
                    clickerListener.onRecyclerItemClick(v, list.get(position), position);
                }
            }
        };
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<ViewHolder> getViewHolderList(){
        return this.viewHolderList;
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        ViewHolder holder = null;
//        if (view == null) {
//            holder = new ViewHolder();
//            //引入布局
//            view = View.inflate(context, R.layout.record_item_layout, null);
//            //实例化对象
//            holder.layout = (LinearLayout) view.findViewById(R.id.record_layout_item);
//            holder.time = (TextView) view.findViewById(R.id.record_item_time);
//            holder.image = (TextView) view.findViewById(R.id.record_item_play);
//
//            viewHolderList.add(holder);
//            view.setTag(holder);
//        } else {
//            holder = (ViewHolder) view.getTag();
//        }
//
////        处理时间字符串
//        String time = list.get(i).getTime();
//        time = time.substring(6, time.length());
//        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//        SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//        time = timeFormatter(time, oldFormatter, newFormatter);
//
//
//        //给控件赋值
//        holder.time.setText(time);
//
//        final ViewHolder finalHolder = holder;
//        return view;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.record_layout_item);
            time = itemView.findViewById(R.id.record_item_time);
            image = itemView.findViewById(R.id.record_item_play);
        }
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


