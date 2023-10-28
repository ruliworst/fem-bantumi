package es.upm.miw.bantumi.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.resultado.Resultado;

public class ResultadoViewHolder extends RecyclerView.ViewHolder {
    TextView tvGanador;
    TextView tvSemillasGanador;
    TextView tvEmpate;
    TextView tvFecha;


    public ResultadoViewHolder(@NonNull View itemView) {
        super(itemView);
        this.tvGanador = itemView.findViewById(R.id.tvGanador);
        this.tvSemillasGanador = itemView.findViewById(R.id.tvSemillasGanador);
        this.tvEmpate = itemView.findViewById(R.id.tvEmpate);
        this.tvFecha = itemView.findViewById(R.id.tvFecha);

    }

    static ResultadoViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resultado_view_item, parent, false);
        return new ResultadoViewHolder(view);
    }

    public void bind(Resultado resultado) {
        this.tvGanador.setText(resultado.getEmpate() ? "Ordenador" : resultado.getGanador());
        this.tvSemillasGanador.setText(resultado.getSemillasGanador().toString());
        this.tvEmpate.setText(resultado.getEmpate()
                ? itemView.getResources().getString(R.string.yes)
                : itemView.getResources().getString(R.string.no));
        this.tvFecha.setText(resultado.getFecha());
    }
}
