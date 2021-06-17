package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import android.annotation.SuppressLint
import com.google.firebase.firestore.GeoPoint

class LocationData {
    var name: String = ""
    var point1: GeoPoint = GeoPoint(0.0, 0.0)
    var point2: GeoPoint = GeoPoint(0.0, 0.0)
    var descripcion: String = ""

    override fun toString(): String {
        return name + "\n" + point1.latitude + ", " + point1.longitude + "\n" +
                point2.latitude + ", " + point2.longitude + descripcion
    }

    @SuppressLint("SetTextI18n")
    fun pointHere(posicionActual: GeoPoint): Boolean {
        if (posicionActual.latitude in point1.latitude..point2.latitude)
            if (invert(posicionActual.longitude) in invert(point1.longitude)..invert(point2.longitude))
                return true
        return false
    }

    private fun invert(valor: Double): Double {
        return valor * -1
    }


}