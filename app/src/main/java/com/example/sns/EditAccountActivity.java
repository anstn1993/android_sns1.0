package com.example.sns;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sns.LoginActivity.Nickname;

public class EditAccountActivity extends AppCompatActivity {
    ImageView ImageView_apply, ImageView_cancel;
    CircleImageView CircleImageView_editprofile;
    TextView TextView_editprofile;
    TextInputEditText TextInputEditText_name, TextInputEditText_statemessage;
    Bundle bundle;
    int Request_library = 1000;

    Bitmap bitmap;
    public static String profileUri;
    public String galleryPhotoUri;
    public String mCurrentPhotoPath;
    public Uri bitmapUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        //적용 버튼 뷰를 객체와 연동
        ImageView_apply = findViewById(R.id.ImageView_apply);
        //취소 버튼 뷰를 객체와 연동
        ImageView_cancel = findViewById(R.id.ImageView_cancel);
        //프로필 편집 버튼 뷰를 객체와 연동
        CircleImageView_editprofile = findViewById(R.id.CircleImageView_editprofile);
        //프로필 편집 텍스트 뷰를 객체와 연동
        TextView_editprofile = findViewById(R.id.TextView_editprofile);
        //이름 입력 뷰를 객체와 연동
        TextInputEditText_name = findViewById(R.id.TextInputEditText_name);
        //닉네임 입력 뷰를 객체와 연동
        TextInputEditText_statemessage = findViewById(R.id.TextInputEditText_statemessage);


        try {//사진을 비트맵으로 얻기
            //이미지 뷰에 내가 가져온 사진 넣어주기
            Uri uri = Uri.parse(getSharedPreferences(Nickname, MODE_PRIVATE).getString("ProfileUri", null));
//            Picasso.with(this).load(uri).into(CircleImageView_editprofile);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;

            bitmap = BitmapFactory.decodeFile(uri.getPath(), options);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri 이미지를 뽑아오게 해준다.
            CircleImageView_editprofile.setRotation(90);//사진을 90도 회전
            CircleImageView_editprofile.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        String name = getSharedPreferences(Nickname, MODE_PRIVATE).getString("Name", null);
        String stateMessage = getSharedPreferences(Nickname, MODE_PRIVATE).getString("StateMessage", null);


        //이름 입력 뷰에 번들의 이름 데이터 입력
        TextInputEditText_name.setText(name);
        //닉네임 입력 뷰에 번들의 닉네임 데이터 입력
        TextInputEditText_statemessage.setText(stateMessage);


        //취소 버튼을 누를 시 동작 정의
        ImageView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        //적용 버튼을 누를 시 동작 정의
        ImageView_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이름칸에 입력된 텍스트
                String name = TextInputEditText_name.getText().toString();
                //상태 메세지에 입력된 텍스트
                String nickname = TextInputEditText_statemessage.getText().toString();
                //닉네임 파일에 값들을 저장시킨다.
                SharedPreferences sharedPreferences = getSharedPreferences(Nickname, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Name", name);
                editor.putString("StateMessage", nickname);
                editor.putString("ProfileUri", profileUri);
                editor.commit();

                //프로필 사진을 수정할 시 기존에 업로드된 게시물의 프로필 사진도 함께 수정되어야 하는데 수정되지 않았음.
                //그 문제를 해결하기 위해 다음과 같은 코드를 삽입했다.
                //반복문을 돌려서 나의 닉네임과 게시물에 저장된 닉네임이 같은 경우 그 게시물의 프로필 사진만 내가 바꾼 사진으로 바뀌게끔 설정
                SharedPreferences postSharedpreferences = getSharedPreferences("MainPost", MODE_PRIVATE);
                SharedPreferences.Editor postEditor = postSharedpreferences.edit();
                for (int i = 0; i < postSharedpreferences.getInt("PostSize", 0); i++) {
                    if (postSharedpreferences.getString("PostNickname" + i, null).equals(Nickname)) {
                        postEditor.putString("PostProfile" + i, profileUri);
                    }
                }
                postEditor.commit();

                SharedPreferences gridPostSharedPreferences = getSharedPreferences(Nickname + "GridPost", MODE_PRIVATE);
                SharedPreferences.Editor gridPostEditor = gridPostSharedPreferences.edit();
                for (int i = 0; i < gridPostSharedPreferences.getInt("GridPostSize", 0); i++) {
                    gridPostEditor.putString("PostProfile" + i, profileUri);
                }
                gridPostEditor.commit();

                //댓글에 저장된 자신의 프로필 사진을 새롭게 바꾼 사진으로 다시 저장하기 위해서 다음과 같은 코드를 삽입했다.
                for (int i = 0; i < postSharedpreferences.getInt("PostSize", 0); i++) {
                    SharedPreferences commentSharedPreferences = getSharedPreferences("Comment" + i, MODE_PRIVATE);
                    SharedPreferences.Editor commentEditor = commentSharedPreferences.edit();
                    for (int j = 0; j < commentSharedPreferences.getInt("CommentSize", 0); j++) {
                        if (commentSharedPreferences.getString("CommentNickname" + j, null).equals(Nickname)) {
                            commentEditor.putString("CommentProfile" + j, profileUri);

                        }
                    }

                    commentEditor.commit();
                }

                Intent intent = new Intent(EditAccountActivity.this, AccountActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
            }
        });

        //카메라와 라이브러리 접근시에 권한 요청
        requirePermission();

        //프로필 사진 편집 버튼을 누를 시 동작 정의
        CircleImageView_editprofile.setClickable(true);
        CircleImageView_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditAccountActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.profile_setting_box);
                dialog.show();

                TextView TextView_library, TextView_camera;

                TextView_library = dialog.findViewById(R.id.TextView_library);
                TextView_camera = dialog.findViewById(R.id.TextView_camera);

                TextView_library.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //읽기와 쓰기 권한 부여되었는지 확인
                        boolean write = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                        boolean read = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                        //읽기, 쓰기 권한이 부여되었으면
                        if (write && read) {
                            //사진찍는 인텐트 코드 넣기
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, Request_library);

                        } else {//권한이 부여되지 않았으면 경고 메시지 출력
                            Toast.makeText(EditAccountActivity.this, "읽기/쓰기 권한 필요", Toast.LENGTH_SHORT).show();

                        }

                        dialog.dismiss();

                    }
                });

                TextView_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //권한 부여되었는지 확인
                        boolean camera = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                        boolean write = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                        //권한이 부여되었으면
                        if (camera && write) {
                            //사진찍는 인텐트 코드 넣기
                            takePicture();

                        } else {//권한이 부여되지 않았으면 경고 메시지 출력
                            Toast.makeText(EditAccountActivity.this, "카메라 및 쓰기 권한 필요", Toast.LENGTH_SHORT).show();

                        }

                        dialog.dismiss();

                    }
                });

            }
        });


        //프로필 사진 편집 텍스트뷰를 누를 시 동작 정의
        TextView_editprofile.setClickable(true);
        TextView_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditAccountActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.profile_setting_box);
                dialog.show();

                TextView TextView_library, TextView_camera;

                TextView_library = dialog.findViewById(R.id.TextView_library);
                TextView_camera = dialog.findViewById(R.id.TextView_camera);

                TextView_library.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //읽기와 쓰기 권한 부여되었는지 확인
                        boolean write = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                        boolean read = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                        //읽기, 쓰기 권한이 부여되었으면
                        if (write && read) {
                            //사진찍는 인텐트 코드 넣기
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, Request_library);

                        } else {//권한이 부여되지 않았으면 경고 메시지 출력
                            Toast.makeText(EditAccountActivity.this, "읽기/쓰기 권한 필요", Toast.LENGTH_SHORT).show();

                        }

                        dialog.dismiss();

                    }
                });

                TextView_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //권한 부여되었는지 확인
                        boolean camera = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                        boolean write = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                        //권한이 부여되었으면
                        if (camera && write) {
                            //사진찍는 인텐트 코드 넣기
                            takePicture();

                        } else {//권한이 부여되지 않았으면 경고 메시지 출력
                            Toast.makeText(EditAccountActivity.this, "카메라 및 쓰기 권한 필요", Toast.LENGTH_SHORT).show();

                        }

                        dialog.dismiss();

                    }
                });
            }
        });

    }


    //startActivityForResult메소드로 보낸 요청 코드에 대한 반응 정의
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_library && resultCode == RESULT_OK && data != null) {
            //선택한 사진의 uri를 가져옴
            Uri uri = data.getData();

            data.putExtra("GalleryPhotoUri", galleryPhotoUri);

//            Picasso.with(this).load(uri).resize(200,200).into(ImageView_image);
//            try {//사진을 비트맵으로 얻기
//                //이미지 뷰에 내가 가져온 사진 넣어주기
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri를 뽑아오게 해준다.
//                ImageView_image.setRotation(90);//사진을 90도 회전
//                ImageView_image.setImageBitmap(bitmap);
//                //비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입
//            } catch (IOException e){
//                e.printStackTrace();
//
//
//
//    }



//            Cursor c = getContentResolver().query(uri, null, null, null, null);
//            c.moveToNext();
//            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
//            bitmapUri = Uri.fromFile(new File(path));
//            bitmapUri = uri;
//            Log.e("URI", uri.toString());
//            c.close();

//            profileUri = bitmapUri.toString();
            profileUri = getRealPathFromURI(uri);;
            if (profileUri != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                //이미지 뷰에 내가 가져온 사진 넣어주기-decodeFile에 들어가는 첫번째 파라미터는 String형태의 Uri다.
                bitmap = BitmapFactory.decodeFile(profileUri, options);
                CircleImageView_editprofile.setRotation(90);//사진을 90도 회전
                CircleImageView_editprofile.setImageBitmap(bitmap);
                //비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입

            }


        }

        //카메라 촬영 result
        if (requestCode == 0) {
            profileUri = mCurrentPhotoPath;

            if (profileUri != null) {
                //사진의 용량을 줄이기 위해서 비트맵 팩토리 옵션 사용
                BitmapFactory.Options options = new BitmapFactory.Options();
                //원래 크기의 1/8크기로 줄임
                options.inSampleSize = 8;
                //ImageView 객체 생성
                CircleImageView_editprofile = findViewById(R.id.CircleImageView_editprofile);
//            Picasso.with(this).load(mCurrentPhotoPath).into(ImageView_image);
                CircleImageView_editprofile.setRotation(90);//사진을 90도 회전
                //decodeFile의 파라미터로 카메라로 찍은 사진의 uri와 비트맵팩토리로 설정해준 옵션을 넣어서 사진 셋
                CircleImageView_editprofile.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath, options));
            }


        }


    }

    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        }
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    //카메라 실행시 권한을 요청하는 메소드
    private void requirePermission() {
        //요구할 퍼미션들의 배열 생성
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //허가되지 않은 퍼미션을 저장할 리스트 생성
        List<String> listPermissionNedded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                listPermissionNedded.add(permission);
            }
        }

        //허가되지 않은 퍼미션이 있을 경우
        if (!listPermissionNedded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNedded.toArray(new String[listPermissionNedded.size()]),
                    1);

        }

    }

    private void takePicture() {
        //인텐트 생성
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            //파일 생성
            File photoFile = createImageFile();
            //Uri 객체로 변환
            Uri photoUri = FileProvider.getUriForFile(this, "com.haram.camera.fileprovider", photoFile);

            //인텐트에 인자로 전달
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            //카메라 실행
            startActivityForResult(intent, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        //날짜와 시간에 대한 문자열
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //원하는 이름 포맷
        String imageFileName = "JPEG_" + timestamp + "_";
        //디렉토리를 받아옴
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //파일 생성
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );

        //전역변수로 설정한 mCurrentPhotoPath에 경로를 저장
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
