package com.example.zero.memorandum.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zero.memorandum.Main_activity;
import com.example.zero.memorandum.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zero on 2017/2/16.
 */

public class MemorandumAdapter extends BaseAdapter {

    private Context context;
    private List<Memorandum_JavaBean> list;

    public MemorandumAdapter(Main_activity mainActivity, List<Memorandum_JavaBean> list) {
        this.context = mainActivity;
        this.list = list;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        LinearLayout.LayoutParams linearParams=null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_layout, null);
            //实例化对象
            holder.layout = (LinearLayout) view.findViewById(R.id.layout_item);
            holder.time = (TextView) view.findViewById(R.id.item_time);
            holder.substance = (TextView) view.findViewById(R.id.item_substance);
            holder.imgbtn_arrow = (TextView) view.findViewById(R.id.imgbtn_arrow);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.time.setText(list.get(i).time.toString());
        holder.substance.setText(list.get(i).substance.toString());

        final ViewHolder finalHolder = holder;
        holder.imgbtn_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.imgbtn_arrow:
//                        v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.rotate90));//旋转动画，时间不同步弃用
                        switch (v.getBackground().getLevel()){
                            case 0:
                                v.getBackground().setLevel(1);
                                finalHolder.substance.setMaxLines(20);
                                break;
                            case 1:
                                v.getBackground().setLevel(0);
                                finalHolder.substance.setMaxLines(1);
                                break;
                        }
                        break;
                }
            }
        });
        return view;
    }

    }
    class ViewHolder{
        LinearLayout layout;
        TextView time;//时间
        TextView substance;//内容
        TextView imgbtn_arrow;//箭头
    }

