package com.example.firebasekotlin

import android.content.Intent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        initMyAuthListenir()  //İnterface atamalari

        tvKayitOl.setOnClickListener {
            val intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
            tvonayMailiTekrarGonder.setOnClickListener {  //Dialog oluşturma
                var dialogGoster=OnayMailTekrarFragment()
                dialogGoster.show(supportFragmentManager,"gosterdialogSifre")
            }
        tvSifreTekrarYolla.setOnClickListener {
            var dialogSifreyiTekrarGonder=SifremiunuttumDialogFragment()
            dialogSifreyiTekrarGonder.show(supportFragmentManager,"gosterdialogSifre")
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
                                if (!p0.result!!.user!!.isEmailVerified)
                                {
                                    FirebaseAuth.getInstance().signOut()
                                }

                                 // Toast.makeText(this@LoginActivity,"Başarili Giris:"+FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_SHORT).show()


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
    private fun progressBarGoster()
    {
        progressBarLogin.visibility= View.VISIBLE
    }
    private fun progressBarGizle()
    {
        progressBarLogin.visibility= View.INVISIBLE
    }
    private fun initMyAuthListenir()
    {
        myAuthStateListenir=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici=p0.currentUser
                if (kullanici!=null)
                {
                    if (kullanici.isEmailVerified)
                    {
                        Toast.makeText(this@LoginActivity,"Mail onaylanmış giriş yapılabilir",Toast.LENGTH_SHORT).show()
                        var intent=Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity,"Mail Adresinizi onaylayin",Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }
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
