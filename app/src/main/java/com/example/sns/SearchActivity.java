package com.example.sns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import static com.example.sns.LoginActivity.Nickname;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchRecyclerViewClickListner{
    ImageButton Button_home;
    ImageButton Button_post;
    ImageButton Button_notification;
    ImageButton Button_account;
    EditText EditText_search;
    Bundle bundle;
    RecyclerView RecyclerView_search_list;

    RecyclerView.LayoutManager layoutManager;

    SearchAdapter searchAdapter;


    //검색목록을 저장할 어레이 리스트
    public static ArrayList<SearchItem> searchItemArrayList=new ArrayList<>();
    //실제로 검색을 할 때 사용자에게 검색 결과를 보여주고 목록을 클릭했을 때 해당 사용자의 계정으로 넘기기 위해 따로 설정
    public static ArrayList<SearchItem> searchedItemArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Button_home=findViewById(R.id.Button_home);
        Button_post=findViewById(R.id.Button_post);
        Button_notification=findViewById(R.id.Button_notification);
        Button_account=findViewById(R.id.Button_account);
        EditText_search=findViewById(R.id.EditText_search);

        Intent intent=getIntent();
        bundle=intent.getExtras();
//        final String Nickname=intent.getStringExtra("Nickname");
//        final String Name=intent.getStringExtra("Name");
//        final String Id=intent.getStringExtra("Id");

        //회원가입을 할 때 저장한 검색 Arraylist를 searchedItemArrayList에 넣어주기 위해서 로드
        JoinActivity.loadSearchArray(SearchActivity.this);

        //해당 조건문을 쓰지 않으니 검색 액티비티에 들어갈 때마다 계속 사용자 목록이 쌓이는 문제가 발생했다.
        //searchedItemArrayList에 계속해서 searchItemArrayList가 추가되어 쌓이는 문제를 막기 위해서
        //조건문으로 searchedItemArrayList가 비어있는 최초에만 searchItemArrayList를 추가
        if(searchedItemArrayList.size()==0){
            searchedItemArrayList.addAll(searchItemArrayList);
            JoinActivity.saveSearchedArray(this);
        }

        setRecyclerView();







        //홈버튼을 누르면 검색 액티비티에서 홈액티비티로 이동
        Button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this, MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //포스트 버튼을 누르면 검색 액티비티에서 포스트 액티비티로 이동
        Button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this, PostActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //알림 버튼을 누르면 검색 액티비티에서 알림 액티비티로 이동
        Button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this, NotificationActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //계정 버튼을 누르면 검색 액티비티에서 계정 액티비티로 이동
        Button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //검색어가 추가될 때 감지하는 리스너를 달아서 밑의 메소드들이 실행되도록 설정
        EditText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //검색창의 텍스트가 변하고 난 이후의 동작을 정의해주면 된다.
            //이 메소드는 문자가 입력될 때마다 호출된다.
            @Override
            public void afterTextChanged(Editable s) {
                //문자가 입력될 때마다 문자를 text변수에 담아서 search메소드로 보내서 동작을 실행시킨다.
                String text=EditText_search.getText().toString();
                search(text);

            }
        });



    }

    //재사용되는 액티비티에 인텐트를 넘겨받기 위해서 필요한 메소드
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        bundle=intent.getExtras();
        //게시물의 텍스트를 담는 변수


    }

    private void setRecyclerView() {
        RecyclerView_search_list=findViewById(R.id.RecyclerView_search_list);
        RecyclerView_search_list.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        RecyclerView_search_list.setLayoutManager(layoutManager);

        //searchItemArrayList는 전체 가입자 목록을 저장하고 있어야 하기 때문에
        //검색을 했을 때 유동적으로 index가 바뀔 수 없게 되기 때문에 searchedItemArrayList로 어댑터에 연결
        searchAdapter=new SearchAdapter(searchedItemArrayList);
        searchAdapter.setOnClickListener(SearchActivity.this);
        RecyclerView_search_list.setAdapter(searchAdapter);
    }

//    public void setSearchedRecyclerView(){
//        RecyclerView_search_list=findViewById(R.id.RecyclerView_search_list);
//        RecyclerView_search_list.setHasFixedSize(true);
//
//        layoutManager=new LinearLayoutManager(this);
//        RecyclerView_search_list.setLayoutManager(layoutManager);
//
//        //회원가입을 할 때 저장한 검색 Arraylist를 검색 액티비티에서 로드해주기 위한 메소드
//        JoinActivity.loadSearchArray(SearchActivity.this);
//        searchAdapter=new SearchAdapter(searchItemArrayList);
//        searchAdapter.setOnClickListener(SearchActivity.this);
//        RecyclerView_search_list.setAdapter(searchAdapter);
//    }



    public void search(String text){
        //문자를 입력할 때마다 모두 지우고 새로 뿌려주기 위해서 먼저 ArrayList를 매번 비워준다.
        searchedItemArrayList.clear();

        //아직 문자가 입력되지 않았으면 모든 리스트를 다 로드해서 보여준다.
        if(text.length()==0){

           searchedItemArrayList.addAll(searchItemArrayList);
           JoinActivity.saveSearchedArray(this);
        }
        //문자를 입력하기 시작하는 경우
        else{
            //리스트의 모든 인덱스를 돌면서 문자를 검색한다. seachItemArrayList에 모든 회원목록이 들어가기 때문에 이 리스트를 검색 기준으로 삼아야 한다.
            for(int i=0; i<searchItemArrayList.size(); i++){
                //저장되어 있는 이름이나 닉네임이 입력한 문자를 포함하고 있으면 true를 반환해 다음의 동작을 실행시킨다.
                if(searchItemArrayList.get(i).nickname.toLowerCase().contains(text) || searchItemArrayList.get(i).name.toLowerCase().contains(text)){
                    //검색어가 전체 목록의 데이터의 문자열에 포함되면 searchedItemArrayList에 해당 리스트의 데이터를 추가해주고 저장하여
                    //계속해서 검색에 나온 리스트의 사이즈와 인덱스를 갱신한다.
                    searchedItemArrayList.add(new SearchItem(searchItemArrayList.get(i).nickname,searchItemArrayList.get(i).name));
                    JoinActivity.saveSearchedArray(this);
                }
            }
        }
        //리스트 데이터가 변경되었기 때문에 어댑터를 갱신해 검색된 리스트만 화면에 보여준다.
        searchAdapter.notifyDataSetChanged();


    }
    //검색 리스트의 아이템을 클릭하면 해당 유저의 계정 액티비티로 이동되는 메소드다.
    @Override
    public void onListClicked(int position) {
        //검색과정에서 저장한 검색결과 리스트를 불러와서 리스트의 인덱스에 맞는 닉네임을 가진 유저의 계정 액티비티로 이동하게끔 함
        //만약 loadSearchArray를 로드하면 검색 전에 전체 목록이 보이는 상태에서 아이템을 클릭하면 해당 닉네임으로 연결이 되지만
        //검색을 시작하면 검색결과의 인덱스와 전체 리스트의 아이템이 불일치하기 때문에 검색후 아이템을 클릭하면 해당 닉네임으로 연결되지 않고
        //엉뚱한 닉네임이 참조됨.
        JoinActivity.loadSearchedArray(this);

        //현재 로그인한 유저의 닉네임이 검색리스트에서 클릭한 닉네임과 일치하는 경우에는 자신의 계정 액티비티로 이동
        if(Nickname.equals(getSharedPreferences("SearchedList",MODE_PRIVATE).getString("Nickname"+position,null))){
            Intent intent = new Intent(SearchActivity.this, AccountActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);

            //현재 로그인한 유저의 닉네임과 검색리스트에서 클릭한 닉네임이 일치하지 않는 경우에는 다른 유저의 계정 액티비티로 이동
        }else{
            Intent intent=new Intent(SearchActivity.this, AccountOthersActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            //clear top플래그가 호출되면 이전 액티비티가 최상단에 위치하게 되기 때문에 그 액티비티를 재활용
            intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle=new Bundle();
            bundle.putString("PostNickname", getSharedPreferences("SearchedList",MODE_PRIVATE).getString("Nickname"+position,null));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
