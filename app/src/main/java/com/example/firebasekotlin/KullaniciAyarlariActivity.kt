package com.example.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlin.math.sign

class KullaniciAyarlariActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)
        var kullanici=FirebaseAuth.getInstance().currentUser!!

        etDetayName.setText(kullanici.displayName.toString())


        btnSifreGonder.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici.email.toString())
                .addOnCompleteListener{task ->
                    if (task.isSuccessful)
                    {
                        Toast.makeText(this@KullaniciAyarlariActivity,"Şifre sıfırlama gonderildi", Toast.LENGTH_SHORT).show()

                    }else
                    {
                        Toast.makeText(this@KullaniciAyarlariActivity,"Hata Oluştu"+task.exception, Toast.LENGTH_SHORT).show()
                    }
                }
        }
        btnDegisikleriKaydet.setOnClickListener {
            if(etDetayName.text.toString().isNotEmpty()){
                if(!etDetayName.text.toString().equals(kullanici.email.toString()))
                {
                      var bilgilerGuncelle= UserProfileChangeRequest.Builder()
                          .setDisplayName(etDetayName.text.toString()).build()
                    kullanici.updateProfile(bilgilerGuncelle)
                        .addOnCompleteListener {task->
                            if(task.isSuccessful)
                            {
                                Toast.makeText(this@KullaniciAyarlariActivity,"Değişiklikler Yapildi", Toast.LENGTH_SHORT).show()
                            }
                        }


                }

            }else{

                Toast.makeText(this@KullaniciAyarlariActivity,"Boş alanlari Doldurunuz",Toast.LENGTH_SHORT).show()


            }
        }

        btnSifreveyaMailGuncelle.setOnClickListener {
            if(etDetaySifre.text.toString().isNotEmpty())
            {
                var credential=EmailAuthProvider.getCredential(kullanici.email.toString(),etDetaySifre.text
                    .toString())
                kullanici.reauthenticate(credential)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful)
                        {
                            guncelleLayout.visibility=View.VISIBLE
                            btnMailGuncelle.setOnClickListener {
                                mailAdresininGuncelle()
                            }
                                btnSifreGuncelle.setOnClickListener {
                                    sifrebilgisiniGuncelle()
                                }
                        }else
                        {
                            Toast.makeText(this@KullaniciAyarlariActivity,"Şuanki şifreniz yanlış girdiniz",Toast.LENGTH_SHORT).show()
                            guncelleLayout.visibility=View.INVISIBLE
                        }
                    }
            }else{
                Toast.makeText(this@KullaniciAyarlariActivity,"Güncellemeler için geçerli şifrenizi yazmalısınız",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sifrebilgisiniGuncelle() {
        var kullanici=FirebaseAuth.getInstance().currentUser!!


        if (kullanici!=null)
        {
            kullanici.updatePassword(etYeniSifreGuncelle.text.toString())
                .addOnCompleteListener { task ->
                    Toast.makeText(this@KullaniciAyarlariActivity,"Şifreniz değiştirildi tekrar giriş yapın",Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    loginSayfasinaYonlendir()

                }
        }
    }

    private fun mailAdresininGuncelle()
    {
        var kullanici=FirebaseAuth.getInstance().currentUser!!
        if (kullanici!=null)
        {
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(etYeniMail.text.toString())
                .addOnCompleteListener { task ->
                    Toast.makeText(this@KullaniciAyarlariActivity,"Güncellendi"+task.exception,Toast.LENGTH_SHORT).show()
                }
            kullanici.updateEmail(etYeniMail.text.toString())
                .addOnCompleteListener { task ->
                    Toast.makeText(this@KullaniciAyarlariActivity,"Mail değiştirildi tekrar giriş yapın",Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    loginSayfasinaYonlendir()
                }
        }else{
            Toast.makeText(this@KullaniciAyarlariActivity,"Mail güncellenemedi",Toast.LENGTH_SHORT).show()
        }

    }
    fun loginSayfasinaYonlendir()
    {
        var intent=Intent(this@KullaniciAyarlariActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
