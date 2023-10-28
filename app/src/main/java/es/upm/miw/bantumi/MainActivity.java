package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.resultado.Resultado;
import es.upm.miw.bantumi.model.resultado.ResultadoBuilder;
import es.upm.miw.bantumi.model.resultado.ResultadoViewModel;

public class MainActivity extends AppCompatActivity {

    protected static final String LOG_TAG = "MiW";

    JuegoBantumi juegoBantumi;

    BantumiViewModel bantumiVM;

    int numInicialSemillas;

    public String infoPartida = "";

    ResultadoViewModel resultadoViewModel;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.resultadoViewModel = new ViewModelProvider(this).get(ResultadoViewModel.class);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(
                MainActivity.this,
                this,
                bantumiVM,
                JuegoBantumi.Turno.turnoJ1,
                numInicialSemillas);
        crearObservadores();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    integer -> mostrarValor(finalI, juegoBantumi.getSemillas(finalI)));
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                turno -> marcarTurno(juegoBantumi.turnoActual())
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            case R.id.opcReiniciarPartida:
                new RestartDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
                return true;

            case R.id.opcGuardarPartida:
                this.guardarPartida();
                return true;

            case R.id.opcRecuperarPartida:
                this.recuperarPartida();
                return true;

            case R.id.opcAjustes:
                Intent ajustes = new Intent(this, SettingsActivity.class);
                startActivity(ajustes);
                return true;

            case R.id.opcMejoresResultados:
                Intent resultados = new Intent(this, ResultadosActivity.class);
                startActivity(resultados);
                return true;

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private String getArchivoPartidaGuardada() {
        return "partidaGuardada.txt";
    }

    private void guardarPartida() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(this.getArchivoPartidaGuardada(), Context.MODE_PRIVATE);
            String info = juegoBantumi.serializa();
            fos.write(info.getBytes());
            fos.close();
            Log.i("MiW", "Saved game.");
        } catch (Exception exception) {
            Log.e("MiW", R.string.IOError + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void recuperarPartida() {
        boolean hayContenido = false;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(openFileInput(this.getArchivoPartidaGuardada())));
            String estadoPartida = br.readLine();
            if (!estadoPartida.isEmpty() && !estadoPartida.equals(juegoBantumi.serializa())) {
                this.infoPartida = estadoPartida;
                hayContenido = true;
                new RecoverDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
            }
            br.close();
        } catch (Exception exception) {
            Log.e("MiW", R.string.IOError + exception.getMessage());
            exception.printStackTrace();
        }

        if (!hayContenido) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "No hay ninguna partida guardada.",
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria [7..12]
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    private void setNombreJugador() {
        String nombre = this.obtenerNombreAjustes();
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        tvJugador1.setText(nombre);
    }

    private String obtenerNombreAjustes() {
        String nombre = preferences.getString(getString(R.string.nombreJugadorKey), getString(R.string.txtPlayer1));
        return nombre.isEmpty() ? getString(R.string.txtPlayer1) : nombre;
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        boolean empate = false;
        String ganador = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? this.obtenerNombreAjustes()
                : getString(R.string.txtPlayer2);

        String texto = String.format("Gana %s", ganador);

        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
            empate = true;
        }
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
        .show();

        int posicionGanador = ganador.equals(this.obtenerNombreAjustes())
                ? 6 :
                JuegoBantumi.NUM_POSICIONES - 1;
        int posicionPerdedor = posicionGanador == 6
                ? JuegoBantumi.NUM_POSICIONES - 1
                : 6;

        ResultadoBuilder builder = new Resultado.Builder();
        Resultado resultado = builder
                .setGanador(ganador)
                .setEmpate(empate)
                .setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setSemillasGanador(this.juegoBantumi.getSemillas(posicionGanador))
                .setSemillasPerdedor(this.juegoBantumi.getSemillas(posicionPerdedor))
                .build();

        this.resultadoViewModel.insertar(resultado);

        // terminar
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }
}