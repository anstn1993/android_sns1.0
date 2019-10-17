package com.example.sns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.sns.LoginActivity.Nickname;

public class NotificationActivity extends AppCompatActivity implements NotificationListAdapter.NotificationRecyclerViewClickListener{


    ImageButton Button_home;
    ImageButton Button_search;
    ImageButton Button_post;
    ImageButton Button_account;
    TextView TextView_delete;
    Bundle bundle;
    RecyclerView RecyclerView_notification_list;
    RecyclerView.LayoutManager layoutManager;
    public static ArrayList<NotificationItem> notificationItemArrayList=new ArrayList<>();
    NotificationListAdapter notificationListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Button_home=findViewById(R.id.Button_home);
        Button_search=findViewById(R.id.Button_search);
        Button_post=findViewById(R.id.Button_post);
        Button_account=findViewById(R.id.Button_account);
        TextView_delete=findViewById(R.id.TextView_delete);

        RecyclerView_notification_list=findViewById(R.id.RecyclerView_notification_list);
        RecyclerView_notification_list.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        RecyclerView_notification_list.setLayoutManager(layoutManager);

        loadNotificationArray(this, Nickname);


        notificationListAdapter=new NotificationListAdapter(notificationItemArrayList);
        notificationListAdapter.setOnClickListener(NotificationActivity.this);
        RecyclerView_notification_list.setAdapter(notificationListAdapter);




        //홈버튼을 누르면 알림 액티비티에서 메인(홈) 액티비티로 이동
        Button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //검색 버튼을 누르면 알림 액티비티에서 검색 액티비티로 이동
        Button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, SearchActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //포스트 버튼을 누르면 알림 액티비티에서 포스트 액티비티로 이동
        Button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, PostActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //계정 버튼을 누르면 알림 액티비티에서 계정 액티비티로 이동
        Button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotificationActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        TextView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSharedPreferences(Nickname+"Notification",MODE_PRIVATE).getInt("NotificationSize",0)!=0){
                    //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                    final Dialog dialog=new Dialog(NotificationActivity.this);
                    //타이틀 바 없애기
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //다이얼로그에 연결시킬 레이아웃 설정
                    dialog.setContentView(R.layout.clear_notification_check_box);
                    //다이얼로그 띄우기
                    dialog.show();

                    //Dialog의 레이아웃에 있는 텍스트뷰 객체를 선언
                    TextView TextView_yes, TextView_no;

                    //객체를 레이아웃의 뷰와 연결
                    TextView_yes=dialog.findViewById(R.id.TextView_yes);
                    TextView_no=dialog.findViewById(R.id.TextView_no);

                    //
                    TextView_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notificationItemArrayList.clear();
                            clearNotification(Nickname);
                            saveNotificationArray(NotificationActivity.this, Nickname);
                            loadNotificationArray(NotificationActivity.this, Nickname);
                            dialog.dismiss();
                        }
                    });

                    TextView_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }else{
                    //현재 액티비티에서 뜨게 할 다이얼로그 객체 생성
                    final Dialog dialog=new Dialog(NotificationActivity.this);
                    //타이틀 바 없애기
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //다이얼로그에 연결시킬 레이아웃 설정
                    dialog.setContentView(R.layout.nothing_notification_box);
                    //다이얼로그 띄우기
                    dialog.show();

                    //Dialog의 레이아웃에 있는 텍스트뷰 객체를 선언
                    TextView TextView_check;

                    //객체를 레이아웃의 뷰와 연결
                    TextView_check=dialog.findViewById(R.id.TextView_check);


                    //
                    TextView_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }



            }
        });
    }


    public static void saveNotificationArray(Context context, String nickname){
        SharedPreferences notificationSharedpreferences=context.getSharedPreferences(nickname+"Notification",MODE_PRIVATE);
        SharedPreferences.Editor notificationEditor=notificationSharedpreferences.edit();
        notificationEditor.putInt("NotificationSize",notificationItemArrayList.size());

        for(int i=0; i<notificationItemArrayList.size(); i++){
            notificationEditor.putString("NotificationProfilePicture"+i,notificationItemArrayList.get(i).profilePicture);
            notificationEditor.putString("NotificationText"+i,notificationItemArrayList.get(i).notificationText);
            notificationEditor.putString("NotificationNickname"+i,notificationItemArrayList.get(i).notificationNickname);
            notificationEditor.putString("NotificationComment"+i,notificationItemArrayList.get(i).notificationComment);
            notificationEditor.putInt("NotificationPostIndex"+i,notificationItemArrayList.get(i).notificationPostIndex);
            notificationEditor.putString("NotificationTime"+i,notificationItemArrayList.get(i).date);

        }

        notificationEditor.commit();
    }

    public static void loadNotificationArray(Context context, String nickname){
        SharedPreferences notificationSharedpreferences=context.getSharedPreferences(nickname+"Notification",MODE_PRIVATE);
        notificationItemArrayList.clear();
        int notificationSize=notificationSharedpreferences.getInt("NotificationSize",0);

        for(int i=0; i<notificationSize; i++){
            notificationItemArrayList.add(new NotificationItem(
                    notificationSharedpreferences.getString("NotificationProfilePicture"+i,null),
                    notificationSharedpreferences.getString("NotificationText"+i,null),
                    notificationSharedpreferences.getString("NotificationNickname"+i,null),
                    notificationSharedpreferences.getString("NotificationComment"+i,null),
                    notificationSharedpreferences.getInt("NotificationPostIndex"+i,0),
                    notificationSharedpreferences.getString("NotificationTime"+i,null)

            ));
        }
    }

    public void clearNotification(String nickname){
        SharedPreferences notificationSharedpreferences=getSharedPreferences(nickname+"Notification",MODE_PRIVATE);
        SharedPreferences.Editor notificationEditor=notificationSharedpreferences.edit();
        notificationListAdapter.notifyDataSetChanged();

        notificationEditor.clear();
        notificationEditor.commit();
    }

    @Override
    public void onNotificationClicked(int position) {
        Intent intent=new Intent(NotificationActivity.this, MyPostActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle=new Bundle();
        bundle.putInt("Position",getSharedPreferences(Nickname+"Notification",MODE_PRIVATE).getInt("NotificationPostIndex"+position,0));
        Log.d("인덱스: ",String.valueOf(getSharedPreferences(Nickname+"Notification",MODE_PRIVATE).getInt("NotificationPostIndex"+position,0)));
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onProfileClicked(int position) {
        Intent intent=new Intent(NotificationActivity.this, AccountOthersActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle=new Bundle();
        bundle.putString("PostNickname", getSharedPreferences(Nickname+"Notification",MODE_PRIVATE).getString("NotificationNickname"+position,null));
        Log.d("프로필 닉네임: ", getSharedPreferences(Nickname+"Notification",MODE_PRIVATE).getString("NotificationNickname"+position,null));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
