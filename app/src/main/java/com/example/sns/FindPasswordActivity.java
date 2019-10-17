package com.example.sns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPasswordActivity extends AppCompatActivity {

    EditText EditText_id, EditText_identitynumber;
    Button Button_findpassword, Button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);


        EditText_id=findViewById(R.id.EditText_id);
        EditText_identitynumber=findViewById(R.id.EditText_identitynumber);
        Button_findpassword=findViewById(R.id.Button_findpassword);
        Button_cancel=findViewById(R.id.Button_cancel);


        //비밀번호 찾기 버튼을 누를 시의 동작 정의
        Button_findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력한 아이디 값을 담아두기 위한 변수
                String id=EditText_id.getText().toString();
                //입력한 주민번호 뒷자리 값을 담아두기 위한 변수
                String identity=EditText_identitynumber.getText().toString();
                //아이디와 주민번호 뒷자리를 모두 입력했을 때만 다음 과정으로 넘어갈 수 있게 조건문 사용
                if(id.equals("") || identity.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디와 주민등록번호 뒷자리를 모두 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{//사용자의 아이디를 파일명으로 가지는 파일의 아이디 값이 존재하고 사용자의 주민번호 뒷자리를 파일명으로 가지는 파일의 주민번호 값이 존재하면 비밀번호 재설정 과정으로 넘어갈 수 있게 조건문 설정
                    if (getSharedPreferences(id, MODE_PRIVATE).getString("Id", null) != null && getSharedPreferences(identity, MODE_PRIVATE).getString("Identity", null) != null) {
                        Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordActivity.class);
                        //비밀번호 재설정 액티비티에서 사용자의 아이디와 주민번호를 파일명으로 가지는 파일의 데이터들을 수정하기 위해서는 사용자 아이디와 주민번호를 참조해야하기 때문에 인텐트로 해당 데이터들을 넘겨줌
                        intent.putExtra("Id", id);
                        intent.putExtra("Identity", identity);
                        startActivity(intent);
                        finish();
                    } else {//잘못된 아이디와 주민번호를 입력시에는 다음 과정으로 넘어가지 못하게 조건문 설정
                        Toast.makeText(getApplicationContext(), "입력환 아이디와 주민등록번호 뒷자리를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
