package com.example.sns;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.example.sns.LoginActivity.Nickname;


public class PostActivity extends AppCompatActivity {

    public TextView TextView_library,TextView_camera, TextView_upload, TextView_position, Button_cancel,Button_upload;
    public ImageView ImageView_image;
    public EditText EditText_text;
    ProgressBar ProgressBar_upload;
    public int Request_library=1000;
    public int Request_position=2000;
    public Bitmap bitmap;
    public Bundle bundle=new Bundle();
    public String mCurrentPhotoPath;
    public String galleryPhotoUri;
    public String profilePhotoUri;
    public static ArrayList<PostItem> postItemArrayList=new ArrayList<>();
    public static ArrayList<PostItem> postPictureItemArrayList=new ArrayList<>();
    public static ArrayList<PostItem> myPostArrayList=new ArrayList<>();
    public Uri bitmapUri;
    float latitude;
    float longitude;
    String address;

//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            TextView_upload.setVisibility(View.VISIBLE);
//            TextView_upload.setText("업로드 완료!");
//        }
//    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_copy);
        Log.d("OnCreate","호출됨");
        Button_cancel=findViewById(R.id.Button_cancel);
        TextView_camera=findViewById(R.id.TextView_camera);
        TextView_library=findViewById(R.id.TextView_library);
        ImageView_image=findViewById(R.id.ImageView_image);
        Button_upload=findViewById(R.id.Button_upload);
        EditText_text=findViewById(R.id.EditText_text);
        TextView_upload=findViewById(R.id.TextView_upload);
        ProgressBar_upload=findViewById(R.id.ProgressBar_upload);
        TextView_position=findViewById(R.id.TextView_position);

        Intent intent=getIntent();
        bundle=intent.getExtras();


//        String Nickname1=intent.getStringExtra("Nickname");
//        final String Nickname=intent.getStringExtra("Nickname");
//        final String Name=intent.getStringExtra("Name");
//        final String Id=intent.getStringExtra("Id");
//        String Text=intent.getStringExtra("Text");




        Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });



        //카메라와 라이브러리 접근시에 권한 요청
        requirePermission();








        //라이브러리로 접근할 시 동작 정의
        TextView_library.setClickable(true);
        TextView_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //읽기와 쓰기 권한 부여되었는지 확인
                boolean write=ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
                boolean read=ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
                //읽기, 쓰기 권한이 부여되었으면
                if(write && read){
                    //사진찍는 인텐트 코드 넣기
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,Request_library);

                }else{//권한이 부여되지 않았으면 경고 메시지 출력
                    Toast.makeText(PostActivity.this, "읽기/쓰기 권한 필요",Toast.LENGTH_SHORT).show();

                }
            }
        });




        //사진 버튼을 눌렀을 때 처리
        TextView_camera.setClickable(true);
        TextView_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //권한 부여되었는지 확인
                boolean camera=ContextCompat.checkSelfPermission(v.getContext(),Manifest.permission.CAMERA) ==PackageManager.PERMISSION_GRANTED;

                boolean write=ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;

                //권한이 부여되었으면
                if(camera && write){
                    //사진찍는 인텐트 코드 넣기
                    takePicture();

                }else{//권한이 부여되지 않았으면 경고 메시지 출력
                    Toast.makeText(PostActivity.this, "카메라 및 쓰기 권한 필요",Toast.LENGTH_SHORT).show();

                }
            }
        });







        //업로드 버튼을 누를 시 동작 정의
        Button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {

                        if(mCurrentPhotoPath!=null){//카메라를 통해서 이미지를 촬영했을 경우

//                            //게시글 입력란에 작성한 게시글을 담는 String 변수
//                            String Text = EditText_text.getText().toString();
//                            //게시물의 ArrayList의 크기를 위치로 설정

                            int position = postItemArrayList.size();
                            int position_grid=postPictureItemArrayList.size();



                             uploadPost(position,position_grid,mCurrentPhotoPath);


                            UploadProgressTask uploadProgressTask=new UploadProgressTask();
                            uploadProgressTask.execute();




                            //Sharedpreferences로 저장에 성공하기 전까진 사용하지 말자
//                            //메인 포스트 ArrayList를 문자열로 저장하기 위해 Gson라이브러리 사용
//                            Gson postGson=new Gson();
//                            Type postListType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//
//                            //Sharedpreferences를 통해 파일 생성
//                            SharedPreferences postSharedPreferences=getSharedPreferences(ID+"MainPost",MODE_PRIVATE);
//                            //에디터를 통해서 파일 안에 들어갈 데이터 담기
//                            SharedPreferences.Editor postEditor=postSharedPreferences.edit();
//
//                            //postItemArrayList를 문자열로 변환 후 저장
//                            String postList=postGson.toJson(postItemArrayList,postListType);
//                            postEditor.putString("PostArray",postList);
//                            postEditor.commit();
//
//                            Gson gridGson=new Gson();
//                            Type gridListType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//
//                            SharedPreferences gridSharedpreferences=getSharedPreferences(ID+"GridPost",MODE_PRIVATE);
//                            SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
//
//
//                            String gridList=gridGson.toJson(postPictureItemArrayList,gridListType);
//                            gridEditor.putString("GridArray",gridList);
//                            gridEditor.commit();


                        }else{//갤러리에서 이미지를 가져왔을 경우


//                            //게시글 입력란에 작성한 게시글을 담는 String 변수
//                            String Text = EditText_text.getText().toString();
//                            //게시물의 ArrayList의 크기를 위치로 설정
                            int position = postItemArrayList.size();
                            int position_grid=postPictureItemArrayList.size();
                            uploadPost(position, position_grid, bitmapUri.getPath());

                            //AsyncTask로 구현한 프로그레스 바와 진행률
                            UploadProgressTask uploadProgressTask=new UploadProgressTask();
                            uploadProgressTask.execute();



                            //Sharedpreferences로 저장에 성공하기 전까진 사용하지 말자

//                            Gson postGson=new Gson();
//                            Type postListType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//
//                            SharedPreferences postSharedPreferences=getSharedPreferences(ID+"MainPost",MODE_PRIVATE);
//                            SharedPreferences.Editor postEditor=postSharedPreferences.edit();
//
//                            String postList=postGson.toJson(postItemArrayList,postListType);
//                            postEditor.putString("PostArray",postList);
//                            postEditor.commit();
//
//                            Gson gridGson=new Gson();
//                            Type gridListType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//
//                            SharedPreferences gridSharedpreferences=getSharedPreferences(ID+"GridPost",MODE_PRIVATE);
//                            SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
//
//
//                            String gridList=gridGson.toJson(postPictureItemArrayList,gridListType);
//                            gridEditor.putString("GridArray",gridList);
//                            gridEditor.commit();
                        }


                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "사진을 올려주세요.", Toast.LENGTH_LONG).show();
                    }
            }
        });

        TextView_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostActivity.this, PlaceSearchActivity.class);
                startActivityForResult(intent, Request_position);
            }
        });

    }


    //카메라 실행시 권한을 요청하는 메소드
    private void requirePermission(){
        //요구할 퍼미션들의 배열 생성
        String[] permissions=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //허가되지 않은 퍼미션을 저장할 리스트 생성
        List<String> listPermissionNedded=new ArrayList<>();

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this, permission)== PackageManager.PERMISSION_DENIED){
                listPermissionNedded.add(permission);
            }
        }

        //허가되지 않은 퍼미션이 있을 경우
        if(!listPermissionNedded.isEmpty()){
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNedded.toArray(new String[listPermissionNedded.size()]),
                    1);

        }

    }

    private void takePicture(){
        //인텐트 생성
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            //파일 생성
            File photoFile = createImageFile();
            //Uri 객체로 변환
            Uri photoUri= FileProvider.getUriForFile(this,"com.haram.camera.fileprovider",photoFile);

            //인텐트에 인자로 전달
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            //카메라 실행
            startActivityForResult(intent,0);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException{
        //날짜와 시간에 대한 문자열
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //원하는 이름 포맷
        String imageFileName="JPEG_"+timestamp+"_";
        //디렉토리를 받아옴
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //파일 생성
        File image=File.createTempFile(
          imageFileName,".jpg",storageDir
        );

        //전역변수로 설정한 mCurrentPhotoPath에 경로를 저장
        mCurrentPhotoPath=image.getAbsolutePath();
        return image;
    }

     //파라미터의 requestCode는 startActivityForResult메소드의 파라미터로 전달된 requestCode가 들어가게 된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Request_library && resultCode==RESULT_OK && data != null){
            //선택한 사진의 uri를 가져옴
            Uri uri=data.getData();
            galleryPhotoUri=uri.toString();
            data.putExtra("GalleryPhotoUri",galleryPhotoUri);


            Cursor c = getContentResolver().query(uri, null,null,null,null);
            c.moveToNext();
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            bitmapUri = Uri.fromFile(new File(path));
            Log.e("URI", uri.toString());
            c.close();


                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize=4;
                //이미지 뷰에 내가 가져온 사진 넣어주기-decodeFile에 들어가는 첫번째 파라미터는 String형태의 Uri다.
                bitmap = BitmapFactory.decodeFile(bitmapUri.getPath(),options);
                ImageView_image.setRotation(90);//사진을 90도 회전
                ImageView_image.setImageBitmap(bitmap);
                //비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입



        }

        //startActivityForResult() 실행할 때 requestCode로 0을 대입함
        if(requestCode==0){

            //사진의 용량을 줄이기 위해서 비트맵 팩토리 옵션 사용
            BitmapFactory.Options options=new BitmapFactory.Options();
            //원래 크기의 1/8크기로 줄임
            options.inSampleSize=4;
            //ImageView 객체 생성
            ImageView_image=findViewById(R.id.ImageView_image);
//            Picasso.with(this).load(mCurrentPhotoPath).into(ImageView_image);
            ImageView_image.setRotation(90);//사진을 90도 회전
            //decodeFile의 파라미터로 카메라로 찍은 사진의 uri와 비트맵팩토리로 설정해준 옵션을 넣어서 사진 셋
            ImageView_image.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath,options));

        }

        if(requestCode==Request_position && resultCode==RESULT_OK){
            TextView_position.setText(data.getStringExtra("Address"));
            latitude=data.getFloatExtra("Latitude", 1000);
            longitude=data.getFloatExtra("Longitude",1000);
            address=data.getStringExtra("Address");
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        try{
            Log.d("넘어온 주소: ",address);
            Log.d("넘어온 위도: ", String.valueOf(latitude));
            Log.d("넘어온 경도: ", String.valueOf(longitude));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void uploadPost(int position,int position_grid, String uri){
        //게시글 입력란에 작성한 게시글을 담는 String 변수
        String Text = EditText_text.getText().toString();
        Log.d("게시글 라인 수: ", String.valueOf(EditText_text.getLineCount()));


        //메인 화면의 리사이클러뷰 어댑터 선언
        MainPostListAdapter mainPostListAdapter = new MainPostListAdapter(postItemArrayList, MainActivity.mainActivity);
        //계정 화면의 그리드 리사이클러뷰 어댑터 선언
        AccountPostGridAdapter accountPostGridAdapter = new AccountPostGridAdapter(postPictureItemArrayList);

        //데이트 객체 생성
        Date date=new Date();
        //데이트를 "yyyy-MM-dd HH:mm:ss"형식으로 포맷하는 SimpleDateFormat객체 생성
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //위에서 만든 simpleDateFormat인스턴스를 format()메소드를 통해 미리 만들어둔 date를 스트링형으로 포맷한 후
        //스트링 변수(uploadDate)에 넣어준다.
        //참고로 이때 simpleDateFormat객체에서 format()은 date를 string형으로 변환하는 것이고 parse()를 사용하면 string형으로 저장된 date를
        //date타입으로 변환시켜준다. format()을 toString()이라고 생각하면 된다.
        String uploadDate=simpleDateFormat.format(date);

        //프로필 사진이 설정되지 않은 경우
        if(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)==null){


            Uri defaultProfileUri = Uri.parse("android.resource://"+getPackageName()+"/drawable/circle");


            //메인 화면의 리사이클러뷰의 ArrayList 추가(리사이클러뷰에 실제로 아이템 추가)
            postItemArrayList.add(new PostItem(
                    defaultProfileUri.getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    0,
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    false,
                    position,
                    true,
                    latitude,
                    longitude,
                    address,
                    uploadDate,
                    EditText_text.getLineCount()));
            //postItemArrayList를 저장하기 위한 메소드
            savePostArray(PostActivity.this);

            //계정 화면의 그리드 리사이클러뷰의 ArrayList 추가(리사이클러뷰에 실제로 아이템 추가)
            postPictureItemArrayList.add(new PostItem(
                    defaultProfileUri.getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    0,
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    false,
                    position_grid,
                    true,
                    latitude,
                    longitude,
                    address,
                    uploadDate,
                    EditText_text.getLineCount()));
            saveGridPostArray(PostActivity.this,Nickname);
        }
        //프로필사진을 설정한 경우
        else{
            //메인 화면의 리사이클러뷰의 ArrayList 추가(리사이클러뷰에 실제로 아이템 추가)
            postItemArrayList.add(new PostItem(
                    Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    0,
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    false,
                    position,
                    true,
                    latitude,
                    longitude,
                    address,
                    uploadDate,
                    EditText_text.getLineCount()));
            //postItemArrayList를 저장하기 위한 메소드
            savePostArray(PostActivity.this);

            //계정 화면의 그리드 리사이클러뷰의 ArrayList 추가(리사이클러뷰에 실제로 아이템 추가)
            postPictureItemArrayList.add(new PostItem(
                    Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    0,
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    false,
                    position_grid,
                    true,
                    latitude,
                    longitude,
                    address,
                    uploadDate,
                    EditText_text.getLineCount()));
            saveGridPostArray(PostActivity.this,Nickname);
        }


        MainActivity.clearMyPostArray(PostActivity.this);
        MainActivity.fillMyPostArray(PostActivity.this);

        //아이템이 추가될 때의 효과
        mainPostListAdapter.notifyItemInserted(position);
        mainPostListAdapter.notifyItemRangeChanged(position, postItemArrayList.size());
        accountPostGridAdapter.notifyItemInserted(position_grid);
        accountPostGridAdapter.notifyItemRangeChanged(position_grid, postPictureItemArrayList.size());
    }


    public static void savePostArray(Context context){
        //게시글 입력란에 작성한 게시글을 담는 String 변수
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MainPost",MODE_PRIVATE);
        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
        postEditor.putInt("PostSize",postItemArrayList.size());


        for(int i=0; i<postItemArrayList.size(); i++){
            postEditor.putString("PostProfile"+i,postItemArrayList.get(i).profilePicutreUri);
            postEditor.putString("PostPicture"+i,postItemArrayList.get(i).postPictureUri);
            postEditor.putInt("PostLike"+i,postItemArrayList.get(i).likeID);
            postEditor.putInt("PostComment"+i,postItemArrayList.get(i).commentID);
            postEditor.putInt("PostCommentCount"+i,postItemArrayList.get(i).commentCount);
            postEditor.putInt("PostMore"+i,postItemArrayList.get(i).moreID);
            postEditor.putString("PostNickname"+i,postItemArrayList.get(i).nickname);
            postEditor.putString("PostArticle"+i,postItemArrayList.get(i).article);
            postEditor.putBoolean("PostLikeState"+i,postItemArrayList.get(i).likeState);
            postEditor.putInt("PostIndex"+i,postItemArrayList.get(i).index);
            postEditor.putBoolean("PostFocusState" + i, postItemArrayList.get(i).focusState);
            postEditor.putFloat("PostLatitude"+i, postItemArrayList.get(i).latitude);
            postEditor.putFloat("PostLongitude"+i, postItemArrayList.get(i).longitude);
            postEditor.putString("PostAddress"+i,postItemArrayList.get(i).address);
            postEditor.putString("PostTime"+i,postItemArrayList.get(i).date);
            postEditor.putInt("PostArticleLine"+i,postItemArrayList.get(i).textLine);
        }

        postEditor.commit();
    }

    public static void loadPostArray(Context context){
        SharedPreferences postSharedPreferences=context.getSharedPreferences("MainPost",MODE_PRIVATE);
        postItemArrayList.clear();
        int postSize=postSharedPreferences.getInt("PostSize",0);
        Log.d("메인포스트 사이즈: ", String.valueOf(postSize));

        for(int i=0; i<postSize; i++){
            postItemArrayList.add(new PostItem(
                    postSharedPreferences.getString("PostProfile"+i,null),
                    postSharedPreferences.getString("PostPicture"+i,null),
                    postSharedPreferences.getInt("PostLike"+i,0),
                    postSharedPreferences.getInt("PostComment"+i,0),
                    postSharedPreferences.getInt("PostCommentCount"+i,0),
                    postSharedPreferences.getInt("PostMore"+i,0),
                    postSharedPreferences.getString("PostNickname"+i,null),
                    postSharedPreferences.getString("PostArticle"+i,null),
                    postSharedPreferences.getBoolean("PostLikeState"+i,false),
                    postSharedPreferences.getInt("PostIndex"+i,0),
                    postSharedPreferences.getBoolean("PostFocusState"+i,true),
                    postSharedPreferences.getFloat("PostLatitude"+i,1000),
                    postSharedPreferences.getFloat("PostLongitude"+i,1000),
                    postSharedPreferences.getString("PostAddress"+i,null),
                    postSharedPreferences.getString("PostTime"+i,null),
                    postSharedPreferences.getInt("PostArticleLine"+i,0)
                    ));
            Log.d("포커스 상태",String.valueOf(postSharedPreferences.getBoolean("PostLikeState"+i,false)));
        }
    }


    public static void saveGridPostArray(Context context, String nickname){
        SharedPreferences gridSharedpreferences=context.getSharedPreferences(nickname+"GridPost",MODE_PRIVATE);
        SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
        gridEditor.putInt("GridPostSize",postPictureItemArrayList.size());

        for(int i=0; i<postPictureItemArrayList.size(); i++){
            gridEditor.putString("PostProfile"+i,postPictureItemArrayList.get(i).profilePicutreUri);
            gridEditor.putString("PostPicture"+i,postPictureItemArrayList.get(i).postPictureUri);
            gridEditor.putInt("PostLike"+i,postPictureItemArrayList.get(i).likeID);
            gridEditor.putInt("PostComment"+i,postPictureItemArrayList.get(i).commentID);
            gridEditor.putInt("PostCommentCount"+i,postPictureItemArrayList.get(i).commentCount);
            gridEditor.putInt("PostMore"+i,postPictureItemArrayList.get(i).moreID);
            gridEditor.putString("PostNickname"+i,postPictureItemArrayList.get(i).nickname);
            gridEditor.putString("PostArticle"+i,postPictureItemArrayList.get(i).article);
            gridEditor.putBoolean("PostLikeState"+i,postPictureItemArrayList.get(i).likeState);
            gridEditor.putInt("PostIndex"+i,postPictureItemArrayList.get(i).index);
            gridEditor.putBoolean("PostFocusState" + i, postItemArrayList.get(i).focusState);
            gridEditor.putFloat("PostLatitude"+i, postPictureItemArrayList.get(i).latitude);
            gridEditor.putFloat("PostLongitude"+i, postPictureItemArrayList.get(i).longitude);
            gridEditor.putString("PostAddress"+i,postPictureItemArrayList.get(i).address);
            gridEditor.putString("PostTime"+i,postPictureItemArrayList.get(i).date);
            gridEditor.putInt("PostArticleLine"+i,postPictureItemArrayList.get(i).textLine);

        }

        gridEditor.commit();
    }

    public static void loadGridPostArray(Context context,String nickname){
        SharedPreferences postSharedPreferences=context.getSharedPreferences(nickname+"GridPost",MODE_PRIVATE);
        postPictureItemArrayList.clear();
        int postSize=postSharedPreferences.getInt("GridPostSize",0);

        for(int i=0; i<postSize; i++){
            postPictureItemArrayList.add(new PostItem(
                    postSharedPreferences.getString("PostProfile"+i,null),
                    postSharedPreferences.getString("PostPicture"+i,null),
                    postSharedPreferences.getInt("PostLike"+i,0),
                    postSharedPreferences.getInt("PostComment"+i,0),
                    postSharedPreferences.getInt("PostCommentCount"+i,0),
                    postSharedPreferences.getInt("PostMore"+i,0),
                    postSharedPreferences.getString("PostNickname"+i,null),
                    postSharedPreferences.getString("PostArticle"+i,null),
                    postSharedPreferences.getBoolean("PostLikeState"+i,false),
                    postSharedPreferences.getInt("PostIndex"+i,0),
                    postSharedPreferences.getBoolean("PostFocusState"+i,true),
                    postSharedPreferences.getFloat("PostLatitude"+i,1000),
                    postSharedPreferences.getFloat("PostLongitude"+i,1000),
                    postSharedPreferences.getString("PostAddress"+i,null),
                    postSharedPreferences.getString("PostTime"+i,null),
                    postSharedPreferences.getInt("PostArticleLine"+i,0)
            ));
        }
    }




    //게시물을 업로드할 때 프로그레스 바와 함께 업로드율을 텍스트로 표시해주고, 완료가 되면 완료 문구가 뜨게 하고
    //메인 액티비티로 intent하기 위한 ASyncTask
    class UploadProgressTask extends AsyncTask<Integer, Integer, Integer> {
        //백그라운드 스레드가 실행되기 전에 ui스레드에서 처리할 작업을 이 메소드로 구현하면 된다.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar_upload.setVisibility(View.VISIBLE);
            TextView_upload.setVisibility(View.VISIBLE);
        }

        //onPreExecute메소드가 다 실행된 후 실행되는 메소드로 백그라운드 스레드로 구현하고자 하는 기능을 구현하면 된다.
        @Override
        protected Integer doInBackground(Integer... integers) {
            //프로그레스 바가 0부터 100까지 차는 작업을 구현한다.
            //이때 publishProgress(i)메소드를 통해서 onProgressUpdate메소드를 호출
            for(int i=0; i<=100; i++){
                ProgressBar_upload.setProgress(i);
                publishProgress(i);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        //publishProgress메소드를 통해서 호출된 메소드로 이 메소드를 통해서 백그라운드 작업과 함께 연동시키고 싶은 UI의 변화를
        //구현하면 된다.
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //progress바가 참과 동시에 진행률을 보여주는 뷰의 숫자가 함께 증가하게끔 설정
            TextView_upload.setText(values[0].intValue()+"%");

        }

        //doInBackground메소드가 다 실행된 후 실행되는 메소드로 다 실행된 후에는 완료라는 문자와 함께
        //메인 액티비티로 이동을 시킨다.
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            TextView_upload.setText("업로드 완료!");

            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }


}
