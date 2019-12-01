package com.example.firebasekotlin

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myAuthStateListenir:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListenir()
        setKullaniciBilgileri()

    }

    private fun setKullaniciBilgileri() {
        var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici!=null)
        {
            tvUserName.text=if(kullanici.displayName.isNullOrEmpty())"Tanımlanmadi" else kullanici.displayName
            tvUserMail.text=kullanici.email
            tvUserId.text=kullanici.uid
        }
    }

    private fun initAuthStateListenir() {
        myAuthStateListenir=object:FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici=p0.currentUser

                if(kullanici!=null)
                {

                }else{
                    var intent= Intent(this@MainActivity,LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)//Mainden çıkmışsa geri gelmesini engellemek için hafızadan silme
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.menuCikisyap->{
                cikisyap()
                return true
            }
            R.id.hesapAyarlari->{
                var intent=Intent(this,KullaniciAyarlariActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cikisyap() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onResume() {
        super.onResume()
        kullanciKontrol()
        setKullaniciBilgileri()
    }

    private fun kullanciKontrol() {
      var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici==null  ){
            var intent= Intent(this@MainActivity,LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)//Mainden çıkmışsa geri gelmesini engellemek için hafızadan silme
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListenir)
    }

    override fun onStop() {
        super.onStop()
        if(myAuthStateListenir!=null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListenir)
    }


    }
}
