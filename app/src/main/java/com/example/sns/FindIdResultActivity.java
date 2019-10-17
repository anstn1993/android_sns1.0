package com.example.sns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FindIdResultActivity extends AppCompatActivity {

    Button Button_back;
    TextView TextView_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_result);


        TextView_id=findViewById(R.id.TextView_id);
        Button_back=findViewById(R.id.Button_back);

        //아이디 찾기 액티비티에서 인텐트로 넘겨준 사용자의 이름 값을 받기 위해서 인텐트 사용
        Intent intent=getIntent();
        //사용자의 이름 값을 담기 위한 변수 사용
        String name=intent.getStringExtra("Name");


        //사용자의 이름값으로 담아둔 변수를 sharedpreferences 파일 명에 대입하여 해당 파일의 아이디 값을 가져온 후 아이디를 출력해줄 텍스트뷰에 뿌려준다.
       TextView_id.setText(getSharedPreferences(name,MODE_PRIVATE).getString("Id",null));

        //로그인 화면으로 돌아가기 버튼을 누를 시에 동작 정의
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FindIdResultActivity.this, LoginActivity.class);
                //아이디 찾기 결과 액티비티는 한 번 호출되고 나가게 되면 더 이상 존재할 필요가 없기 때문에 이전 액티비티가 호출되면서 사라지게 함
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //이미 로그인 액티비티가 호출된 상태이기 때문에 재호출하기 위한 플래그 사용

                finish();

            }
        });

    }
}
