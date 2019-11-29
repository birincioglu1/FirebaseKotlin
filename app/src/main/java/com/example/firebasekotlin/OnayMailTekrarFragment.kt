package com.example.firebasekotlin


import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firebase.ui.auth.ui.email.EmailActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_dialog.view.*

/**
 * A simple [Fragment] subclass.
 */
class OnayMailTekrarFragment : DialogFragment() {

    lateinit var emailEditText: EditText
    lateinit var sifreEditText: EditText
    lateinit var mContext:FragmentActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_dialog, container, false)

        emailEditText=view.findViewById(R.id.etDialogMail)
        sifreEditText=view.findViewById(R.id.etDialogSifre)
        mContext=activity!!
        var btnIptal=view.findViewById<Button>(R.id.btnDialogIptal)
        btnIptal.setOnClickListener {
            dialog.dismiss()
        }
        var btnGonder=view.findViewById<Button>(R.id.btnDialogGonder)
        btnGonder.setOnClickListener{
            if(emailEditText.text.toString().isNotEmpty()&&sifreEditText.text.toString().isNotEmpty())
            {
                girisYapveOnayMailiTekrarGonder(emailEditText.text.toString(),sifreEditText.text.toString())
                dialog.dismiss()

            }else
            {
                Toast.makeText(mContext,"Boş Alanlari Doldurunuz !",Toast.LENGTH_SHORT).show()
            }



        }




        return view
    }

    private fun girisYapveOnayMailiTekrarGonder(email: String, sifre: String) {

        var credential=EmailAuthProvider.getCredential(email,sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener{task ->
                if(task.isSuccessful)
                {
                    onayMailiniTekrarGonder()
                }else{
                    Toast.makeText(mContext,"Email ya da şifre Hatali",Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun onayMailiniTekrarGonder() {
        var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici!=null)
        {
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful)
                        {
                            Toast.makeText(mContext,"Mail Kutunuzu Kontrol Ediniz",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(mContext,"Mail Gönderilirken Sorun Oluştu"+p0.exception,Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                        }
                    }

                })
        }

    }


}
