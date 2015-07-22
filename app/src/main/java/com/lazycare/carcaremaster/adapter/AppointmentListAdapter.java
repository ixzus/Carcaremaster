package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.AppointmentClass;
import com.lazycare.carcaremaster.widget.CircularImage;
import com.squareup.picasso.Picasso;

/**
 * 预约界面adapter
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentListAdapter extends BaseAdapter {

    public List<AppointmentClass> listAppointment = new ArrayList<AppointmentClass>();
    private Context mContext;

    public AppointmentListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public AppointmentListAdapter(List<AppointmentClass> mList, Context mContext) {
        super();
        this.listAppointment = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listAppointment.size();
    }

    @Override
    public Object getItem(int position) {
        return listAppointment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addNewItem(AppointmentClass newItem) {
        listAppointment.add(newItem);
    }

    public void removeAll() {
        listAppointment.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_appointmentlist, null);
            holder.ci_userphoto = (CircularImage) convertView
                    .findViewById(R.id.ci_userphoto);
            holder.tv_phonenumber = (TextView) convertView
                    .findViewById(R.id.tv_phonenumber);
            holder.tv_servicetime = (TextView) convertView
                    .findViewById(R.id.tv_servicetime);
            holder.tv_appointstate = (TextView) convertView
                    .findViewById(R.id.tv_appointstate);
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.tv_cartype = (TextView) convertView
                    .findViewById(R.id.tv_cartype);
            holder.tv_appointtime = (TextView) convertView
                    .findViewById(R.id.tv_appointtime);
            holder.tv_pay_state = (ImageView) convertView
                    .findViewById(R.id.tv_pay_state);
            holder.rl_appointphone = (RelativeLayout) convertView
                    .findViewById(R.id.rl_appointphone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppointmentClass ac = listAppointment.get(position);
        if (ac.getHead() != null && !ac.getHead().equals("")) {
            Picasso.with(mContext).load(ac.getHead()).into(holder.ci_userphoto);
        }
        // holder.ci_userphoto.setImageDrawable(drawable);
        holder.tv_phonenumber.setText(ac.getMmobile());
        String addtime = ac.getAdd_time();
        holder.tv_servicetime.setText(addtime);
        holder.tv_appointstate.setText(ac.getService_state());
        holder.tv_content.setText(ac.getService());
        holder.tv_cartype.setText(ac.getCar());
        holder.tv_appointtime.setText(ac.getBook_time());
        holder.rl_appointphone = (RelativeLayout) convertView
                .findViewById(R.id.rl_appointphone);
        if (ac.getPay_state().equals("1"))
            holder.tv_pay_state.setVisibility(View.VISIBLE);
        else
            holder.tv_pay_state.setVisibility(View.GONE);

        final String phonenumber = ac.getMobile();
        holder.rl_appointphone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("tel:" + phonenumber);
                Intent call = new Intent(Intent.ACTION_DIAL, uri);// 弹出拨号选择器，所以这里修改为跳转到拨号界面，如何不弹出这个选择框，暂时先这样
                mContext.startActivity(call);
            }
        });
        return convertView;
    }

    class ViewHolder {
        CircularImage ci_userphoto;
        TextView tv_phonenumber;
        TextView tv_servicetime;
        TextView tv_appointstate;
        TextView tv_content;
        ImageView tv_pay_state;
        TextView tv_cartype;
        TextView tv_appointtime;
        RelativeLayout rl_appointphone;
    }
}
