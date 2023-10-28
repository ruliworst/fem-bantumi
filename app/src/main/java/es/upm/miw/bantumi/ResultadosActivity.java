package es.upm.miw.bantumi;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.bantumi.model.resultado.ResultadoViewModel;
import es.upm.miw.bantumi.view.ResultadoListAdapter;

public class ResultadosActivity extends AppCompatActivity {
    ResultadoViewModel resultadoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultados_recyclerview);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);

        resultadoViewModel = new ViewModelProvider(this).get(ResultadoViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.resultados_recycler_view);
        final ResultadoListAdapter resultadoListAdapter = new ResultadoListAdapter(new ResultadoListAdapter.ResultadoDiff());
        recyclerView.setAdapter(resultadoListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultadoViewModel.obtenerDiezMejoresResultados().observe(this, resultadoListAdapter::submitList);
        Log.i(MainActivity.LOG_TAG, "Mejores 10 resultados obtenidos.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.resultado_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.opcBorrarTodo:
                Log.i(MainActivity.LOG_TAG, "Todos los resultados borrados.");
                this.borrarTodos();
                return true;
            default:
                break;
        }
        return true;
    }

    public void borrarTodos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.txtBorrarTodos)
                .setMessage(R.string.pregBorrarTodos)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> resultadoViewModel.borrarTodos())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {})
                .show();
    }
}
