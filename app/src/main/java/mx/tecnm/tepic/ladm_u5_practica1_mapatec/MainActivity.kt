package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u5_practica1_mapatec.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loc: LocationManager
    var db = FirebaseFirestore.getInstance()
    var pos = ArrayList<LocationData>()
    private val DataArray: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }


        loc = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = ListenerLocation(this, binding)
        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, listener)

        getLocation()

    }

    fun getLocation() {
        db.collection("tecnologico").orderBy("nombre")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    showToastMsg(this, "ERROR: " + firebaseFirestoreException.message, 1)
                    return@addSnapshotListener
                }


                DataArray.clear()
                pos.clear()


                for (document in querySnapshot!!) {
                    val data = LocationData()
                    data.name = document.getString("nombre").toString()
                    data.point1 = document.getGeoPoint("pos1")!!
                    data.point2 = document.getGeoPoint("pos2")!!
                    data.descripcion = document.getString("descripcion").toString()
                    pos.add(data)
                    DataArray.add(data.name)

                }

                val list = ArrayAdapter(this, R.layout.simple_list_item_1, DataArray)
                binding.listUbi.adapter = list
            }
        binding.listUbi.setOnItemClickListener { parent, view, position, id ->
            db.collection("tecnologico")
                .whereEqualTo("nombre", DataArray.get(position))
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        showToastMsg(this, "Sin conexion", 0)
                        return@addSnapshotListener
                    }

                    var p = ""
                    var nombre = ""
                    var posicionLatitud = 0.0
                    var posicionLongitud = 0.0

                    for (document in querySnapshot!!) {
                        nombre = document.getString("nombre").toString()
                        posicionLatitud = document.getGeoPoint("place")!!.latitude
                        posicionLongitud = document.getGeoPoint("place")!!.longitude

                        p = "Edificio: \n${document.getString("nombre")} \n\n" +
                                "Ubicacion: \n" +
                                "[${document.getGeoPoint("place")!!.latitude}, ${document.getGeoPoint("place")!!.longitude}]\n\n" +
                                if(document.getString("descripcion")!=".")"Aqui hay: ${document.getString("descripcion")}" else ""
                    }

                    AlertDialog.Builder(this)
                        .setMessage("Seleccionaste:\n\n" + p)
                        .setPositiveButton("Abrir mapa") { _, _ ->
                            val otraVentana = Intent(this, MapsActivity::class.java)
                            otraVentana.putExtra("latitud", posicionLatitud)
                            otraVentana.putExtra("longitud", posicionLongitud)
                            otraVentana.putExtra("nombre", nombre)
                            startActivity(otraVentana)
                        }
                        .setNegativeButton("Cancelar") { _, _ -> }
                        .show()

                }
        }
    }

    private fun showToastMsg(c: Context, msg: String, time: Int) {
        val toast = Toast.makeText(c, msg, time)
        toast.show()
    }
}
