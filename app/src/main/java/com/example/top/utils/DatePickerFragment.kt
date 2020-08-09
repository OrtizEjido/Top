package com.example.top.utils

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment

class DatePickerFragment : DialogFragment(), OnDateSetListener {
    companion object {
        val FECHA = "fecha"
        val SELECTED_DATE = "selectedDate"
        fun newInstance(listener: OnDateSetListener): DatePickerFragment {
            val fragment = DatePickerFragment()

            fragment.listener = listener
            return fragment
        }
    }


    public var listener: OnDateSetListener? = null
    //Una madre que se tiene que agregar por compatibilidad
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance()
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = activity?.let { DatePickerDialog(it, listener, year, month, day) }

        //El min y el max de fecha se cuentan desde el dia actual
        //Establecemos un minimo para el año a seleccionar
        c.set(Calendar.YEAR, year)
        if (datePickerDialog != null) {
            datePickerDialog.datePicker.minDate = c.timeInMillis
        }
        //Establecemos un max para la fecha a colocar
        c.set(Calendar.YEAR, year + 1)
        if (datePickerDialog != null) {
            datePickerDialog.datePicker.maxDate = c.timeInMillis
        }

        return datePickerDialog!!
    }



    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

}