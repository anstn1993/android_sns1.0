# android_sns1.0
my first android sns application

[어플 시연 영상] (https://www.youtube.com/watch?v=DGg_yQxp_nk&t=295s)

## 제작 의도

자바를 이용한 안드로이드 어플 개발을 학습하기 위해서 제작하였습니다. 안드로이드 플랫폼을 통한 어플 개발의 세계관을 이해할 수 있었습니다. 

이 어플은 서버와 연동되어있지 않으며 모든 데이터는 shared prefecence를 이용하여 로컬에 저장하는 방식으로 제작되었습니다.  

## 사용 기술

언어: 자바

IDE: Android Studio

주요 기술: recycler view, shared preference, handler, async task

API: google map

## 작품 소개

인스타그램을 모토로 한 SNS어플 입니다. 

## 주요 기능

##### 1. 회원가입

-아이디, 닉네임, 주민등록번호 뒷자리를 입력할 때 실시간으로 중복확인을 할 수 있습니다. 

##### 2. 아이디/비밀번호 찾기

-회원가입할 때 입력한 정보를 이용하여 아이디와 비밀번호를 찾을 수 있습니다.

##### 3. 로그인/로그아웃

-회원가입한 계정으로 로그인을 할 수 있습니다.

-'아이디 기억' 체크박스에 체크를 하고 로그인을 하면 다시 앱을 실행시켰을 때 자동으로 아이디가 입력됩니다.   

-'자동로그인' 체크박스에 체크를 하고 로그인을 하면 다시 앱을 실행시켰을 때 자동으로 로그인이 됩니다.

-'마이 페이지'에서 로그아웃을 할 수 있습니다.

##### 4. 메인 페이지

-사용자들이 업로드한 게시물을 한 곳에 모아둔 페이지 입니다.

-메인 페이지에 최초 접근 시 몇 초 후에 게시물 업로드를 유도하는 말풍선이 뜹니다. 

-게시물이 언제 올라왔는지 확인할 수 있고, 한줄을 넘어가는 게시글을 '더 보기'버튼을 통해 펼쳐서 볼 수 있습니다. 

-해당 게시물에 현재 몇 개의 댓글이 달렸는지 확인할 수 있습니다. 

-게시물에 표시되는 닉네임이나 프로필사진을 누르면 해당 게시물을 작성한 사용자의 페이지로 이동합니다. 

##### 5. 마이 페이지

-자신의 게시물만 따로 모아서 보여주는 페이지 입니다. 

-프로필, 이름, 상태 메세지 수정이 가능합니다. 상태 메세지를 설정하면 마이 페이지에서 이름과 상태 메세지가 번갈아가면서 뜨게 됩니다.  

-위에서 언급한 로그아웃을 마이 페이지우측 상단의 로그아웃 버튼을 통해서 할 수 있습니다.


##### 6. 게시물 업로드, 수정, 삭제

-게시물을 업로드할 경우 사진은 필수적으로 등록을 해야 하며 게시글과 장소는 선택적으로 등록할 수 있습니다.

-장소는 현재 위치를 등록할 수도 있고, 장소를 검색해서 특정 장소를 등록할 수도 있고, 직접 화면의 지도를 터치해서 등록할 수도 있습니다.

-게시물을 업로드하게 되면 화면의 상단에서 업로드 진행률을 확인할 수 있습니다.

-업로드가 완료되면 '메인 페이지'와 '마이 페이지'에 게시물이 추가됩니다.  

-자신의 게시물을 '수정', '삭제'할 수 있습니다. 

##### 7. 지도에서 게시물 보기

-만약 사용자들이 게시물을 업로드할 때 장소를 등록했다면 지도 상에 등록된 게시물의 사진을 볼 수 있습니다. 

-처음에는 자신의 현재 위치 주변에서 등록된 게시물의 사진을 보여주고 특정 장소에서 업로드된 게시물의 사진을 보고 싶다면 검색을 통해서 볼 수 있습니다. 

-사진을 클릭하면 해당 게시물 상세 페이지로 이동하게 됩니다. 

##### 8. 댓글

-게시물의 댓글 버튼을 누르면 댓글 페이지로 넘어가고 댓글을 달 수 있습니다. 

-자신이 단 댓글은 수정과 삭제를 할 수 있습니다. 

##### 9. 알림

-누군가가 댓글을 달면 댓글이 달린 게시물의 주인의 알림 페이지에 알림이 생기게 됩니다. 

-알림의 프로필 사진을 누르면 댓글을 단 사람의 페이지로 이동하고 알림의 내용을 누르면 댓글이 달린 게시물로 이동하여 댓글을 확인할 수있습니다. 

##### 10. 사용자 검색    

-어플의 사용자들을 검색할 수 있습니다. 

-검색된 사용자를 클릭하면 해당 사용자의 페이지로 이동하게 됩니다. 
