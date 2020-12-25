package com.example.projectaasaruna

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Activity : AppCompatActivity() {
    lateinit var list: ArrayList<Model>
    lateinit var adapter: Adapter
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        list = arrayListOf()
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView = findViewById(R.id.recycler)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = mLayoutManager
        adapter = Adapter(this, list)
        load()
    }

    private fun load() {
        val query = FirebaseDatabase.getInstance().getReference("Audio")
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                list.clear()
                if(p0.exists()) {
                    p0.children.forEach {
                        val value = it.getValue(Model::class.java)
                        if(value != null) {
                            list.add(value)
                        }
                    }
                    mRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}