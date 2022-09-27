package com.sakshigarg.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakshigarg.musicplayer.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestRuntimePermission()
        setTheme(R.style.coolPinkNav)
        binding= ActivityMainBinding.inflate(layoutInflater)
        //binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FOR NAV DRAWER
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission())
             initializeLayout()


        binding.shufflebutton.setOnClickListener {

            val intent= Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)

        }
        binding.fvtbutton.setOnClickListener {
            val intent= Intent(this@MainActivity, FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistbutton.setOnClickListener {
            val intent= Intent(this@MainActivity, PlaylistActivity::class.java)
            startActivity(intent)
        }

        //initializing navview

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navFeedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.navSettings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.navAbout -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> exitProcess(1)
            }
            true
        }
    }
    // For requesting permission
    private fun requestRuntimePermission(): Boolean{
    if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
        return false
    }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==13){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            }

            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)

        }

//        while(requestCode!=13){
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
//
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){

        MusicListMA= getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager= LinearLayoutManager(this@MainActivity)
        musicAdapter= MusicAdapter(this, MusicListMA )
        binding.musicRV.adapter = musicAdapter

        binding.totalsong.text= " Total Songs : "+musicAdapter.itemCount


    }

    @SuppressLint("Recycle", "Range")
    private fun getAllAudio():ArrayList<Music>{
        val templist= ArrayList<Music>()
         val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION , MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor= this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection,
            null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
        if(cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titlec= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idc= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumc= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistc= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathc= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationc= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumidc= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri= Uri.parse("content://media/external/audio/albumart")
                    val artUric= Uri.withAppendedPath(uri, albumidc).toString()
                    val music= Music(id= idc, title = titlec, album = albumc, artist = artistc, path = pathc, duration = durationc,
                    artUri= artUric)
                    val file= File(music.path)
                    if(file.exists())
                        templist.add(music)
                } while (cursor.moveToNext())
            cursor.close()

        }


        return templist
    }
}