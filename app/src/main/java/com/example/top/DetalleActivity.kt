package com.example.top

import android.annotation.SuppressLint
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detalle.*
import kotlinx.android.synthetic.main.content_detalle.*
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*

class DetalleActivity: AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var mArtista: Artista? = null
    private val mCalendar: Calendar? = null
    private var mMenuItem: MenuItem? = null
    private var mIsEdit = false

    companion object{
        const val RC_PHOTO_PICKER = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        configArtista()
        configActionBar()
        configImageView(mArtista?.fotoUrl)
        configCalendar()
        configListeners()
    }


    private fun configArtista() {
        mArtista = MainActivity.artistaDAO.getArtista(Artista.NUMERO)

        etNombre.setText(mArtista!!.nombre)
        etApellidos.setText(mArtista!!.apellidos)
        etFechaNacimiento.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
                .format(mArtista!!.fechaDeNacimiento)
        )
        etEdad.setText(mArtista!!.fechaDeNacimiento?.let { getEdad(it) })
        etEstatura.setText(String.valueOf(mArtista!!.estatura))
        etOrden.setText(String.valueOf(mArtista!!.orden))
        etLugarNacimiento.setText(mArtista!!.lugarDeNacimiento)
        etNotas.setText(mArtista!!.notas)
    }

    private fun getEdad(fechaNacimiento: Long): kotlin.String {
        val time =
            Calendar.getInstance().timeInMillis / 1000 - fechaNacimiento / 1000
        val years = Math.round(time.toFloat()) / 31536000
        return years.toString()
    }

    private fun configActionBar() {
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        configTitle()
    }

    private fun configTitle() {
        toolbar_layout.title = mArtista?.nombreCompleto()
    }

    @SuppressLint("CheckResult")
    private fun configImageView(fotoUrl: kotlin.String?) {
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
        mArtista?.fotoUrl = fotoUrl.toString()
    }

    private fun configCalendar() {}

    fun configListeners(){
        fab.setOnClickListener {
            saveOrEdit()
        }
        imgDeleteFoto.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle(R.string.detalle_dialogDelete_title)
            .setMessage(
                String.format(
                    Locale.ROOT,
                    getString(R.string.detalle_dialogDelete_message),
                    mArtista!!.nombreCompleto()
                )
            )
            .setPositiveButton(
                R.string.label_dialog_delete,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    savePhotoUrlArtist(
                        null
                    )
                })
            .setNegativeButton(R.string.label_dialog_cancel, null)
            builder.show() }
        imgFromGallery.setOnClickListener { val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.detalle_chooser_title)
                ), RC_PHOTO_PICKER
            ) }
        imgFromUrl.setOnClickListener {  }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> saveOrEdit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveOrEdit() {
        if (mIsEdit) {
            if (validateFields()) {
                mArtista!!.nombre = etNombre.text.toString().trim()
                mArtista!!.apellidos = etApellidos.text.toString().trim()
                mArtista!!.estatura = java.lang.Short.valueOf(
                    etEstatura.text.toString().trim()
                )
                mArtista!!.lugarDeNacimiento = etLugarNacimiento.text.toString().trim()
                mArtista!!.notas = etNotas.text.toString().trim()

                try {

                    MainActivity.artistaDAO.update(mArtista!!)
                    configTitle()
                    showMessage(R.string.detalle_message_update_success)
                    Log.i("DBFlow", "Inserción correcta de datos.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(R.string.detalle_message_update_fail)
                    Log.i("DBFlow", "Error al insertar datos.")
                }
            }
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account_edit))
            enableUIElements(false)
            mIsEdit = false
        } else {
            mIsEdit = true
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account_check))
            enableUIElements(true)
        }
    }

    private fun enableUIElements(enable: Boolean) {
        etNombre.isEnabled = enable
        etApellidos.isEnabled = enable
        etFechaNacimiento.isEnabled = enable
        etEstatura.isEnabled = enable
        etLugarNacimiento.isEnabled = enable
        etNotas.isEnabled = enable
        mMenuItem!!.isVisible = enable
        app_bar.setExpanded(!enable)
        containerMain.isNestedScrollingEnabled = !enable
    }

    private fun showMessage(resource: Int) {
        Snackbar.make(containerMain, resource, Snackbar.LENGTH_SHORT).show()
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

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        mCalendar!!.timeInMillis = System.currentTimeMillis()
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, day)

        etFechaNacimiento.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(
                mCalendar.timeInMillis
            )
        )
        mArtista?.fechaDeNacimiento = mCalendar.timeInMillis
        etEdad.setText(getEdad(mCalendar.timeInMillis))
    }

    fun onSetFecha() {
        val selectorFecha = DatePickerFragment()
        selectorFecha.listener = this@DetalleActivity
        val args = Bundle()
        args.putLong(DatePickerFragment.FECHA, mArtista!!.fechaDeNacimiento!!)
        selectorFecha.setArguments(args)
        selectorFecha.show(supportFragmentManager,DatePickerFragment.SELECTED_DATE)
    }

    private fun savePhotoUrlArtist(fotoUrl: kotlin.String?) {
        try {
            mArtista!!.fotoUrl = fotoUrl.toString()
            MainActivity.artistaDAO.update(mArtista!!)
            configImageView(fotoUrl)
            showMessage(R.string.detalle_message_update_success)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showMessage(R.string.detalle_message_update_fail)
        }
    }

    private fun showAddPhotoDialog() {
        val etFotoUrl = EditText(this)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle(R.string.addArtist_dialogUrl_title)
            .setPositiveButton(R.string.label_dialog_add,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    savePhotoUrlArtist(
                        etFotoUrl.text.toString()
                    )
                })
            .setNegativeButton(R.string.label_dialog_cancel, null)
        builder.setView(etFotoUrl)
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        mMenuItem = menu.findItem(R.id.action_save)
        mMenuItem!!.setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }

}