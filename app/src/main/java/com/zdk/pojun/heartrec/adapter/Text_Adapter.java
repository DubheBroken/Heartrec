package com.zdk.pojun.heartrec.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.activity.Main_activity;
import com.zdk.pojun.heartrec.entity.Text_Entity;

import java.util.List;

/**
 * Created by Zero on 2017/2/16.
 */

public class Text_Adapter extends RecyclerView.Adapter<Text_Adapter.ViewHolder> {

    private Context context;
    private List<Text_Entity> list;
    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener = null;


    public Text_Adapter(Main_activity mainActivity, List<Text_Entity> list) {
        this.context = mainActivity;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(Text_Adapter.ViewHolder holder, final int position) {
        //给控件赋值
        holder.time.setText(list.get(position).getTime());
        holder.substance.setText(list.get(position).getSubstance());

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

        //通过为条目设置点击事件触发回调
        if (onItemClickListener != null) {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view,position);
                }
            });
        }

        if( onItemLongClickListener!=null) {
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null)
                        onItemLongClickListener.onItemLongClick(v, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.text_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    //设置回调接口
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    //设置回调接口
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.text_layout_item);
            time = itemView.findViewById(R.id.text_item_time);
            substance = itemView.findViewById(R.id.text_item_substance);
            imgbtn_arrow = itemView.findViewById(R.id.text_item_imgbtn_arrow);
        }
        LinearLayout layout;
        TextView time;//时间
        TextView substance;//内容
        TextView imgbtn_arrow;//箭头
    }
}



