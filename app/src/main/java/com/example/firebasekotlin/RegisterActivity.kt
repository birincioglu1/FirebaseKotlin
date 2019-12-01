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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnKayitOl.setOnClickListener {
            if (etMail.text.isNotEmpty()&& etSifre.text.isNotEmpty()&& etSifreTekrar.text.isNotEmpty())
            {
                if(etSifre.text.toString().equals(etSifreTekrar.text.toString()))
                {
                    yeniUyeKayit(etMail.text.toString(),etSifre.text.toString())

                }
                else
                {
                    Toast.makeText(this,"Şifreler aynı değil!",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this,"Boş alanlari doldurunuz!!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        progressBarGoster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object:OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful)
                    {


                        onayMailiGonder()
                        var veriTabaninaEklenecekKullanici=Kullanici()
                        veriTabaninaEklenecekKullanici.isim=etMail.text.toString().substring(0,etMail.text.toString().indexOf("@"))
                        //@işaretine kadarki kısmı al isim gibi yap
                        veriTabaninaEklenecekKullanici.user_id=FirebaseAuth.getInstance().currentUser?.uid
                        veriTabaninaEklenecekKullanici.profil_resmi=""
                        veriTabaninaEklenecekKullanici.telefon="123"
                        veriTabaninaEklenecekKullanici.seviye="1"

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(veriTabaninaEklenecekKullanici)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful)
                                {
                                    Toast.makeText(this@RegisterActivity,"Üye kayit edildi!"+FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginSayfasinaYonlendir()

                                }
                            }


                    }
                    else{

                        Toast.makeText(this@RegisterActivity,"Üye kayit edilirken sorun oluştu!"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                    }

                }

            })
        progressBarGizle()

    }
    private fun progressBarGoster()
    {
        progressBar.visibility= View.VISIBLE
    }
    private fun progressBarGizle()
    {
        progressBar.visibility= View.INVISIBLE
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
                            Toast.makeText(this@RegisterActivity,"Mail Kutunuzu Kontrol Ediniz",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@RegisterActivity,"Mail Gönderilirken Sorun Oluştu"+p0.exception,Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()

                        }
                    }

                })
        }

    }
    fun loginSayfasinaYonlendir()
    {
        var intent= Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
