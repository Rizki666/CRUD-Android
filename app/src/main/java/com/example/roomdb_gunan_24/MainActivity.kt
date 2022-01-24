package com.example.roomdb_gunan_24

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdb_gunan_24.room.Constant
import com.example.roomdb_gunan_24.room.Movie
import com.example.roomdb_gunan_24.room.MovieDb
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy { MovieDb(this) }
    lateinit var movieAdapter:MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecycleView()

    }

    override fun onStart(){
        super.onStart()
        loaddata()
    }

    fun loaddata(){
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.moviedao().getMovies()
            Log.d("MainActivity", "dbresponse: $movies")
            withContext(Dispatchers.Main) {
                movieAdapter.setData(movies)
            }
        }
    }

    fun setupListener(){
        add_movie.setOnClickListener {
            intentEdit(0,Constant.TYPE_CREATE)
        }
    }

    fun intentEdit(movieid: Int, intentType: Int){
        startActivity(
            Intent(applicationContext, AddActivity::class.java)
            .putExtra("intent_id", movieid)
            .putExtra("intent_type", intentType)
        )
    }

    private fun setupRecycleView(){
        movieAdapter = MovieAdapter(arrayListOf(), object : MovieAdapter.OnAdapterListener{
            override fun onClick(movie: Movie) {
                // read
                    intentEdit(movie.id, Constant.TYPE_READ)
            }
                // update
            override fun onUpdate(movie: Movie) {
                    intentEdit(movie.id, Constant.TYPE_UPDATE)
            }
                // delete
            override fun onDelete(movies: Movie) {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.moviedao().deletemovie(movies)
                            loaddata()
                        }
                    }


        })
        rv_movie.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = movieAdapter
        }
    }
}

