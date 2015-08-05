package com.lazycare.carcaremaster.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.AppointmentClass;
import com.lazycare.carcaremaster.util.DateUtil;

/**
 * 预约界面adapter
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentListAdapter extends BaseAdapter {

    public List<AppointmentClass> listAppointment = new ArrayList<>();
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
            holder.ci_userphoto = (SimpleDraweeView) convertView
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
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(ac.getHead()))
                    .setAutoRotateEnabled(true)//设置图片智能摆正
                    .setProgressiveRenderingEnabled(true)//设置渐进显示
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.ci_userphoto.getController())
                    .build();
            holder.ci_userphoto.setController(controller);
        }
        holder.tv_phonenumber.setText(ac.getMmobile());
        holder.tv_appointstate.setText(ac.getService_state());
        holder.tv_content.setText(ac.getService());
        holder.tv_cartype.setText(ac.getCar());
        holder.tv_appointtime.setText(ac.getBook_time());
        holder.rl_appointphone = (RelativeLayout) convertView
                .findViewById(R.id.rl_appointphone);
        //付款状态
        if (ac.getPay_state().equals("1"))
            holder.tv_pay_state.setVisibility(View.VISIBLE);
        else {
            holder.tv_pay_state.setVisibility(View.GONE);
        }

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
        int period = 0;
        try {
            //系统时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddkkmmss");
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String strnow = formatter.format(curDate);
            //下单时间
            Date oldDate = formatter2.parse(ac.getAdd_time());
            String str = formatter.format(oldDate);
            //得到时间差
            period = DateUtil.getSecsDiff(strnow, str);
            Log.d("gmyboy", "str=" + str + "  strnow=" + strnow + "   peroid=" + period + "" + "  addtime= " + ac.getAdd_time());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //标准单  未付款   时间间隔小于10分钟   （period < Config.PEROID && period >= 0 &&）
        if (ac.getPay_state().equals("0") && ac.getStandard().equals("1")) {
//            new TimerClass(holder.tv_servicetime, Config.PEROID - period).schedule();
            holder.tv_servicetime.setText("等待车主支付中...");
        } else {
            holder.tv_servicetime.setText(ac.getAdd_time());
        }
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView ci_userphoto;
        TextView tv_phonenumber;
        TextView tv_servicetime;
        TextView tv_appointstate;
        TextView tv_content;
        ImageView tv_pay_state;
        TextView tv_cartype;
        TextView tv_appointtime;
        RelativeLayout rl_appointphone;
    }

    /**
     * 显示倒计时
     */
    class TimerClass extends Timer {
        private TextView textView;
        private int period;
        private TimerTask timerTask;
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    textView.setText("等待支付: " + msg.what / 60 + "分" + msg.what % 60 + "秒");
                } else {
                    textView.setText("该订单已被取消");
                    // 结束Timer计时器
                    cancel();
                }
            }
        };

        public TimerClass(TextView textView, final int period) {
            this.textView = textView;
            this.period = period;
            timerTask = new TimerTask() {
                int i = period;

                @Override
                public void run() {
                    Log.d("debug", "run方法所在的线程："
                            + Thread.currentThread().getName());
                    // 定义一个消息传过去
                    Message msg = new Message();
                    msg.what = i--;
                    handler.sendMessage(msg);
                }
            };
        }


        public void schedule() {
            schedule(timerTask, 1000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒
        }
    }

}
