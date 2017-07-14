package com.zdk.pojun.heartrec.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.activity.Main_activity;
import com.zdk.pojun.heartrec.entity.Text_Entity;

import java.util.List;

/**
 * Created by Zero on 2017/2/16.
 */

public class Text_Adapter extends BaseAdapter {

    private Context context;
    private List<Text_Entity> list;

    public Text_Adapter(Main_activity mainActivity, List<Text_Entity> list) {
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
        LinearLayout.LayoutParams linearParams = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.text_item_layout, null);
            //实例化对象
            holder.layout = (LinearLayout) view.findViewById(R.id.text_layout_item);
            holder.time = (TextView) view.findViewById(R.id.text_item_time);
            holder.substance = (TextView) view.findViewById(R.id.text_item_substance);
            holder.imgbtn_arrow = (TextView) view.findViewById(R.id.text_item_imgbtn_arrow);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.time.setText(list.get(i).getTime());
        holder.substance.setText(list.get(i).getSubstance());

        final ViewHolder finalHolder = holder;
        holder.imgbtn_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.text_item_imgbtn_arrow:
//                        v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.rotate90));//旋转动画，时间不同步弃用
                        switch (v.getBackground().getLevel()) {
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

    private class ViewHolder {
        LinearLayout layout;
        TextView time;//时间
        TextView substance;//内容
        TextView imgbtn_arrow;//箭头
    }
}



