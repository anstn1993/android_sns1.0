package com.example.sns;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class ProfileSetActivity extends AppCompatActivity {
    Button Button_library,Button_camera;
    int Request_library=1;
    String mCurrentPhotoPath;
    Uri uri;
    Bitmap bitmap;
    EditAccountActivity editAccountActivity;



//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile_set);
//        Button_library=findViewById(R.id.Button_library);
//        Button_camera=findViewById(R.id.Button_camera);
//
//        //카메라와 라이브러리 접근시에 권한 요청
//        requirePermission();
//        Button_library.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //읽기와 쓰기 권한 부여되었는지 확인
//                boolean write= ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
//                boolean read=ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
//                //읽기, 쓰기 권한이 부여되었으면
//                if(write && read){
//                    //사진찍는 인텐트 코드 넣기
//                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(intent,Request_library);
//
//                }else{//권한이 부여되지 않았으면 경고 메시지 출력
//                    Toast.makeText(ProfileSetActivity.this, "읽기/쓰기 권한 필요",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
//        Button_camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //권한 부여되었는지 확인
//                boolean camera=ContextCompat.checkSelfPermission(v.getContext(),Manifest.permission.CAMERA) ==PackageManager.PERMISSION_GRANTED;
//
//                boolean write=ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
//
//                //권한이 부여되었으면
//                if(camera && write){
//                    //사진찍는 인텐트 코드 넣기
//                    takePicture();
//
//                }else{//권한이 부여되지 않았으면 경고 메시지 출력
//                    Toast.makeText(ProfileSetActivity.this, "카메라 및 쓰기 권한 필요",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
//
//    }
//
//
//    //카메라 실행시 권한을 요청하는 메소드
//    private void requirePermission(){
//        //요구할 퍼미션들의 배열 생성
//        String[] permissions=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        //허가되지 않은 퍼미션을 저장할 리스트 생성
//        List<String> listPermissionNedded=new ArrayList<>();
//
//        for(String permission : permissions){
//            if(ContextCompat.checkSelfPermission(this, permission)== PackageManager.PERMISSION_DENIED){
//                listPermissionNedded.add(permission);
//            }
//        }
//
//        //허가되지 않은 퍼미션이 있을 경우
//        if(!listPermissionNedded.isEmpty()){
//            ActivityCompat.requestPermissions(
//                    this,
//                    listPermissionNedded.toArray(new String[listPermissionNedded.size()]),
//                    1);
//
//        }
//
//    }
//
//    private void takePicture(){
//        //인텐트 생성
//        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try{
//            //파일 생성
//            File photoFile = createImageFile();
//            //Uri 객체로 변환
//            Uri photoUri= FileProvider.getUriForFile(this,"com.haram.camera.fileprovider",photoFile);
//            //인텐트에 인자로 전달
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
//            //카메라 실행
//            startActivityForResult(intent,0);
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    private File createImageFile() throws IOException{
//        //날짜와 시간에 대한 문자열
//        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        //원하는 이름 포맷
//        String imageFileName="JPEG_"+timestamp+"_";
//        //디렉토리를 받아옴
//        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        //파일 생성
//        File image=File.createTempFile(
//                imageFileName,".jpg",storageDir
//        );
//
//        //전역변수로 설정한 mCurrentPhotoPath에 경로를 저장
//        mCurrentPhotoPath=image.getAbsolutePath();
//        return image;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==Request_library && resultCode==RESULT_OK && data != null){
//            //사진이 정상적으로 선택된 경우
//            uri=data.getData();
//
//
//            try {//사진을 비트맵으로 얻기
//                //이미지 뷰에 내가 가져온 사진 넣어주기
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//MediaStore 클래스에서 getContentResolver()를 통해서 provider에서 해당 uri를 뽑아오게 해준다.
//                editAccountActivity.CircleImageView_profile_picture.setRotation(90);//사진을 90도 회전
//                editAccountActivity.CircleImageView_profile_picture.setImageBitmap(bitmap);//비트맵 객체에 들어있는 이미지 파일을 메소드 파라미터로 대입해서 해당 이미지를 이미지 뷰에 삽입
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//
//        }
//
//        //startActivityForResult() 실행할 때 requestCode로 0을 대입함
//        if(requestCode==0){
//            //ImageView 객체 생성
//            editAccountActivity.ImageButton_editprofile=findViewById(R.id.ImageButton_editprofile);
//            editAccountActivity.ImageButton_editprofile.setRotation(90);//사진을 90도 회전
//            editAccountActivity.ImageButton_editprofile.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
//
//        }
//
//
//    }
}
