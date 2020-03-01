package com.example.instartest1.navigation

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instartest1.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage?=null
    var photoUri : Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // storge 초기화
        storage = FirebaseStorage.getInstance()

        // 액티비티 시작하자마자 화면 열릴수 있도록 Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        // 버튼에 이벤트 넣어주기

        addphoto_btn_upload.setOnClickListener(){
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){ // 사진을 클릭했을때
                // 이미지의 경로가 이쪽으로 넘어오도록
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }else{
                // 취소버튼을 눌렀을때 작동하는 부분
                finish()
            }
        }
    }
    fun contentUpload(){
        //  Make filename

        // 이름이 중복생성되지 않도록 날짜를 입력해줌
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // 이렇게하면 이름이 중복생성되지 않음
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        // 이미지 업로드
        // images 폴더명에 imagefileName 파일명
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 파일 업로드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,getString(R.string.upload_success), Toast.LENGTH_LONG).show()

        }
    }
}
