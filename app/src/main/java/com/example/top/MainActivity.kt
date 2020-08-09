package com.example.top

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.top.adapters.ArtistaAdapter
import com.example.top.adapters.OnItemClickListener
import com.example.top.database.TopDB
import com.example.top.utils.Artista
import com.example.top.utils.ArtistaDAO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.String
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClickListener {

    companion object{
        lateinit var artistaDAO: ArtistaDAO
    }

    private var adapter: ArtistaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        artistaDAO = TopDB.getInstance(this).artistaDao()
        configAdapter()
        configRecyclerView()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(this@MainActivity, AddArtistActivity::class.java)
            intent.putExtra(Artista.ORDEN, adapter!!.itemCount + 1)
            //startActivity(intent)

            startActivityForResult(intent, 1)
        }
        if (artistaDAO.getCount() == 0) generateArtist()
    }


    private fun generateArtist() {
        val nombres =
            arrayOf("Israel", "Jorge", "Manuel", "Javi")
        val apellidos =
            arrayOf("Zyk", "Nieto", "Soto", "Garcia")
        val nacimientos =
            longArrayOf(280108800000L, 470469600000L, 228031200000L, 483667200000L)
        val lugares =
            arrayOf("Canada", "USA", "USA", "Israel")
        val estaturas = shortArrayOf(163, 173, 163, 178)
        val notas = arrayOf(
            "Israel Miranda Diaz es el pana que prepara los mejores tragos",
            "JOvenciTO buen pedo",
            "El mas papi del tec",
            "Regresa la bocina puto"
        )
        val fotos = arrayOf(
            "https://scontent.ftij3-2.fna.fbcdn.net/v/t1.0-9/79433219_10215602920092530_3190569975421075456_o.jpg?_nc_cat=106&_nc_sid=09cbfe&_nc_ohc=7OwbdPPXvvQAX_zgdRo&_nc_ht=scontent.ftij3-2.fna&oh=c1363da0eb348eceb87320270a8fdd56&oe=5F4492E5",
            "https://scontent.ftij3-2.fna.fbcdn.net/v/t1.0-9/75496031_2354578764857391_959384913834934272_o.jpg?_nc_cat=105&_nc_sid=09cbfe&_nc_ohc=OVz88f6ORDMAX_PwgU8&_nc_ht=scontent.ftij3-2.fna&oh=1784efa9609f3c8f0af7627b84ac2c46&oe=5F433F76",
            "https://scontent.ftij3-2.fna.fbcdn.net/v/t1.0-9/80684313_1979595002186363_3712518946969092096_o.jpg?_nc_cat=109&_nc_sid=09cbfe&_nc_ohc=7qXY92W7n0IAX_aw56c&_nc_ht=scontent.ftij3-2.fna&oh=a26ba25b88474c0cf8e33dc38e3ce700&oe=5F4AE351",
            "https://scontent.ftij3-2.fna.fbcdn.net/v/t1.0-9/36948422_2075040372555419_8187733607926726656_o.jpg?_nc_cat=107&_nc_sid=09cbfe&_nc_ohc=OTcm2vwNe4AAX-Vp8O3&_nc_ht=scontent.ftij3-2.fna&oh=fcd01ce7dd8fcb2965579af824cf084f&oe=5F47657A"
        )

        //for (int i = 0; i < 7; i++) {
        for (i in nombres.indices) {
            val artista = Artista(
                nombres[i], apellidos[i], nacimientos[i], lugares[i],
                estaturas[i], notas[i], i + 1, fotos[i]
            )
            //adapter.add(artista);
            try {
                artistaDAO.insert(artista)
                Log.i("ROOM", "Inserción correcta de datos.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("ROOM", "Error al insertar datos.")
            }
        }
    }



    private fun configAdapter() {
        adapter = ArtistaAdapter(artistaDAO.getAll(), this)
    }

    private fun configRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> resetDatabase()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(artista: Artista?) {
//        sArtista.id = artista?.id
//        sArtista.nombre = artista?.nombre
//        sArtista.apellidos = artista?.apellidos
//        sArtista.fechaDeNacimiento = artista?.fechaDeNacimiento
//        sArtista.estatura = artista?.estatura
//        sArtista.lugarDeNacimiento = artista?.lugarDeNacimiento
//        sArtista.orden = artista?.orden
//        sArtista.notas = artista?.notas
//        sArtista.fotoUrl = artista?.fotoUrl
        Artista.NUMERO = artista!!.id!!
        val intent = Intent(this@MainActivity, DetalleActivity::class.java)
        intent.putExtra(Artista.ID, artista.id)
        startActivity(intent)
    }

    private fun resetDatabase(){
        artistaDAO.clearAll()
        generateArtist()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLongItemClick(artista: Artista?) {
        val vibrator =
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(60L, VibrationEffect.DEFAULT_AMPLITUDE))
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle(R.string.main_dialogDelete_title)
            .setMessage(
                String.format(
                    Locale.ROOT, getString(R.string.main_dialogDelete_message),
                    artista!!.nombreCompleto()
                )
            )
            .setPositiveButton(
                R.string.label_dialog_delete,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    try {
                        artistaDAO.delete(artista)
                        adapter!!.remove(artista)
                        showMessage(R.string.main_message_delete_success)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        showMessage(R.string.main_message_delete_fail)
                    }
                })
            .setNegativeButton(R.string.label_dialog_cancel, null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        configAdapter()empleo
        adapter?.artistas = artistaDAO.getAll()
        configRecyclerView()
    }

    private fun showMessage(resource: Int) {
        Snackbar.make(containerMain, resource, Snackbar.LENGTH_SHORT).show()
    }

//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
//            artistaDAO.insert(sArtista)
//            adapter!!.add(sArtista)
//        }
//    }

}