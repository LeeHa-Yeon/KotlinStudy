package com.example.instartest1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
// 내 말 안ㄷ아니 ㄴㅡㄹ맂마건즐지마 ㄱㄴ드리자건들ㅈ ㅣ마

class MainActivity : AppCompatActivity() {
    var auth : FirebaseAuth?=null
    var googleSignInClient : GoogleSignInClient ?= null
    var GOOGLE_LOGIN_CODE =9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            // First step
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("54143322940-39hhobq6li47hsfoll97l5q2ui6eje9a.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        printHashKey()
    }
    //
    fun printHashKey() {
        try {
            val info =packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
    /*fun printHashKey() {
        try {
            val info = packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }

    }*/
    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account = result.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(){
                    task ->
                if(task.isSuccessful) {
                    // Login
                }else{
                    // Show the error massage
                }
            }
    }
    // 회원가입 코드
    fun signinAndSignup() {
        // 회원가입한 결과값을 받아오기 위해 addOnComplateListener를 쓴다. 이때 중괄호있는 람다형식을 사용
        // task 는 패러미터 값 , 변경가능
     auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
         ?.addOnCompleteListener(){
             task ->
             if(task.isSuccessful) {
                 // 아이디가 생성되었을 때 작동하는 if문 사용
                 // 사용자 계정이 생성되었을 때
                 moveMainPage(task.result?.user)
             }else if(!task.exception?.message.isNullOrEmpty()){
                 Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
             // 로그인 에러났을시
             }else{
                 signinEmail()
             // 회원가입이 아니거나 에러메시지가 아닐 경우 로그인하는 부분으로 빠지도록
             }
         }

     }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener(){
                    task ->
                if(task.isSuccessful) {
                    // Login
                }else{
                    // Show the error massage
                }
            }
    }
    // 로그인 성공시 다음 페이지로 넘어가는 함수
    fun moveMainPage(user: FirebaseUser?){ // 파이어베이스유저상태를 넘겨준다.
        if(user != null){
            startActivity(Intent(this,Main2Activity::class.java))
        }

    }

}
