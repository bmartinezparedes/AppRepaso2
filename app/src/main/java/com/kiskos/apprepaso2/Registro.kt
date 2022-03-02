package com.kiskos.apprepaso2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Registro : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val TAG = "RealTime"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        //Inicializar FireBase Auth
        auth = Firebase.auth
        //Inicializo BD RealTime
        database = Firebase.database("https://apprepaso-c63ee-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val crearCuenta: Button = findViewById(R.id.bCrearCuenta)
        crearCuenta.setOnClickListener{
            //Declaro la variable de usuario para recojer el texto
            val usuario: EditText = findViewById(R.id.entradaCorreo)
            //Declaro la variable de contraseña para recojer el texto
            val contraseña: EditText = findViewById(R.id.entradaCorreo)
            //Llamo al metodo donde de crear cuenta y le paso de variables el correo y la contraseña
            crearCuenta(usuario.text.toString(),contraseña.text.toString())

            val intent = Intent(this, ClienteActivity::class.java).apply {
                /*
                 *Con finish() impedimos que retorne al anterior activity donde tenemos
                 *crear cuenta y no queremos que de recuperado esos datos,
                 *obligando a que se logue de nuevo
                 */
                finish()
            }
            startActivity(intent)
        }
    }
    /**
     * @param email Correo del usuario
     * @param password Contraseña del usuario
     */
    private fun crearCuenta(email: String, password: String) {
        //Crear nuevo usuario
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Iniciar sesión correctamente, actualizar la interfaz de usuario con la información del usuario que inició sesión
                    Log.i("bien", "Crear Usuario Bien")
                    val user = auth.currentUser
                    updateUI(user)
                    val usuario: EditText = findViewById(R.id.entradaCorreo)
                    val nombre: EditText = findViewById(R.id.entradaNombre)
                    val primerApellido: EditText = findViewById(R.id.entradaApellido1)
                    val segundoApellido:EditText = findViewById(R.id.entradaApellido2)
                    val user2 = Cliente(nombre.text.toString(),primerApellido.text.toString(),
                        segundoApellido.text.toString())
                    subirDatosRealTime(usuario.text.toString(),user2,user)
                } else {
                    // Si el inicio de sesión falla, muestre un mensaje al usuario.
                    Log.i("mal", "Crear Usuario Mal", task.exception)
                    Toast.makeText(baseContext, "Fallo en autenticación.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [Fin Crear Cuenta]
    }
    private fun updateUI(user: FirebaseUser?) {

    }

    /**
     * @param correo Correo del usuario que usaremos como id en la BD
     * @param user Tipo
     */
    private fun subirDatosRealTime(correo:String, user:Cliente, uid: FirebaseUser?){
        Log.d(TAG,"Escribiendo Datos")
        Log.d(TAG, intent.getStringExtra(EXTRA_MESSAGE).toString())
        database.child(intent.getStringExtra(EXTRA_MESSAGE).toString()+"/"+uid!!.uid).setValue(user)
        Log.d(TAG,"Datos Escritos")
    }
}