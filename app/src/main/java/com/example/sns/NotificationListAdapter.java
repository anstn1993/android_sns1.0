package com.example.sns;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NotificationItem> notificationItemArrayList;

    NotificationRecyclerViewClickListener mListener;

    interface NotificationRecyclerViewClickListener{
        void onNotificationClicked(int position);
        void onProfileClicked(int position);
    }

    public void setOnClickListener(NotificationRecyclerViewClickListener listener){
        mListener=listener;
    }




    public NotificationListAdapter(ArrayList<NotificationItem> notificationItemArrayList) {
        this.notificationItemArrayList = notificationItemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item,viewGroup,false);
        return new NotificationList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BitmapFactory.Options profileOptions=new BitmapFactory.Options();
        profileOptions.inSampleSize=30;
        Date date=new Date();


        ((NotificationList)viewHolder).CircleImageView_profile_picture.setRotation(90);
        ((NotificationList)viewHolder).CircleImageView_profile_picture.setImageBitmap(BitmapFactory.decodeFile(notificationItemArrayList.get(position).profilePicture,profileOptions));
        ((NotificationList)viewHolder).TextView_notification_text.setText(notificationItemArrayList.get(position).notificationText);
        ((NotificationList)viewHolder).TextView_Comment_text.setText(notificationItemArrayList.get(position).notificationComment);

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date=dateFormat.parse(notificationItemArrayList.get(position).date);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        ((NotificationList)viewHolder).TextView_notification_time.setText(beforeTime(date));

        if(mListener!=null){
            final int index=viewHolder.getAdapterPosition();
            ((NotificationList)viewHolder).CircleImageView_profile_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onProfileClicked(index);
                }
            });

            ((NotificationList)viewHolder).TextView_notification_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNotificationClicked(index);
                }
            });

            ((NotificationList)viewHolder).TextView_Comment_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNotificationClicked(index);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notificationItemArrayList.size();
    }

    private static class NotificationList extends RecyclerView.ViewHolder {

        CircleImageView CircleImageView_profile_picture;
        TextView TextView_notification_text, TextView_Comment_text, TextView_notification_time;

        public NotificationList(View view) {
            super(view);
            CircleImageView_profile_picture=(CircleImageView) view.findViewById(R.id.CircleImageView_profile_picture);
            TextView_notification_text=(TextView) view.findViewById(R.id.TextView_notification_text);
            TextView_Comment_text=(TextView)view.findViewById(R.id.TextView_Comment_text);
            TextView_notification_time=(TextView)view.findViewById(R.id.TextView_notification_time);
        }
    }

    public String beforeTime(Date date){

        //캘린더 클레스는 추상 클레스라서 객체를 생성할 수 없다.
        //대신 getInstance()메소드를 통해서 객체 생성이 가능하다.
        Calendar c = Calendar.getInstance();

        //캘린더 객체에서 getTimeInMillis()메소드를 사용해 현재 시간을 가져옴
        long now = c.getTimeInMillis();
        //date에서 시간만 가져온다. 여기서 중요한 점은 now변수는 계속해서 현재시간을 반환하기 때문에 변하는 수이고
        //date는 내가 선언한 순간의 시간을 가져오기 때문에 고정된 시간이다.
        long dateM = date.getTime();

        //이 변수는 위에서 봤듯이 현재의 시간에서 내가 이 메소드를 호출한 시간을 뺀 시간을 의미한다.
        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long)(gap/1000);
        long hour = gap/3600;
        gap = gap%3600;
        long min = gap/60;
        long sec = gap%60;

        if(hour > 24){
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        else if(hour > 0){
            ret = hour+"시간 전";
        }
        else if(min > 0){
            ret = min+"분 전";
        }
        else if(sec > 0){
            ret = sec+"초 전";
        }
        else{
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        return ret;

    }
}
