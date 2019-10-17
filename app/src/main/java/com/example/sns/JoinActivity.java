package com.example.sns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

import static com.example.sns.SearchActivity.searchItemArrayList;
import static com.example.sns.SearchActivity.searchedItemArrayList;

public class JoinActivity extends AppCompatActivity {

    EditText EditText_ID, EditText_name, EditText_nickname, EditText_password, EditText_checkpassword, EditText_identitynumber;
    Button Button_join, Button_cancel;
    public Bundle bundle=new Bundle();
    TextView TextView_idcheck, TextView_nicknamecheck, TextView_identitycheck;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    TextView_idcheck.setVisibility(View.VISIBLE);
                    TextView_idcheck.setText("사용불가");
                    TextView_idcheck.setTextColor(Color.RED);
                    break;
                case 1:
                    TextView_idcheck.setVisibility(View.VISIBLE);
                    TextView_idcheck.setText("사용가능");
                    TextView_idcheck.setTextColor(Color.GREEN);
                    break;
                case 2:
                    TextView_idcheck.setVisibility(View.GONE);
                    break;
                case 3:
                    TextView_nicknamecheck.setVisibility(View.VISIBLE);
                    TextView_nicknamecheck.setText("사용불가");
                    TextView_nicknamecheck.setTextColor(Color.RED);
                    break;
                case 4:
                    TextView_nicknamecheck.setVisibility(View.VISIBLE);
                    TextView_nicknamecheck.setText("사용가능");
                    TextView_nicknamecheck.setTextColor(Color.GREEN);
                    break;
                case 5:
                    TextView_nicknamecheck.setVisibility(View.GONE);
                    break;
                case 6:
                    TextView_identitycheck.setVisibility(View.VISIBLE);
                    TextView_identitycheck.setText("사용불가");
                    TextView_identitycheck.setTextColor(Color.RED);
                    break;
                case 7:
                    TextView_identitycheck.setVisibility(View.VISIBLE);
                    TextView_identitycheck.setText("사용가능");
                    TextView_identitycheck.setTextColor(Color.GREEN);
                    break;
                case 8:
                    TextView_identitycheck.setVisibility(View.GONE);
                    break;
                case 9:
                    Button_join.setBackgroundResource(R.drawable.joinbutton_border);
                    break;
                case 10:
                    Button_join.setBackgroundResource(R.drawable.joinbutton_border_notaccepted);
                    break;
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //취소 버튼 뷰를 객체와 연동
        Button_cancel=findViewById(R.id.Button_cancel);
        //회원가입 버튼 뷰를 객체와 연동
        Button_join=findViewById(R.id.Button_join);
        //아이디 입력 뷰를 객체와 연동
        EditText_ID=findViewById(R.id.EditText_ID);
        //이름 입력 뷰를 객체와 연동
        EditText_name=findViewById(R.id.EditText_name);
        //닉네임 입력 뷰를 객체와 연동
        EditText_nickname=findViewById(R.id.EditText_nickname);
        //비밀번호 입력 뷰를 객체와 연동
        EditText_password=findViewById(R.id.EditText_password);
        //비밀번호 확인 뷰를 객체와 연동
        EditText_checkpassword=findViewById(R.id.EditText_checkpassword);
        //주민번호 뷰를 객체와 연동
        EditText_identitynumber=findViewById(R.id.EditText_identitynumber);
        //아이디 중복확인 여부를 보여주는 뷰를 객체와 연동
        TextView_idcheck=findViewById(R.id.TextView_idcheck);
        //닉네임 중복확인 여부를 보여주는 뷰를 객체와 연동
        TextView_nicknamecheck=findViewById(R.id.TextView_nicknamecheck);
        //주민등록번호 중복확인 여부를 보여주는 뷰를 객체와 연동
        TextView_identitycheck=findViewById(R.id.TextView_identitycheck);



        //아이디 입력칸에 문자열이 입력될 때마다 감지를 하는 리스너를 통해서 문자열을 입력할 때마다 기존 가입 아이디들과 중복확인을 해준다.
        EditText_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //문자열이 입력된 후에 실행되는 메소드
            @Override
            public void afterTextChanged(Editable s) {
                Thread thread=new Thread(idCheckThread);
                thread.setDaemon(true);
                thread.start();
            }
        });

        EditText_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Thread thread=new Thread(nicknameCheckThread);
                thread.setDaemon(true);
                thread.start();
            }
        });

        EditText_identitynumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Thread thread=new Thread(identityCheckThread);
                thread.setDaemon(true);
                thread.start();
            }
        });


        //취소버튼을 누를 시 회원가입 액티비티가 완전 종료되면서 로그인 액티비티로 전환
        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//취소버튼을 누를 시 뒤로 돌아가기
            }
        });

        //회원가입 버튼을 누를 시 동작 정의
        Button_join.setOnClickListener(new View.OnClickListener() {//회원가입 버튼 눌렀을 때 일어날 동작 정의
            @Override
            public void onClick(View v) {
                //이메일 입력 뷰에 입력된 문자열을 담는 변수
                String id=EditText_ID.getText().toString();
                //이름 입력 뷰에 입력된 문자열을 담는 변수
                String name=EditText_name.getText().toString();
                //닉네임 입력 뷰에 입력된 문자열을 담는 변수
                String nickname=EditText_nickname.getText().toString();
                //비밀번호 입력 뷰에 입력된 문자열을 담는 변수
                String password=EditText_password.getText().toString();
                //비밀번호 확인 입력 뷰에 입력된 문자열을 담는 변수
                String checkPassword=EditText_checkpassword.getText().toString();
                //주민등록번호 입력 뷰에 입력된 문자열을 담는 변수
                String identity=EditText_identitynumber.getText().toString();

                if(id.equals("") || name.equals("") || nickname.equals("") || password.equals("") || checkPassword.equals("")){
                    Toast.makeText(getApplicationContext(),"항목을 모두 입력해주세요.",Toast.LENGTH_LONG).show();
                }else{
                    if(getSharedPreferences(id,MODE_PRIVATE).getString("Id",null)!=null){
                        Toast.makeText(getApplicationContext(),"중복확인을 해주세요.",Toast.LENGTH_LONG).show();
                    }else{
                        if(getSharedPreferences(nickname,MODE_PRIVATE).getString("Nickname",null)!=null){
                            Toast.makeText(getApplicationContext(),"중복확인을 해주세요.",Toast.LENGTH_LONG).show();
                        }else{
                            if(getSharedPreferences(identity,MODE_PRIVATE).getString("Identity",null)!=null){
                                Toast.makeText(getApplicationContext(),"중복확인을 해주세요.",Toast.LENGTH_LONG).show();
                            }else{
                                if(password.equals(checkPassword)){
                                    Hashtable<String, String> membershipTable=new Hashtable<>();
                                    membershipTable.put("Id",id);
                                    membershipTable.put("Password",password);
                                    membershipTable.put("Name",name);
                                    membershipTable.put("Nickname",nickname);
                                    membershipTable.put("Identity", identity);


                                    SharedPreferences sharedPreferencesId=getSharedPreferences(id,MODE_PRIVATE);
                                    SharedPreferences.Editor editorId=sharedPreferencesId.edit();
                                    editorId.putString("Id",membershipTable.get("Id"));
                                    editorId.putString("Password",membershipTable.get("Password"));
                                    editorId.putString("Name",membershipTable.get("Name"));
                                    editorId.putString("Nickname",membershipTable.get("Nickname"));
                                    editorId.putString("Identity",membershipTable.get("Identity"));
                                    editorId.commit();

                                    SharedPreferences sharedPreferencesNickname=getSharedPreferences(nickname,MODE_PRIVATE);
                                    SharedPreferences.Editor editorNickname=sharedPreferencesNickname.edit();
                                    editorNickname.putString("Id",membershipTable.get("Id"));
                                    editorNickname.putString("Password",membershipTable.get("Password"));
                                    editorNickname.putString("Name",membershipTable.get("Name"));
                                    editorNickname.putString("Nickname",membershipTable.get("Nickname"));
                                    editorNickname.putString("Identity",membershipTable.get("Identity"));
                                    editorNickname.commit();

                                    SharedPreferences sharedPreferencesName=getSharedPreferences(name,MODE_PRIVATE);
                                    SharedPreferences.Editor editorName=sharedPreferencesName.edit();
                                    editorName.putString("Id",membershipTable.get("Id"));
                                    editorName.putString("Password",membershipTable.get("Password"));
                                    editorName.putString("Name",membershipTable.get("Name"));
                                    editorName.putString("Nickname",membershipTable.get("Nickname"));
                                    editorName.putString("Identity",membershipTable.get("Identity"));
                                    editorName.commit();

                                    SharedPreferences sharedPreferencesIdentity=getSharedPreferences(identity,MODE_PRIVATE);
                                    SharedPreferences.Editor editorIdentity=sharedPreferencesIdentity.edit();
                                    editorIdentity.putString("Id",membershipTable.get("Id"));
                                    editorIdentity.putString("Password",membershipTable.get("Password"));
                                    editorIdentity.putString("Name",membershipTable.get("Name"));
                                    editorIdentity.putString("Nickname",membershipTable.get("Nickname"));
                                    editorIdentity.putString("Identity",membershipTable.get("Identity"));
                                    editorIdentity.commit();

                                    //회원가입을 할 때의 정보(이름과 닉네임)를 검색 Arraylist에 넣어서 저장해두고
                                    //검색 액티비티의 리사이클러뷰에 뿌려준다.
                                    loadSearchArray(JoinActivity.this);
                                    int position=searchItemArrayList.size();
                                    searchItemArrayList.add(new SearchItem(sharedPreferencesNickname.getString("Nickname",null),"("+sharedPreferencesNickname.getString("Name",null)+")"));
                                    saveSearchArray(JoinActivity.this);

                                    SearchAdapter searchAdapter=new SearchAdapter(searchItemArrayList);
                                    searchAdapter.notifyItemInserted(position);
                                    searchAdapter.notifyItemRangeChanged(position, searchItemArrayList.size());


                                    Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.",Toast.LENGTH_LONG).show();
                                    //회원가입 화면에서 로그인 화면으로 인텐트 전달
                                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                    //회원가입 액티비티는 한 번 호출되고 나가게 되면 더 이상 존재할 필요가 없기 때문에 이전 액티비티가 호출되면서 사라지게 함
                                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                                    intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    //아이디 데이터를 번들에 입력
                                    bundle.putString("Id", id);
                                    bundle.putString("Password", password);
                                    //번들을 인텐트에 담아서
                                    intent.putExtras(bundle);
                                    //다른 액티비티로 전달
                                    startActivity(intent);

                                }else{
                                    Toast.makeText(getApplicationContext(),"비밀번호와 비밀번호 확인이 일치하지 않습니다.",Toast.LENGTH_LONG).show();

                                }
                            }
                        }

                    }
                }
            }
        });
    }

    public static void saveSearchArray(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SearchList",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("SearchListSize",searchItemArrayList.size());

        for(int i=0; i<searchItemArrayList.size(); i++){
            editor.putString("Nickname"+i,searchItemArrayList.get(i).nickname);
            editor.putString("Name"+i,searchItemArrayList.get(i).name);
        }
        editor.commit();

    }

    public static void loadSearchArray(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SearchList",MODE_PRIVATE);
        searchItemArrayList.clear();
        int searchListSize=sharedPreferences.getInt("SearchListSize",0);

        for(int i=0; i<searchListSize; i++){
            searchItemArrayList.add(new SearchItem(
                    sharedPreferences.getString("Nickname"+i,null),
                    sharedPreferences.getString("Name"+i,null)
            ));
        }
    }

    public static void saveSearchedArray(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SearchedList",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("SearchedListSize",searchItemArrayList.size());

        for(int i=0; i<searchedItemArrayList.size(); i++){
            editor.putString("Nickname"+i,searchedItemArrayList.get(i).nickname);
            editor.putString("Name"+i,searchedItemArrayList.get(i).name);
        }
        editor.commit();

    }


    public static void loadSearchedArray(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SearchedList",MODE_PRIVATE);
        searchedItemArrayList.clear();
        int searchedListSize=sharedPreferences.getInt("SearchedListSize",0);

        for(int i=0; i<searchedListSize; i++){
            searchedItemArrayList.add(new SearchItem(
                    sharedPreferences.getString("Nickname"+i,null),
                    sharedPreferences.getString("Name"+i,null)
            ));
        }
    }

    //아이디 입력 칸에 문자열이 입력될 때마다 실행되는 메소드
    //입력될 때마다 문자를 받아와서 기존의 아이디와 비교한 후 중복이면 handler에 what=0을 전송하고
    //중복되지 않으면 handler에 what=1을 전송한다.
    //입력칸에 아무것도 입력되어있지 않은 경우에는 what=2를 전송
    Thread idCheckThread=new Thread(new Runnable() {
        @Override
        public void run() {
            String id=EditText_ID.getText().toString();
            if(id.length()!=0){
                if(EditText_ID!=null){
                    if(getSharedPreferences(id,MODE_PRIVATE).getString("Id",null)!=null){
                        Log.d("사용불가", "true");
                        handler.sendEmptyMessage(0);
                    }else{
                        Log.d("사용불가", "false");
                        handler.sendEmptyMessage(1);
                    }
                }
            }else {
                handler.sendEmptyMessage(2);
            }
        }
    });

    //닉네임 입력 칸에 문자열이 입력될 때마다 실행되는 메소드
    //입력될 때마다 문자를 받아와서 기존의 닉네임과 비교한 후 중복이면 handler에 what=3을 전송하고
    //중복되지 않으면 handler에 what=4를 전송한다.
    //입력칸에 아무것도 입력되어있지 않은 경우에는 what=5를 전송
    Thread nicknameCheckThread=new Thread(new Runnable() {
        @Override
        public void run() {
            String nickname=EditText_nickname.getText().toString();
            if(nickname.length()!=0){
                if(EditText_nickname!=null){
                    if(getSharedPreferences(nickname,MODE_PRIVATE).getString("Nickname",null)!=null){
                        Log.d("사용불가", "true");
                        handler.sendEmptyMessage(3);
                    }else{
                        Log.d("사용불가", "false");
                        handler.sendEmptyMessage(4);
                    }
                }
            }else {
                handler.sendEmptyMessage(5);
            }
        }
    });

    //주민등록번호 뒷자리 입력 칸에 문자열이 입력될 때마다 실행되는 메소드
    //입력될 때마다 문자를 받아와서 기존의 주민번호와 비교한 후 중복이면 handler에 what=6을 전송하고
    //중복되지 않으면 handler에 what=7를 전송한다.
    //입력칸에 아무것도 입력되어있지 않은 경우에는 what=8을 전송
    Thread identityCheckThread=new Thread(new Runnable() {
        @Override
        public void run() {
            String identity=EditText_identitynumber.getText().toString();
            if(identity.length()!=0){
                if(EditText_identitynumber!=null){
                    if(getSharedPreferences(identity,MODE_PRIVATE).getString("Identity",null)!=null){
                        Log.d("사용불가", "true");
                        handler.sendEmptyMessage(6);
                    }else{
                        Log.d("사용불가", "false");
                        handler.sendEmptyMessage(7);
                    }
                }
            }else {
                handler.sendEmptyMessage(8);
            }
        }
    });

//    Thread joinButtonTread=new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while(true) {
//                String id=EditText_ID.getText().toString();
//                String nickname=EditText_nickname.getText().toString();
//                String identity=EditText_identitynumber.getText().toString();
//                SharedPreferences idSF=getSharedPreferences(id,MODE_PRIVATE);
//                SharedPreferences nicknameSF=getSharedPreferences(nickname,MODE_PRIVATE);
//                SharedPreferences identitySF=getSharedPreferences(identity,MODE_PRIVATE);
//                if (idSF.getString("Id", null) != null && nicknameSF.getString("Nickname", null) != null && identitySF.getString("Identity", null) != null) {
//                    handler.sendEmptyMessage(9);
//                }else{
//                    handler.sendEmptyMessage(10);
//                }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d("쓰레드: ", "실행중");
//            }
//
//        }
//    });


}
