package appjpm4everyone.webservice_googlemaps;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //Creo las instancias de los objetos de XML u otros
    Button coordenadas;
    EditText latitud, longitud;
    TextView resusltados;
    //Objeto que contrendra la información del JSON
    DatosWebService ribbon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Casting de objetos de XML
        coordenadas = (Button) findViewById(R.id.btnCoordenadas);
        latitud = (EditText) findViewById(R.id.cmplatitud);
        longitud = (EditText) findViewById(R.id.cmplongitud);
        resusltados = (TextView) findViewById(R.id.cmprespuesta);




    }

    //Listener o escuchador para los datos
    public void ClickCoordenadas(View view) {


        switch (view.getId()){
            //En caso de presionar el boton de resultados
            case R.id.btnCoordenadas:
                ribbon = new DatosWebService();
                //A esta clase le envio los parametros que necesite
                ribbon.execute(latitud.getText().toString(),longitud.getText().toString());   // Parámetros que recibe doInBackgroun

            break;

            //Opcion por defecto
            default:
            break;

        }

    }//Final public void ClickCoordenadas


    //Clase para consultar el WebService
    public class DatosWebService extends AsyncTask <String,Integer,String>{

        @Override
        protected String doInBackground(String... voids) {

            //Creo un String para acceder a la URL
            //enlace de ejemplo
            //http://maps.googleapis.com/maps/api/geocode/json?latlng=12.5427939,-81.7237939&sensor=false
            String enlace="http://maps.googleapis.com/maps/api/geocode/json?latlng=";
            enlace = enlace + voids[0]+","+voids[1]+"&sensor=false";

            //Creo el objeto URL para acceder a la información
            URL url = null;

            //String que me indicará cual es la direccion de las coordenadas
            String direccion="";


            try {
                url = new URL(enlace);  //URL de la cual obtenemos información
                //Abrir la conexión
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode(); //Me indica si hay respuesta o no
                //String connstructor que obtendra toda la información del JSON
                StringBuilder answer = new StringBuilder();

                //Verifico si hay conexión con la URL
                if (respuesta == HttpURLConnection.HTTP_OK){

                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                    //La información está en un BufferRead
                    BufferedReader lectura = new BufferedReader(new InputStreamReader(in));

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = lectura.readLine()) != null) {
                        answer.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(answer.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("results");   // results es el nombre del campo en el JSON

                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String adress="SIN DATOS PARA ESA LONGITUD Y LATITUD";
                    if (resultJSON.length()>0){
                        adress = resultJSON.getJSONObject(0).getString("formatted_address");    // dentro del results pasamos a Objeto la seccion formated_address
                    }
                    direccion = "Dirección: " + adress;   // variable de salida que mandaré al onPostExecute para que actualice la UI
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return direccion;
        }

        @Override
        protected void onPreExecute() {
            //Antes de ejecutar la clase me limpia la pantalla
            resusltados.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            //Muesta en pantalla el resultado obtenido por JSONObjet
            resusltados.setText(aVoid);
           // super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }
    }

}
