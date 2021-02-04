package com.example.rxmvp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PostsAdapter
    private lateinit var api: MyApi
    private lateinit var rvPosts: RecyclerView
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeRecyclerViewPosts()
        initializeApi()
        retrievePosts()
    }

    private fun initializeRecyclerViewPosts() {
        rvPosts = findViewById(R.id.rvPosts)
        adapter = PostsAdapter()
        rvPosts.adapter = adapter
    }

    private fun initializeApi() {
        api = RetrofitClient.retrofit.create(MyApi::class.java)
    }

    private fun retrievePosts() {
        //Incluimos le siguiente Observable al CompositeDisposable
        compositeDisposable.add(
                //Forma resumida
                api.getPosts()
//                .map { it.filter { it.title.contains("sunt aut") } } //Filtramos los datos que se obtienen
                        .subscribeOn(Schedulers.io()) //Indica el hilo dónde se ejecutarán las líneas de código anteriores
                        .doOnSubscribe { Toast.makeText(this, "On Subscribe", Toast.LENGTH_LONG).show() }
                        .subscribeOn(AndroidSchedulers.mainThread())//Indica el hilo dónde se ejecutarán las líneas de Código anteriores, y como no hay otro subscribeOn más adelante, las siguientes también se ejecutarán en este hilo
                        .observeOn(AndroidSchedulers.mainThread()) //Indicamos desde que hilo se "escuchará" la respuesta, como definir un listener
                        .subscribe({
                            adapter.posts = it
                            adapter.notifyDataSetChanged()
                        }, {
                            Log.e("MainActivity", "Error al obtener los posts", it)
                        })
        )
        //Forma verbosa
//        api.getPosts()
//                .subscribeOn(Schedulers.io()) //Indica el hilo dónde se ejecutarán las líneas de Código anteriores, y como no hay otro subscribeOn más adelante, las siguientes también se ejecutarán en este hilo
//                .observeOn(AndroidSchedulers.mainThread()) //Indicamos desde que hilo se "escuchará" la respuesta, como definir un listener
//                .subscribe(
//                        object : SingleObserver<List<Post>> {
//                            override fun onSuccess(posts: List<Post>) {
//                                adapter.posts = posts
//                                adapter.notifyDataSetChanged()
//                            }
//
//                            override fun onSubscribe(d: Disposable) {
//                                Log.i("MainActivity", "getPosts onSuscribe")
//                            }
//
//                            override fun onError(e: Throwable) {
//                                Log.e("MainActivity", "Error al obtener los posts", e)
//                            }
//                        }
//                )
    }

    override fun onDestroy() {
        //Libera todos los recursos asociados al proceso que contenga
        compositeDisposable.clear()
        super.onDestroy()
    }
}