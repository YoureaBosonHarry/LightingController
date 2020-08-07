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
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private val url = "192.168.50.102:80"
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
                handleColorChange(selectedColor, id, selectedColor.toUInt().toString(16), name)
            }
            .setPositiveButton(
                "ok"
            ) { _, _, _ ->
                println("Selected")
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ -> }
            .build()
            .show()
    }

    private fun handleColorChange(selectedColor: Int, id: Int, argb: String, name: String){
        if (resources.getResourceEntryName(id).takeLast(1).toInt() == 8){
            for (i in 0..7) {
                val buttonId = resources.getIdentifier("button$i", "id", packageName)
                sendPattern(i,"solid", argb.takeLast(6), 1.0)
                findViewById<Button>(buttonId).setBackgroundColor(selectedColor)
            }
        }
        else {
            sendPattern(id, "solid", argb, 1.0)
            val button = findViewById<Button>(id)
            button.setBackgroundColor(selectedColor)
        }
    }

    private fun sendPattern(lanternId: Int, pattern: String, hexColor: String, frequency: Double) {
        val url = "http://$url/sendPattern?id=$lanternId&pattern=$pattern&hexColor=$hexColor&frequency=$frequency"
        println(url)
        Fuel.post(url)
            .timeout(400)
            .responseString {_, _, response -> println(response.component1())}
    }
}