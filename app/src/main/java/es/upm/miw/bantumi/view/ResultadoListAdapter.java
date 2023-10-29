package es.upm.miw.bantumi.view;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.upm.miw.bantumi.model.resultado.Resultado;

public class ResultadoListAdapter extends ListAdapter<Resultado, ResultadoViewHolder> {
    public ResultadoListAdapter(@NonNull DiffUtil.ItemCallback<Resultado> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ResultadoViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        Resultado resultado = getItem(position);
        holder.bind(resultado);
    }

    public static class ResultadoDiff extends DiffUtil.ItemCallback<Resultado> {
        @Override
        public boolean areItemsTheSame(@NonNull Resultado oldItem, @NonNull Resultado newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Resultado oldItem, @NonNull Resultado newItem) {
            return oldItem.getFecha().equals(newItem.getFecha());
        }
    }
}