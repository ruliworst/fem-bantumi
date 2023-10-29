package es.upm.miw.bantumi.fragments.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.JuegoBantumi;
import es.upm.miw.bantumi.MainActivity;
import es.upm.miw.bantumi.R;

public class RestartDialog extends DialogFragment {
    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.txtReiniciarPartida)
                .setMessage(R.string.reiniciarPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> main.juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1)
                )
                .setNegativeButton(
                        getString(android.R.string.cancel),
                        (dialog, which) -> main.finish()
                );

        return builder.create();
    }
}
