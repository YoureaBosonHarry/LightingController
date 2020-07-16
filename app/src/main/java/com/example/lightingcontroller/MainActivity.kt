package com.example.lightingcontroller

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.lightingcontroller.ui.main.SectionsPagerAdapter
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private val url = "192.168.50.102"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                println("Selected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                println("Reselected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                println("Unselected")
            }
        })
    }

    fun lanternButtonClicked(view: View){
        val id = view.id
        val name = resources.getResourceEntryName(id)
        showColorPicker(name, id)
    }

    private fun showColorPicker(name: String, id: Int) {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose Lantern Color")
            .initialColor(Color.argb(255, 255, 255,255 ))
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener { selectedColor ->
               println("Change")
            }
            .setPositiveButton(
                "ok"
            ) { _, selectedColor, _ ->
                changeBackgroundColor(selectedColor, id)
                val argb = selectedColor.toUInt().toString(16)
                sendPattern(name.takeLast(1).toInt(),"solid", argb.takeLast(6), 1.0)
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ -> }
            .build()
            .show()
    }

    private fun changeBackgroundColor(selectedColor: Int, id: Int) {
        if (resources.getResourceEntryName(id).takeLast(1).toInt() == 0){
            for (i in 1..8) {
                val buttonId = resources.getIdentifier("button$i", "id", packageName)
                findViewById<Button>(buttonId).setBackgroundColor(selectedColor)
            }
        }
        else {
            val button = findViewById<Button>(id)
            button.setBackgroundColor(selectedColor)
        }
    }

    private fun sendPattern(lanternId: Int, pattern: String, hexColor: String, frequency: Double) {
        val url = "http://$url/sendPattern?id=$lanternId&pattern=$pattern&hexColor=$hexColor&frequency=$frequency"
        println(url)
        //Fuel.post(url)
        //    .timeout(400)
        //    .responseString {_, _, response -> println(response.component1())}
    }
}