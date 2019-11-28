package com.example.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
   lateinit var myAuthStateListenir:FirebaseAuth.AuthStateListener  //İnterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvKayitOl.setOnClickListener {
            val intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        btnGirisYap.setOnClickListener {

            if(etMail.text.isNotEmpty()&&etSifre.text.isNotEmpty())
            {
                progressBarGoster()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etMail.text.toString(),etSifre.text.toString())
                    .addOnCompleteListener(object :OnCompleteListener<AuthResult>{
                        override fun onComplete(p0: Task<AuthResult>) {
                            if(p0.isSuccessful)
                            {progressBarGizle()
                                Toast.makeText(this@LoginActivity,"Başarili Giris:"+FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_SHORT).show()
                                onayMailiGonder()
                                FirebaseAuth.getInstance().signOut()
                            }else{
                                progressBarGizle()
                                Toast.makeText(this@LoginActivity,"Hatali giriş"+p0.exception,Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

            }else{
                Toast.makeText(this@LoginActivity,"Boş alanlari Doldurunuz!",Toast.LENGTH_SHORT).show()
            }


        }
    }
    private fun onayMailiGonder(){
        var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici!=null)
        {
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object :OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful)
                        {
                            Toast.makeText(this@LoginActivity,"Mail Kutunuzu Kontrol Ediniz",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@LoginActivity,"Mail Gönderilirken Sorun Oluştu"+p0.exception,Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

    }
    private fun progressBarGoster()
    {
        progressBarLogin.visibility= View.VISIBLE
    }
    private fun progressBarGizle()
    {
        progressBarLogin.visibility= View.INVISIBLE
    }

    override fun onStart() { //Aktivite Açılmadan önce çalışır
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListenir)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListenir)
    }
}
