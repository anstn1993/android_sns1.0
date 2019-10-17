package com.example.sns;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText EditText_newpassword, EditText_checknewpassword;
    Button Button_changepassword, Button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText_newpassword=findViewById(R.id.EditText_newpassword);
        EditText_checknewpassword=findViewById(R.id.EditText_checknewpassword);
        Button_changepassword=findViewById(R.id.Button_changepassword);
        Button_cancel=findViewById(R.id.Button_cancel);

        //비밀번호 찾기 액티비티에서 던져준 아이디, 주민번호 데이터를 받기 위한 인텐트
        final Intent intent=getIntent();
        final String id=intent.getStringExtra("Id");
        final String identity=intent.getStringExtra("Identity");


        //비밀번호 재설정 버튼을 눌렀을 때의 동작 정의
        Button_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력한 새로운 비밀번호를 담기 위한 변수
                String newPassword=EditText_newpassword.getText().toString();
                //입력한 새로운 비밀번호 확인을 담기 위한 변수
                String checkNewPassword=EditText_checknewpassword.getText().toString();

                if(newPassword.equals("") || checkNewPassword.equals("")){
                    Toast.makeText(getApplicationContext(), "새로운 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    //새로운 비밀번호와 비밀번호 확인이 같은 경우에만 다음 과정으로 넘어갈 수 있도록 조건문 설정
                    if(newPassword.equals(checkNewPassword)){
                        //어떤 파일에 새로운 비밀번호를 덮어씌울 것인지 정하기 위해서 다음의 코드 사용
                        SharedPreferences sharedPreferencesId=getSharedPreferences(id,MODE_PRIVATE);
                        //파일을 정했으면 editor를 통해서 그 파일에 기존 key에 새로운 value를 넣기 위해 editor 선언
                        SharedPreferences.Editor editorId=sharedPreferencesId.edit();
                        //에디터를 통해서 기존에 있던 비밀번호 key에 새로운 비밀번호 데이터 입력
                        editorId.putString("Password",newPassword);
                        //변경사항 저장
                        editorId.commit();

                        //어떤 파일에 새로운 비밀번호를 덮어씌울 것인지 정하기 위해서 다음의 코드 사용
                        SharedPreferences sharedPreferencesNickname=getSharedPreferences(identity,MODE_PRIVATE);
                        //파일을 정했으면 editor를 통해서 그 파일에 기존 key에 새로운 value를 넣기 위해 editor 선언
                        SharedPreferences.Editor editorIdentity=sharedPreferencesNickname.edit();
                        //에디터를 통해서 기존에 있던 비밀번호 key에 새로운 비밀번호 데이터 입력
                        editorIdentity.putString("Password",newPassword);
                        //변경사항 저장
                        editorIdentity.commit();

                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();

                        Intent intent=new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        //비밀번호 재설정 액티비티는 한 번 호출되고 나가게 되면 더 이상 존재할 필요가 없기 때문에 이전 액티비티가 호출되면서 사라지게 함
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"비밀번호와 비밀번호 확인이 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChangePasswordActivity.this, LoginActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });



    }
}
