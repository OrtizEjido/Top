package com.example.top

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.top.utils.Artista
import com.example.top.utils.DatePickerFragment
import kotlinx.android.synthetic.main.activity_add_artist.*
import kotlinx.android.synthetic.main.content_detalle.etApellidos
import kotlinx.android.synthetic.main.content_detalle.etEstatura
import kotlinx.android.synthetic.main.content_detalle.etFechaNacimiento
import kotlinx.android.synthetic.main.content_detalle.etNombre
import java.text.SimpleDateFormat
import java.util.*

class AddArtistActivity: AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var mArtista: Artista? =null
    lateinit var mCalendar: Calendar

    companion object{
        var RC_PHOTO_PICKER = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_artist)
        configActionBar()
        configArtista(intent)
        configCalendar()
        configListeners()



    }

    private fun configActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configArtista(intent: Intent) {
        mArtista = Artista()
        this.mArtista!!.fechaDeNacimiento = System.currentTimeMillis()
        mArtista!!.orden = intent.getIntExtra(Artista.ORDEN, 0)
    }

    private fun configCalendar() {
        mCalendar = Calendar.getInstance(Locale.ROOT)
        etFechaNacimiento.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                System.currentTimeMillis()
            )
        )
    }

    @SuppressLint("CheckResult")
    private fun configImageView(fotoUrl: String?) {
        if (fotoUrl != null) {
            val options = RequestOptions()
            options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
            Glide.with(this)
                .load(fotoUrl)
                .apply(options)
                .into(imgFoto)
        } else {
            imgFoto.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_photo_size_select_actual
                )
            )
        }
        mArtista!!.fotoUrl = fotoUrl
    }

    private fun showAddPhotoDialog() {
        val etFotoUrl = EditText(this)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle(R.string.addArtist_dialogUrl_title)
            .setPositiveButton(R.string.label_dialog_add,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    configImageView(
                        etFotoUrl.text.toString().trim { it <= ' ' })
                })
            .setNegativeButton(R.string.label_dialog_cancel, null)
        builder.setView(etFotoUrl)
        builder.show()
    }

    private fun configListeners(){
        imgFromUrl.setOnClickListener {
            showAddPhotoDialog()
        }
        imgDeleteFoto.setOnClickListener {
            var builder = AlertDialog.Builder(this).setTitle(R.string.detalle_dialogDelete_title)
                .setMessage(String.format(Locale.ROOT, getString(R.string.detalle_dialogDelete_message), mArtista!!.nombreCompleto()))
                .setPositiveButton(R.string.label_dialog_delete, DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                    configImageView(null)
                } )
                .setNegativeButton(R.string.label_dialog_cancel, null)
            builder.show()
        }
        imgFromGallery.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image.jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, getString(R.string.detalle_chooser_title)), RC_PHOTO_PICKER)
        }
        etFechaNacimiento.setOnClickListener {
            val selectorFecha = DatePickerFragment()
            selectorFecha.listener = this@AddArtistActivity
            val args = Bundle()
            args.putLong(DatePickerFragment.FECHA, mArtista?.fechaDeNacimiento!!)
            selectorFecha.arguments = args
            selectorFecha.show(supportFragmentManager, DatePickerFragment.SELECTED_DATE)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (etEstatura.text.toString().trim().isEmpty() ||
            Integer.valueOf(
                etEstatura.text.toString().trim()
            ) < resources.getInteger(R.integer.estatura_min)
        ) {
            etEstatura.error = getString(R.string.addArtist_error_estaturaMin)
            etEstatura.requestFocus()
            isValid = false
        }
        if (etApellidos.text.toString().trim().isEmpty()) {
            etApellidos.error = getString(R.string.addArtist_error_required)
            etApellidos.requestFocus()
            isValid = false
        }
        if (etNombre.text.toString().trim().isEmpty()) {
            etNombre.error = getString(R.string.addArtist_error_required)
            etNombre.requestFocus()
            isValid = false
        }
        return isValid
    }

    private fun saveArtist() {
        if (validateFields()) {
            mArtista?.nombre = etNombre.text.toString()
            mArtista?.apellidos = etApellidos.text.toString()
            mArtista?.estatura = etEstatura.text.toString().toShort()
            mArtista?.lugarDeNacimiento = etLugarNacimiento.toString()
            mArtista?.notas = etNotas.text.toString()
            //mArtista.orden = mArtista?.orden
            //mArtista.fotoUrl = mArtista?.fotoUrl
            try {
                MainActivity.artistaDAO.insert(mArtista!!)
                Log.i("DBFlow", "Inserción correcta de datos.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("DBFlow", "Error al insertar datos.")
                //setResult(RESULT_OK)
            }
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_save -> saveArtist()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        mCalendar.timeInMillis = System.currentTimeMillis()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = day

        etFechaNacimiento.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                mCalendar.timeInMillis
            )
        )
        mArtista?.fechaDeNacimiento = mCalendar.timeInMillis
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                RC_PHOTO_PICKER ->configImageView(data!!.dataString)
            }
        }
    }

}