package com.example.firebasekotlin

class Kullanici {

    var isim: String? = null
    var telefon: String? = null
    var profil_resmi: String? = null
    var seviye: String? = null
    var user_id: String? = null

    constructor(
        isim: String,
        telefon: String,
        profil_resmi: String,
        seviye: String,
        user_id: String
    ) {
        this.isim = isim
        this.telefon = telefon
        this.profil_resmi = profil_resmi
        this.seviye = seviye
        this.user_id = user_id
    }

    constructor() {

    }
}
