package com.example.pezosergio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pezosergio.databinding.ActivityMainListBinding

class MainList: AppCompatActivity() {

    private lateinit var binding: ActivityMainListBinding

    private var adapter : MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_list)
        binding = ActivityMainListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        adapter = MyAdapter(applicationContext)
        binding.lvNodes.adapter = adapter
        binding.lvNodes.setOnItemLongClickListener{ parent, view, position, id ->
            Global.bezierNodes.removeAt(position)
            Global.bezierOrderNodes.removeAt(position)
            Toast.makeText(applicationContext, "Node removed", Toast.LENGTH_LONG).show()
            intent = Intent(applicationContext, MainList::class.java)
            startActivity(intent)
            true
        }

        binding.txtHelper.text = if (Global.bezierNodes.size == 0) {
            "No nodes to show"
        } else {
            "Long press on a node to remove it"
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.mnuListingNodes -> {
                Toast.makeText(applicationContext, "You are already in listing view", Toast.LENGTH_LONG).show()
            }
            R.id.mnuGenerateCurves -> {
                intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
