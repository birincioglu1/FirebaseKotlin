package com.example.firebasekotlin

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class KullaniciAyarlariActivity : AppCompatActivity(),ProfilResmiFragment.onProfilResimListenir {
    var izinlerVerildiMi=false
    var galeridenGelenUri:Uri?=null
    var kameradanGelenBitmap:Bitmap?=null
    val MEGABYTE=10000000.toDouble()
    override fun getResimYolu(resimPath: Uri?) {
        galeridenGelenUri=resimPath
        Picasso.with(this).load(galeridenGelenUri).resize(100,100).into(imgProfil)
    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kameradanGelenBitmap=bitmap
        imgProfil.setImageBitmap(bitmap)
       //Picasso.with(this).load(bitmap)

    }
    inner class BackgroundResimCompress: AsyncTask<Uri,Void,ByteArray?>//arka planda işlem yaptırmak
    {
        var myBitmap:Bitmap?=null
        constructor(){}
        constructor(bm:Bitmap){
            if (bm!=null)
            {
                myBitmap=bm
            }
        }
        override fun onPreExecute() { //Main thread te çalışır / ana ekrana mesaj
            super.onPreExecute()
        }
        override fun doInBackground(vararg params: Uri?): ByteArray? {//worker thread
            if(myBitmap==null) //galeriden gelmiş resim
            {

                   myBitmap=ImageDecoder.decodeBitmap(ImageDecoder.createSource(this@KullaniciAyarlariActivity.contentResolver, params[0]!!))
                    Log.e("Test","Orjinal resim Boyutu:"+(myBitmap!!.byteCount).toDouble()/MEGABYTE)

            }
            var resimBytes: ByteArray?=null
            for(i in 1..5)
            {
                resimBytes=convertBitMaptoByte(myBitmap,100/i)
            }
            return resimBytes
        }

        private fun convertBitMaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {
            var stream=ByteArrayOutputStream()//sıkıştırma işlemleri
            myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()
        }

        override fun onPostExecute(result: ByteArray?) { //Main thread te çalışır
            super.onPostExecute(result)
            uploadResimtoFirebase(result)
        }

    }

    private fun uploadResimtoFirebase(result: ByteArray?) {

    }

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
            if(galeridenGelenUri!=null)
            {
                fotografCompressed(galeridenGelenUri!!)

            }else if (kameradanGelenBitmap!=null)
            {
                fotografCompressed(kameradanGelenBitmap!!)
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

        imgProfil.setOnClickListener {




            if(izinlerVerildiMi==true) {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager, "fotosec")
            }else{
                izinleriIste()
            }

        }
    }

    private fun fotografCompressed(galeridenGelenUri: Uri) {
        var compressed=BackgroundResimCompress()
        compressed.execute(galeridenGelenUri) //worker threadi çalıştırma

    }
    private fun fotografCompressed(kameradanGelenBitmap: Bitmap) {
        var compressed=BackgroundResimCompress(kameradanGelenBitmap)
        var uri:Uri?=null
        compressed.execute(uri)
    }

    private fun izinleriIste() {
        var izinler= arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA )
        if (ContextCompat.checkSelfPermission(this,izinler[0])== PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,izinler[1])== PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,izinler[2])== PackageManager.PERMISSION_GRANTED)
        {
            izinlerVerildiMi=true
        }
        else{
            ActivityCompat.requestPermissions(this,izinler,150)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==150)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED&&
                grantResults[2]==PackageManager.PERMISSION_GRANTED)
            {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager, "fotosec")

            }
            else
            {
                Toast.makeText(this@KullaniciAyarlariActivity,"Tum izinleri vermelisiniz!",Toast.LENGTH_SHORT).show()
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
