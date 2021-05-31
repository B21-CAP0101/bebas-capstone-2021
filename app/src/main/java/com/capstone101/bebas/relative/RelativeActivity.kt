package com.capstone101.bebas.relative

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone101.bebas.databinding.ActivityRelativeBinding
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.MapVal
import org.koin.android.ext.android.inject

class RelativeActivity : AppCompatActivity() {
    private var binding: ActivityRelativeBinding? = null
    private val bind get() = binding!!

    private val viewModel: RelativeViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelativeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val test = User("rudy1609", "", "", null, null, 2, listOf())
        val confirmTest = User("liong", "", "", null, null, 2, listOf())
        viewModel.getRelative { relatives ->
            bind.invite.setOnClickListener {
                viewModel.invite(relatives, test, true)
                Toast.makeText(this, "DONE", Toast.LENGTH_LONG).show()
            }
            bind.cancel.setOnClickListener {
                viewModel.invite(relatives, test, false)
                Toast.makeText(this, "DONE CANCEL", Toast.LENGTH_LONG).show()
            }
            bind.confirm.setOnClickListener {
                viewModel.confirm(relatives, confirmTest, true)
                Toast.makeText(this, "CONFIRMED", Toast.LENGTH_LONG).show()
            }
            bind.deny.setOnClickListener {
                viewModel.confirm(relatives, confirmTest, false)
                Toast.makeText(this, "DENIED", Toast.LENGTH_LONG).show()
            }
            bind.delete.setOnClickListener {
                viewModel.delete(
                    relatives, if (test.username != MapVal.user!!.username) test else confirmTest
                )
                Toast.makeText(this, "DIVORCED", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}