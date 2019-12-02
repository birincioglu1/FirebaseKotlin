package com.example.firebasekotlin


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth


class SifremiunuttumDialogFragment : DialogFragment() {

    lateinit var emailEditText: EditText

    lateinit var mContext: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_sifremiunuttum_dialog, container, false)
       mContext=activity!!
        emailEditText=view.findViewById(R.id.etSfireyiTekraGonder)
        var btnIptal=view.findViewById<Button>(R.id.btnSifreyiUnuttumIptal)
        btnIptal.setOnClickListener {
            dialog?.dismiss()
        }
        var btnGonder=view.findViewById<Button>(R.id.btnSifreyiUnuttumGonder)
        btnGonder.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailEditText.text.toString())
                .addOnCompleteListener{task ->
                    if (task.isSuccessful)
                    {
                        Toast.makeText(mContext,"Şifre sıfırlama gonderildi",Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }else
                    {
                        Toast.makeText(mContext,"Hata Oluştu"+task.exception,Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }
                }

        }
        return view
    }


}
