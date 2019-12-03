package com.example.firebasekotlin


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

/**
 * A simple [Fragment] subclass.
 */
class ProfilResmiFragment : DialogFragment() {

    lateinit var tvGaleridenSec:TextView
    lateinit var tvKameradanSec:TextView

    interface onProfilResimListenir{
        fun getResimYolu(resimPath: Uri?)
        fun getResimBitmap(bitmap: Bitmap)


    }
    lateinit var myProfilResimListenenir:onProfilResimListenir

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_profil_resmi, container, false)
        tvGaleridenSec=view.findViewById(R.id.yeniGaleridenFoto)
        tvKameradanSec=view.findViewById(R.id.yeniKameradanFoto)

        tvGaleridenSec.setOnClickListener{
            var intent=Intent(Intent.ACTION_GET_CONTENT)//Galeriden Seçne
            intent.type="image/*"
            startActivityForResult(intent,100)


        }
        tvKameradanSec.setOnClickListener {
            var intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if(requestCode==100&& requestCode== Activity.RESULT_OK&&data!=null)
        {
            var galeridenSecilenResimYolu=data.data
            myProfilResimListenenir.getResimYolu(galeridenSecilenResimYolu)
            dialog?.dismiss()

        }else if(requestCode==200 && requestCode== Activity.RESULT_OK&&data!=null){
            var kameredanCekilenResim:Bitmap
            kameredanCekilenResim=data.extras?.get("data") as Bitmap
            myProfilResimListenenir.getResimBitmap(kameredanCekilenResim)
            dialog?.dismiss()

        }else{

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        myProfilResimListenenir=activity as onProfilResimListenir
        super.onAttach(context)

    }
}
