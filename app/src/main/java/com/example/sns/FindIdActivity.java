package com.example.sns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindIdActivity extends AppCompatActivity {
    EditText EditText_name, EditText_identitynumber;
    Button Button_findid, Button_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        EditText_name=findViewById(R.id.EditText_name);
        EditText_identitynumber=findViewById(R.id.EditText_identitynumber);
        Button_findid=findViewById(R.id.Button_findid);
        Button_cancel=findViewById(R.id.Button_cancel);

        //아이디 찾기 버튼을 누를 때의 동작 정의
        Button_findid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력한 이름을 담아두기 위한 변수
                String name=EditText_name.getText().toString();
                //입력한 주민등록번호 뒷자리를 담아두기 위한 변수
                String identity=EditText_identitynumber.getText().toString();
                //둘 다 입력했을 때만 다음 단계로 넘어갈 수 있게끔 조건문을 사용
                if(name.equals("") || identity.equals("")) {
                    Toast.makeText(getApplicationContext(), "이름과 주민등록번호 뒷자리를 모두 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{//사용자의 이름을 파일명으로 지정한 파일의 이름값이 존재하고, 사용자의 주민번호 뒷자리를 파일명으로 지정한 파일의 주민번호 뒷자리가 존재하는 경우에만 아이디를 찾을 수 있게 조건문 사용
                    if (getSharedPreferences(name, MODE_PRIVATE).getString("Name", null) != null && getSharedPreferences(identity, MODE_PRIVATE).getString("Identity", null) != null) {
                        Intent intent = new Intent(FindIdActivity.this, FindIdResultActivity.class);
                        //아이디 찾기 결과 액티비티에서 사용자 이름으로 된 파일명을 참조하기 위해서는 사용자 이름이 다음 액티비티에서 필요하기 때문에 인텐트로 사용자 이름을 넘겨줌
                        intent.putExtra("Name", name);
                        intent.putExtra("Identity", identity);
                        startActivity(intent);
                        finish();
                    } else {//저장된 이름과 주민번호가 아닌 경우
                        Toast.makeText(getApplicationContext(), "입력한 이름과 주민등록번호 뒷자리를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //취소버튼을 누르면 해당 액티비티 종료
        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
