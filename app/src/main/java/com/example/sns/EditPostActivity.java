package com.example.sns;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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
import static com.example.sns.MainActivity.clearMyPostArray;
import static com.example.sns.MainActivity.fillMyPostArray;
import static com.example.sns.PostActivity.postItemArrayList;
import static com.example.sns.PostActivity.postPictureItemArrayList;


public class EditPostActivity extends AppCompatActivity {
    ProgressBar ProgressBar_upload;
    TextView TextView_library,TextView_camera, TextView_upload, TextView_position,Button_cancel, Button_edit;
    ImageView ImageView_image;
    EditText EditText_text;
    int Request_library=1000;
    int Request_position=2000;
    Bitmap bitmap;
    Bundle bundle=new Bundle();
    Uri uri;
    String mCurrentPhotoPath;
    String galleryPhotoUri;
    public Uri bitmapUri;
    float latitude;
    float longitude;
    String address;


//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            TextView_upload.setVisibility(View.VISIBLE);
//            TextView_upload.setText("수정 완료!");
//        }
//    };








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Button_cancel=findViewById(R.id.Button_cancel);
        TextView_camera=findViewById(R.id.TextView_camera);
        TextView_library=findViewById(R.id.TextView_library);
        ImageView_image=findViewById(R.id.ImageView_image);
        Button_edit=findViewById(R.id.Button_edit);
        EditText_text=findViewById(R.id.EditText_text);
        ProgressBar_upload=findViewById(R.id.ProgressBar_upload);
        TextView_upload=findViewById(R.id.TextView_upload);
        TextView_position=findViewById(R.id.TextView_position);


        Intent intent=getIntent();
        bundle=intent.getExtras();

        address=bundle.getString("PostAddress");
        if(address!=null){
            TextView_position.setText(address);
        }

        latitude=bundle.getFloat("PostLatitude");

        longitude=bundle.getFloat("PostLongitude");

        String article=bundle.getString("PostArticle");
        EditText_text.setText(article);

        final String postPicture=bundle.getString("PostPicture");
        ImageView_image.setRotation(90);
        ImageView_image.setImageBitmap(BitmapFactory.decodeFile(postPicture));

        PostActivity.loadPostArray(this);
        PostActivity.loadGridPostArray(this, Nickname);

        MainActivity.clearMyPostArray(EditPostActivity.this);
        MainActivity.fillMyPostArray(EditPostActivity.this);




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
                    Toast.makeText(EditPostActivity.this, "읽기/쓰기 권한 필요",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(EditPostActivity.this, "카메라 및 쓰기 권한 필요",Toast.LENGTH_SHORT).show();

                }
            }
        });

        //수정 버튼을 누를 시 동작 정의
        Button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = bundle.getInt("Position");

                try {

                    //수정 버튼을 눌렀을 때 카메라로 찍은 게시물을 수정하는 경우
                    if(mCurrentPhotoPath!=null){

                        editPost(position, mCurrentPhotoPath);

                        EditProgressTask editProgressTask=new EditProgressTask();
                        editProgressTask.execute();
//                        Gson postGson=new Gson();
//                        Type postListType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//
//                        SharedPreferences postSharedPreferences=getSharedPreferences(ID+"MainPost",MODE_PRIVATE);
//                        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
//
//                        String postList=postGson.toJson(postItemArrayList1,postListType);
//                        postEditor.putString("PostArray",postList);
//                        postEditor.commit();
//
//                        Gson gridGson=new Gson();
//                        Type gridListType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//
//                        SharedPreferences gridSharedpreferences=getSharedPreferences(ID+"GridPost",MODE_PRIVATE);
//                        SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
//
//
//                        String gridList=gridGson.toJson(postPictureItemArrayList1,gridListType);
//                        gridEditor.putString("GridArray",gridList);
//                        gridEditor.commit();

                    //그냥 사진은 수정하지 않는 경우
                    }else if(bitmapUri!=null){

                        editPost(position, bitmapUri.getPath());
                        EditProgressTask editProgressTask=new EditProgressTask();
                        editProgressTask.execute();


//                        Gson postGson=new Gson();
//                        Type postListType = new TypeToken<ArrayList<PostItem>>() {}.getType();
//
//                        SharedPreferences postSharedPreferences=getSharedPreferences(ID+"MainPost",MODE_PRIVATE);
//                        SharedPreferences.Editor postEditor=postSharedPreferences.edit();
//
//                        String postList=postGson.toJson(postItemArrayList1,postListType);
//                        postEditor.putString("PostArray",postList);
//                        postEditor.commit();
//
//                        Gson gridGson=new Gson();
//                        Type gridListType = new TypeToken<ArrayList<PostPictureItem>>() {}.getType();
//
//                        SharedPreferences gridSharedpreferences=getSharedPreferences(ID+"GridPost",MODE_PRIVATE);
//                        SharedPreferences.Editor gridEditor=gridSharedpreferences.edit();
//
//
//                        String gridList=gridGson.toJson(postPictureItemArrayList1,gridListType);
//                        gridEditor.putString("GridArray",gridList);
//                        gridEditor.commit();

                    }else{
                        String postPicture=bundle.getString("PostPicture");
                        editPost(position, postPicture);
                        EditProgressTask editProgressTask=new EditProgressTask();
                        editProgressTask.execute();

                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "사진을 올려주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditPostActivity.this, PlaceSearchActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_library && resultCode == RESULT_OK && data != null) {
            //사진이 정상적으로 선택된 경우
            uri = data.getData();

            galleryPhotoUri = uri.toString();


            Cursor c = getContentResolver().query(uri, null, null, null, null);
            c.moveToNext();
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            bitmapUri = Uri.fromFile(new File(path));
            Log.e("URI", uri.toString());
            c.close();


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            //이미지 뷰에 내가 가져온 사진 넣어주기
            bitmap = BitmapFactory.decodeFile(bitmapUri.getPath(), options);
            ImageView_image.setRotation(90);//사진을 90도 회전
            ImageView_image.setImageBitmap(bitmap);
            //비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입


            //            try {//사진을 비트맵으로 얻기
//                //이미지 뷰에 내가 가져온 사진 넣어주기
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri를 뽑아오게 해준다.
//                ImageView_image.setRotation(90);//사진을 90도 회전
//                ImageView_image.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//
        }

            //startActivityForResult() 실행할 때 requestCode로 0을 대입함
            if (requestCode == 0) {
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

    public void editPost(int position, String uri){
        String Text = EditText_text.getText().toString();
        MainPostListAdapter mainPostListAdapter = new MainPostListAdapter(postItemArrayList, MainActivity.mainActivity);
        AccountPostGridAdapter accountPostGridAdapter = new AccountPostGridAdapter(postPictureItemArrayList);

        if(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)==null){
            Uri defaultProfileUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.circle) + '/' + getResources().getResourceTypeName(R.drawable.circle) + '/' + getResources().getResourceEntryName(R.drawable.circle));
            //선택된 인덱스의 포스트의 데이터를 수정
            postItemArrayList.set(position,new PostItem(
                    defaultProfileUri.getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+position,0),
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    getSharedPreferences(Nickname+"MainPost",MODE_PRIVATE).getBoolean("PostLikeState"+position,false),
                    position,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,false),
                    latitude,
                    longitude,
                    address,
                    getSharedPreferences("MainPost", MODE_PRIVATE).getString("PostTime"+position,null),
                    EditText_text.getLineCount()
            ));

            //수정된 내용 저장
            PostActivity.savePostArray(EditPostActivity.this);


            //선택된 인덱스의 그리드 포스트 데이터 수정
            Log.d("수정하는 그리드 게시물의 인덱스",String.valueOf(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
            postPictureItemArrayList.set(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0),new PostItem(
                    defaultProfileUri.getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+position,0),
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    getSharedPreferences(Nickname+"MainPost",MODE_PRIVATE).getBoolean("PostLikeState"+position,false),
                    getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0),
                    getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,false),
                    latitude,
                    longitude,
                    address,
                    getSharedPreferences("MainPost", MODE_PRIVATE).getString("PostTime"+position,null),
                    EditText_text.getLineCount()
            ));

            //수정된 내용 저장
            PostActivity.saveGridPostArray(EditPostActivity.this,Nickname);
        }else{
            //선택된 인덱스의 포스트의 데이터를 수정
            postItemArrayList.set(position,new PostItem(
                    Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+position,0),
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    getSharedPreferences(Nickname+"MainPost",MODE_PRIVATE).getBoolean("PostLikeState"+position,false),
                    position,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,false),
                    latitude,
                    longitude,
                    address,
                    getSharedPreferences("MainPost", MODE_PRIVATE).getString("PostTime"+position,null),
                    EditText_text.getLineCount()
            ));

            //수정된 내용 저장
            PostActivity.savePostArray(EditPostActivity.this);


            //선택된 인덱스의 그리드 포스트 데이터 수정
            Log.d("수정하는 그리드 게시물의 인덱스",String.valueOf(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0)));
            postPictureItemArrayList.set(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0),new PostItem(
                    Uri.parse(getSharedPreferences(Nickname,MODE_PRIVATE).getString("ProfileUri",null)).getPath(),
                    uri,
                    R.drawable.like,
                    R.drawable.comment,
                    getSharedPreferences("MainPost",MODE_PRIVATE).getInt("PostCommentCount"+position,0),
                    R.drawable.more,
                    getSharedPreferences(Nickname,MODE_PRIVATE).getString("Nickname",null),
                    Text,
                    getSharedPreferences(Nickname+"MainPost",MODE_PRIVATE).getBoolean("PostLikeState"+position,false),
                    getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0),
                    getSharedPreferences("MainPost",MODE_PRIVATE).getBoolean("PostFocusState"+position,false),
                    latitude,
                    longitude,
                    address,
                    getSharedPreferences("MainPost", MODE_PRIVATE).getString("PostTime"+position,null),
                    EditText_text.getLineCount()
            ));

            //수정된 내용 저장
            PostActivity.saveGridPostArray(EditPostActivity.this,Nickname);

        }

        clearMyPostArray(EditPostActivity.this);
        //변화된 나의 포스트 Array를 저장
        fillMyPostArray(EditPostActivity.this);

        mainPostListAdapter.notifyItemChanged(position);
        accountPostGridAdapter.notifyItemChanged(getSharedPreferences("MyPost",MODE_PRIVATE).getInt("PostIndex"+position,0));
    }

    class EditProgressTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar_upload.setVisibility(View.VISIBLE);
            TextView_upload.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            TextView_upload.setText(values[0].intValue()+"%");

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            TextView_upload.setText("수정 완료!");

            Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }




    }
}





