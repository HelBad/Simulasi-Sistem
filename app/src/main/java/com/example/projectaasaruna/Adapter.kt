package com.example.projectaasaruna

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class Adapter(val context: Context, val list: ArrayList<Model>): RecyclerView.Adapter<Adapter.Holder>() {
    lateinit var waktu:String
    var mp = MediaPlayer()

    inner class Holder(view: View?) : RecyclerView.ViewHolder(view!!), View.OnClickListener {
        val mJudul = view?.findViewById(R.id.judul) as TextView
        val mKeterangan = view?.findViewById(R.id.keterangan) as TextView
        val mDurasi = view?.findViewById(R.id.durasi) as TextView
        val mPlay = view?.findViewById(R.id.play) as ImageView
        val mStop = view?.findViewById(R.id.stop) as ImageView

        fun bind(context: Context, list: Model) {
            mJudul.text = list.judul
            mKeterangan.text = list.keterangan
            mDurasi.text = list.durasi

            mPlay.setOnClickListener {
                Toast.makeText(context, "Playing Audio", Toast.LENGTH_SHORT).show()
                val query = FirebaseDatabase.getInstance().getReference("Audio")
                query.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(datasnapshot: DataSnapshot) {
                        for (snapshot1 in datasnapshot.children) {
                            val allocation = snapshot1.getValue(Model::class.java)
                            if(mJudul.text.toString() == allocation!!.judul) {
                                query.orderByChild("judul").equalTo(mJudul.toString())

                                waktu = allocation.durasi
                                val millisInFuture:Long = (1000 * (waktu).toLong())
                                val countDownInterval:Long = 1000
                                timer(millisInFuture, countDownInterval).start()

                                val mediaPlayer = MediaPlayer()
                                mp = mediaPlayer
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                                mediaPlayer.setDataSource(allocation.url)
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            mStop.setOnClickListener {
                val intent = Intent(context, Activity::class.java)
                context.startActivity(intent)
                mp.stop()
            }
        }

        private fun timer(millisInFuture: Long, countDownInterval: Long): CountDownTimer {
            return object: CountDownTimer(millisInFuture, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    val timeRemaining = timeString(millisUntilFinished)
                    mDurasi.text = timeRemaining
                    mPlay.visibility = View.GONE
                    mStop.visibility = View.VISIBLE
                }

                override fun onFinish() {
                    mDurasi.text = waktu
                    mp.stop()
                    mStop.visibility = View.GONE
                    mPlay.visibility = View.VISIBLE
                }
            }
        }

        private fun timeString(millisUntilFinished: Long): String {
            var millisUntilFinished: Long = millisUntilFinished
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
            return String.format(Locale.getDefault(), "%2d",seconds)
        }

        override fun onClick(v: View?) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_audio, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(context, list[position])
    }
}