package com.udacity.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.utils.Constants.FILE
import com.udacity.utils.Constants.STATUS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        tvFileName.text = intent.getStringExtra(FILE)
        tvStatus.text = intent.getStringExtra(STATUS)
        okButton.setOnClickListener { finish() }
    }
}