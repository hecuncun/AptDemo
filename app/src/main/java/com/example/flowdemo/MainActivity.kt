package com.example.flowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hcc.annotation.BindView
import com.hcc.annotation.OnClick

class MainActivity : AppCompatActivity() {
    @BindView(R.id.textview1)
    lateinit var textView1 :TextView
    @BindView(R.id.textview2)
    lateinit var textView2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        textView1.setText("DDDDD");

    }

    @OnClick(R.id.textview1,R.id.textview2)
    fun onClick(view: View){
        when(view.id){
            R.id.textview1->{
                textView1.text = "11111"
            }
            R.id.textview2->{
                textView2.text = "11111"
            }
        }

    }

}