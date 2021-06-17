package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.widget.Toast
import com.google.firebase.firestore.GeoPoint
import mx.tecnm.tepic.ladm_u5_practica1_mapatec.databinding.ActivityMainBinding

class ListenerLocation(puntero:MainActivity, binding: ActivityMainBinding) : LocationListener {
    private var p = puntero
    private var b = binding

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        b.textView.text = "${location.latitude}, ${location.longitude}"
        val geoPosGPS = GeoPoint(location.latitude,location.longitude)

        for(item in p.pos){
            if(item.pointHere(geoPosGPS)){
                b.textView.text = "\"${item.name}\""
                showToastMsg(p, item.name)
            }
        }
    }
    private fun showToastMsg(c: Context, msg: String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT)
        toast.show()
    }
}