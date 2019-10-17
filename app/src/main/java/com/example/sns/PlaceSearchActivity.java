package com.example.sns;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PlaceSearchActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    EditText EditText_search;
    TextView TextView_search, TextView_address;
    ImageView ImageView_back;
    TextView TextView_apply;

    //검색한 장소의 위도를 담기 위한 변수
    float latitude;

    //검색한 장소의 경도를 담기 위한 변수
    float longitude;

    //검색한 장소의 문자 주소를 담기 위한 변수
    String address;
    String markerTitle;
    String markerSnippet;

    GoogleMap mGoogleMap=null;
    Marker currentMarker=null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    //스낵바를 사용하기 위해서는 스낵바가 실행될 현재 view를 가져와야 한다.\
    //(Toast에서는 Context가 필요했듯이)
    private View mLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("onCreate", "호출됨");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_place_search);


        mLayout = findViewById(R.id.ConstraintLayout_map);
        ImageView_back=findViewById(R.id.ImageView_back);
        EditText_search=findViewById(R.id.EditText_search);
        TextView_search=findViewById(R.id.TextView_search);
        TextView_apply=findViewById(R.id.TextView_apply);
        TextView_address=findViewById(R.id.TextView_address);


        locationRequest = new LocationRequest()
                //priorty는 4가지가 있는데 지금 설정된 값은 배터리 소모를 고려하지 않고 정확도를 최우선으로 하여 위치를 가져오는 것이다.
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                //위치가 update되는 주기를 설정
//                .setInterval(UPDATE_INTERVAL_MS)
//                //위치 획득 후에 update되는 주기다.
//                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);



        ImageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Address",address);
                setResult(RESULT_OK,intent);
                finish();

                try{
                    Log.d("주소", address);
                    Log.d("위도",String.valueOf(latitude));
                    Log.d("경도", String.valueOf(longitude));
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());


                 markerTitle = getCurrentAddress(currentPosition);
                 markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mGoogleMap.setMyLocationEnabled(true);

        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap=googleMap;
        //지도를 클릭했을 때 동작은 onMapClick메소드를 통해서 구현된다.
        mGoogleMap.setOnMapClickListener(this);

        //현재 위치 버튼을 누르면 벌어지는 동작 설정
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mGoogleMap.clear();
                setCurrentLocation(location, markerTitle, markerSnippet);

                return false;
            }
        });

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요하다. 2가지 경우(3-1, 4-1)가 있음

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있음
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( PlaceSearchActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        TextView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색할 때마다 이전에 찍힌 마커를 지우기 위해 설정
                googleMap.clear();
                //검색창에 입력한 장소를 담는 변수
                String searchedAddress = EditText_search.getText().toString();
                //현재 컨텍스트에서 지오 코더를 사용하여 주소명을 좌표로 변환
                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    //지오코더를 통해서 주소명으로 위도와 경도를 가져온다.
                    List<Address> searhedLocation= geocoder.getFromLocationName(searchedAddress, 10);
                    //전역변수로 설정해둔 위도와 경도 변수에 리스트에 담은 위도 경도 값을 넣어준다.
                    latitude = (float) searhedLocation.get(0).getLatitude();
                    longitude = (float) searhedLocation.get(0).getLongitude();

                    //이번엔 반대로 새로운 리스트를 선언하고 거기에 지오코더를 통해서 위도와 경도를 통해 주소명을 가져온다.
                    List<Address> locationAddress=geocoder.getFromLocation(latitude,longitude,1);
                    //LatLng객체에 전역변수에 담아둔 위도 경도 값을 넣어준다.
                    LatLng location=new LatLng(latitude, longitude);
                    //마커 옵션 설정을 위해 마커옵션 객체 초기화
                    MarkerOptions markerOptions = new MarkerOptions();
                    //마커의 위치를 설정한다.
                    markerOptions.position(location);
                    //그리고 위에서 선언한 전역변수인 주소 변수에 지오코더를 통해서 가져온 주소명을 넣어준다.
                    address=locationAddress.get(0).getAddressLine(0);
                    //그리고 그 주소 변수를 마커의 타이틀로 사용한다.
                    markerOptions.title(address);
                    TextView_address.setText(address);

                    //마커 옵션 설정이 다 끝났으면 그 옵션이 적용된 마커를 지도에 적용해준다.
                    googleMap.addMarker(markerOptions);
                    //아까 LatLnt객체에 넣어준 위도 경도의 위치로 카메라를 이동시칸 후
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    //카메라를 15만큼 줌인한다.
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                } catch(IndexOutOfBoundsException e){
                    final Dialog dialog=new Dialog(PlaceSearchActivity.this);
                    //타이틀 바 없애기
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //다이얼로그에 연결시킬 레이아웃 설정
                    dialog.setContentView(R.layout.non_location_box);
                    //다이얼로그 띄우기
                    dialog.show();
                    TextView TextView_check;
                    TextView_check=dialog.findViewById(R.id.TextView_check);
                    TextView_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }



            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mGoogleMap.clear();


        //현재 컨텍스트에서 지오 코더를 사용하여 좌표를 주소명으로 변환
        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            //전역변수로 설정해둔 위도와 경도 변수에 리스트에 담은 위도 경도 값을 넣어준다.
            latitude = (float) latLng.latitude;
            longitude = (float) latLng.longitude;

            //이번엔 반대로 새로운 리스트를 선언하고 거기에 지오코더를 통해서 위도와 경도를 통해 주소명을 가져온다.
            List<Address> locationAddress=geocoder.getFromLocation(latitude,longitude,1);
            //LatLng객체에 전역변수에 담아둔 위도 경도 값을 넣어준다.
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            mGoogleMap.addMarker(markerOptions);
            //그리고 위에서 선언한 전역변수인 주소 변수에 지오코더를 통해서 가져온 주소명을 넣어준다.
            address=locationAddress.get(0).getAddressLine(0);
            //그리고 그 주소 변수를 마커의 타이틀로 사용한다.
            markerOptions.title(address);
            TextView_address.setText(address);

            //마커 옵션 설정이 다 끝났으면 그 옵션이 적용된 마커를 지도에 적용해준다.
            mGoogleMap.addMarker(markerOptions);
            //아까 LatLnt객체에 넣어준 위도 경도의 위치로 카메라를 이동시칸 후
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //카메라를 15만큼 줌인한다.
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mGoogleMap!=null)
                mGoogleMap.setMyLocationEnabled(true);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "호출됨");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
            latitude= (float) latlng.latitude;
            longitude=(float) latlng.longitude;
            address=addresses.get(0).getAddressLine(0);
            TextView_address.setText(address);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {

            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Geocoder geocoder=new Geocoder(getApplicationContext());

        latitude= (float) currentLatLng.latitude;
        longitude=(float) currentLatLng.longitude;

        List<Address> locationAddress= null;
        try {
            locationAddress = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address=locationAddress.get(0).getAddressLine(0);
        TextView_address.setText(address);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.draggable(true);


        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mGoogleMap.moveCamera(cameraUpdate);

    }

    //지도가 처음 실행되면 이동시킬 포커스를 지정하기 위해 존재하는 메소드
    //하지만 현재 위치가 파악되면 그 곳으로 포커스가 이동됨
    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        //gps가 현재 위치를 가져오지 못할 때를 대비해서 만든 초기 위치
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //전역변수로 설정해둔 마커 변수에 마커 옵션으로 지정이 끝난 마커를 추가해서 넣어줌
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceSearchActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
}
