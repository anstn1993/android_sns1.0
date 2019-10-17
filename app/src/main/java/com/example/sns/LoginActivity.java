package com.example.sns;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.sns.MainActivity.clearMyPostArray;

public class LoginActivity extends AppCompatActivity {

    Button Button_login, Button_join;
    EditText EditText_ID, EditText_password;
    Bundle bundle=new Bundle();
    TextView TextView_lookforid, TextView_lookforpassword;
    CheckBox checkbox_autologin,checkbox_rememberid;
    public static String ID;
    public static String Nickname;
    Boolean autoLogin;
    Boolean rememberId;
    Boolean logedIn;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //회원가입 버튼 뷰를 객체와 연동
        Button_join=findViewById(R.id.Button_join);
        //아이디 입력 뷰를 객체와 연동
        EditText_ID=findViewById(R.id.EditText_ID);
        //비밀번호 입력 뷰를 객체와 연동
        EditText_password=findViewById(R.id.EditText_password);
        //로그인 버튼 뷰를 객체와 연동
        Button_login=findViewById(R.id.Button_login);
        //아이디 찾기 버튼 뷰를 객체와 연동
        TextView_lookforid=findViewById(R.id.TextView_lookforid);
        //비밀번호 찾기 버튼 뷰를 객체와 연동
        TextView_lookforpassword=findViewById(R.id.TextView_lookforpassword);
        //자동로그인 체크박스를 객체와 연동
        checkbox_autologin=findViewById(R.id.checkbox_autologin);
        //아이디 기억 체크박스를 객체와 연동
        checkbox_rememberid=findViewById(R.id.checkbox_rememberid);


        clearMyPostArray(LoginActivity.this);


        //자동 로그인을 위해 체크박스를 체크했을 때의 boolean값을 autoLogin변수에 넣어줌
        autoLogin=getSharedPreferences("AutoLogin",MODE_PRIVATE).getBoolean("AutoLoginState",false);
        //자동로그인 체크박스에 체크를 한 후 로그인을 했는지 안 했는지의 여부를 저장한 값을 담는 변수
        logedIn=getSharedPreferences("LoginState",MODE_PRIVATE).getBoolean("LogedIn",false);
        //체크박스를 체크해서 AutoLoginState키의 값이 true일 경우 자동 로그인이 구현되도록 조건문 설정
        if(autoLogin==true) {
            //해당 조건문이 없을 때는 체크박스에 체크만 한 상태에서 종료 후 다시 실행하면 바로 로그인이 되는 문제가 있었음
            //그 문제를 해결하기 위해서 로그인 버튼 리스너 안에서 로그인에 성공하면 로그인 상태를 true로 저장하는 파일을 하나 생성하고 로그아웃을 하면 거기에 다시 false로 덮어쓰는 방식을 적용
            if (logedIn == false) {//체크박스에 체크를 하고 로그인은 하지 않았다면 재실행을 했을 때 로그인이 되지 않도록 함
                checkbox_autologin.setChecked(true);
            } else {//체크박스에 체크를 하고 로그인을 한 적이 있다면 재실행시 자동 로그인이 되도록 구현
                Log.d("자동로그인", "접근");
                //로그인 버튼을 누를 때 생성한 SaveLoginId파일의 LoginId키의 아이디 값을 가져와서 ID변수에 넣어줌
                ID = getSharedPreferences("SaveLoginId", MODE_PRIVATE).getString("LoginId", null);
                Log.d("로그인ID", ID);
                Nickname=getSharedPreferences("SaveLoginId",MODE_PRIVATE).getString("LoginNickname",null);
                Log.d("로그인 닉네임", Nickname);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }
        }






        //아이디 기억을 위해 체크박스를 체크했을 때의 boolean값을 rememberId변수에 넣어줌
        rememberId=getSharedPreferences("RememberId",MODE_PRIVATE).getBoolean("RememberIdState",false);
        //체크박스를 체크해서 RememberIdState키의 값이 true일 경우 아이디기억이 구현되도록 조건문 설정
        if(rememberId==true){
            //체크박스가 체크상태가 되도록 하고
            checkbox_rememberid.setChecked(true);
            //아이디 입력란에 LoginId키에 저장된 아이디 값을 입력시켜준다.
            EditText_ID.setText(getSharedPreferences("SaveLoginId",MODE_PRIVATE).getString("LoginId",null));
        }






        //로그인 버튼을 누를 경우 발생할 동작 정의
        Button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkId=EditText_ID.getText().toString();
                String checkPassowrd=EditText_password.getText().toString();
                String id=getSharedPreferences(checkId,MODE_PRIVATE).getString("Id",null);
                String password=getSharedPreferences(checkId,MODE_PRIVATE).getString("Password",null);
                String nickname=getSharedPreferences(checkId,MODE_PRIVATE).getString("Nickname",null);

                if(checkId.equals("")||checkPassowrd.equals("")){
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_LONG).show();

                }else{
                    if(getSharedPreferences(checkId,MODE_PRIVATE).getString("Id",null)!=null){
                        //아이디와 비밀번호가 회원가입시 저장된 아이디, 비밀번호와 같은 경우 메인 액티비티로 전환
                        if(checkId.equals(id) && checkPassowrd.equals(password)) {
                            //static 변수인 ID에 사용자가 입력한 아이디 값을 넣어줘서 해당 이름의 파일의 데이터가 쓰일 수 있게끔 함
                            ID=getSharedPreferences(checkId,MODE_PRIVATE).getString("Id",null);
                            Log.d("로그인 아이디", ID);
                            Nickname=getSharedPreferences(checkId,MODE_PRIVATE).getString("Nickname",null);
                            Log.d("로그인 닉네임", Nickname);
                            //로그인을 할 때 자동로그인 체크박스를 클릭하고 로그인을 하면 이후에는 로그인 과정을 거치지 않아서 static ID변수에 id값을 넣어줄 수 없기 때문에
                            //해당 문제를 해결하기 위해 로그인 할 때마다 SaveAutoLoginId라는 파일의 AutoLoginId 키에 id값을 저장해둔다.
                            SharedPreferences sharedPreferences=getSharedPreferences("SaveLoginId",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("LoginId",ID);
                            editor.putString("LoginNickname",Nickname);
                            editor.commit();

                            //자동로그인 체크박스에 체크만 하고 종료후 재실행했을 때 자동로그인 기능이 동작하는 것을 방지하기 위해서
                            // 체크박스에 체크하고 로그인을 한 상태인지 아닌지를 저장하기 위한 파일 생성
                            SharedPreferences sharedPreferences1=getSharedPreferences("LoginState",MODE_PRIVATE);
                            SharedPreferences.Editor editor1=sharedPreferences1.edit();
                            //로그인을 하는 경우이기 때문에 true가 저장됨
                            editor1.putBoolean("LogedIn",true);
                            editor1.commit();

                            //로그인하는 계정의 닉네임에 포커스를 줘서 메인 게시물의 닉네임과 비교하여 포커스가 있는 닉네임과 일치하는 경우
                            //더 보기에서 수정, 삭제 버튼이 보이게끔 하기 위한 코드


                            SharedPreferences sharedPreferences2=getSharedPreferences("MainPost",MODE_PRIVATE);
                            SharedPreferences.Editor editor2=sharedPreferences2.edit();
                            Log.d("메인포스트 사이즈: ",String.valueOf(sharedPreferences2.getInt("PostSize",0)));
                            //반복문에서 특정 숫자를 건너띄고 돌던 문제를 해결했음
                            //원인은 반복문 안의 반복문의 변수를 j로 설정해줬는데 맨 마지막에 j++가 아닌 i++가 되니까 i가 2씩 증가하게 되는 문제가 발생했음
                            //진짜 병신이냐??
                            for(int i=0; i<sharedPreferences2.getInt("PostSize",0); i++){
                                //만약 로그인하는 아이디 파일의 닉네임이 메인의 게시물의 닉네임과 일치하는 경우
                                if(Nickname.equals(sharedPreferences2.getString("PostNickname"+i,null))){
                                    //해당 게시물 인덱스의 포커스를 true로 줘서 수정, 삭제 버튼이 보이게 함
                                    editor2.putBoolean("PostFocusState"+i,true);
                                }else{
                                    //그렇지 않은 경우에는 false로 줘서 수정, 삭제 버튼을 감춤
                                    editor2.putBoolean("PostFocusState"+i,false);
                                }
                                editor2.apply();
                                Log.d("인덱스"+i+"포커스 상태",String.valueOf(sharedPreferences2.getBoolean("PostFocusState"+i,false)));

                                SharedPreferences sharedPreferences3=getSharedPreferences("Comment"+i,MODE_PRIVATE);
                                SharedPreferences.Editor editor3=sharedPreferences3.edit();
                                for(int j=0; j<getSharedPreferences("Comment"+i,MODE_PRIVATE).getInt("CommentSize",0);j++){
                                    if(Nickname.equals(getSharedPreferences("Comment"+i,MODE_PRIVATE).getString("CommentNickname"+j,null))){

                                        editor3.putInt("ViewType"+j,0);
                                        editor3.apply();
                                    }else{

                                        editor3.putInt("ViewType"+j,1);
                                        editor3.apply();
                                    }
                                    Log.d("게시물"+i+"의 댓글 "+j+"의 뷰 타입",String.valueOf(sharedPreferences3.getInt("ViewType"+j,-1)));
                                }
                            }

                            Toast.makeText(getApplicationContext(), "반갑습니다. " + nickname + "님", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{//다를 경우 메인 액티비티로 진입 불가
                            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "존재하지 않는 계정입니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });







        //회원가입 버튼을 누르면 회원가입 액티비티로 전환
        Button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent();
//                ComponentName componentName=new ComponentName("com.example.layout_assignment","com.example.layout_assignment.JoinActivity");
//                intent.setComponent(componentName);
//                startActivity(intent);

                  //로그인 액티비티에서 회원가입 액티비티로 이동하라는 내용을 담은 인텐트 전달
                  Intent intent=new Intent(LoginActivity.this,JoinActivity.class);
                  startActivity(intent);
            }
        });






        //아이디 찾기 버튼을 누르면 아이디 찾기 액티비티로 전환
        TextView_lookforid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, FindIdActivity.class);
                startActivity(intent);
            }
        });






        //비밀번호 찾기 버튼을 누르면 비밀번호 찾기 액티비티로 전환
        TextView_lookforpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });






        //자동로그인 체크박스 클릭시 동작 정의
        checkbox_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //체크박스가 체크됐을 경우 동작을 정의한다.
                if(isChecked){
                    SharedPreferences sharedPreferences=getSharedPreferences("AutoLogin",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //자동 로그인 체크박스가 체크가 된 상태를 저장함
                    editor.putBoolean("AutoLoginState", checkbox_autologin.isChecked());
                    editor.commit();
                    Log.d("자동로그인", getSharedPreferences("AutoLogin",MODE_PRIVATE).toString());
                }else{//체크박스 체크가 해제됐을 경우 동작을 정의한다.
                    SharedPreferences sharedPreferences=getSharedPreferences("AutoLogin",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //자동 로그인 체크박스의 체크 상태 저장 내용을 다 지운다.
                    editor.clear();
                    editor.commit();
                    Log.d("자동로그인", getSharedPreferences("AutoLogin",MODE_PRIVATE).toString());
                }
            }
        });






        //아이디 저장 체크박스 클릭시 동작 정의
        checkbox_rememberid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences sharedPreferences=getSharedPreferences("RememberId",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //아이디 저장 체크박스가 체크가 된 상태를 저장함
                    editor.putBoolean("RememberIdState", checkbox_rememberid.isChecked());
                    editor.commit();
                    Log.d("아이디기억", getSharedPreferences("RememberId",MODE_PRIVATE).toString());
                }else{//체크박스 체크가 해제됐을 경우 동작을 정의한다.
                    SharedPreferences sharedPreferences=getSharedPreferences("RememberId",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //자동 로그인 체크박스의 체크 상태 저장 내용을 다 지운다.
                    editor.clear();
                    editor.commit();
                    Log.d("아이디기억", getSharedPreferences("AutoLogin",MODE_PRIVATE).toString());
                }
            }
        });

    }










    //재사용되는 액티비티에 인텐트를 넘겨받기 위해서 필요한 메소드
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Intent intent=getIntent();
        //번들에 받아온 데이터 입력
        bundle=intent.getExtras();
        if(bundle!=null){
            //Id변수에 번들의 Id값 대입
            String id=bundle.getString("Id");
            //Password변수에 번들의 Password값 대입
            String password=bundle.getString("Password");

            EditText_ID.setText(id);
            //받아온 Password 값을 로그인 액티비티의 비밀번호 입력 뷰에 입력
            EditText_password.setText(password);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();


    }
}
