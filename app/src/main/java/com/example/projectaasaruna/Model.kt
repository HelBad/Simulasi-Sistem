package com.example.projectaasaruna

class Model {
    lateinit var judul: String
    lateinit var keterangan: String
    lateinit var durasi: String
    lateinit var url: String

    constructor(){}
    constructor(judul: String, keterangan: String, durasi: String, url: String) {
        this.judul = judul
        this.keterangan = keterangan
        this.durasi = durasi
        this.url = url
    }
}