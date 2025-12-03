package com.example.chatrealtime.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatrealtime.R;
import com.example.chatrealtime.Model.Mensaje;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;
    private List<Mensaje> listaMensajes;
    private String miUid;

    public MensajeAdapter(List<Mensaje> listaMensajes, String miUid) {
        this.listaMensajes = listaMensajes;
        this.miUid = miUid;
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = listaMensajes.get(position);

        // CORRECCIÓN: Asegúrate que el método Getter coincida con tu modelo.
        // Si tu clase Mensaje tiene getSenderId(), cambia getEmisor() por getSenderId()
        if (mensaje.getEmisor() != null && mensaje.getEmisor().equals(miUid)) {
            return TIPO_ENVIADO;
        } else {
            return TIPO_RECIBIDO;
        }
    }

    // ... (El resto del código se mantiene igual: onCreateViewHolder y onBindViewHolder) ...
    // Solo asegúrate de copiar el resto del archivo como lo tenías.

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_ENVIADO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.burbuja_mensaje_enviado, parent, false);
            return new EnviadoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.burbuja_mensaje_recibido, parent, false);
            return new RecibidoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        if (holder.getItemViewType() == TIPO_ENVIADO) {
            EnviadoViewHolder h = (EnviadoViewHolder) holder;
            h.tvContenido.setText(mensaje.getContenido());
            h.tvHora.setText(mensaje.getHora());
        } else {
            RecibidoViewHolder h = (RecibidoViewHolder) holder;
            h.tvContenido.setText(mensaje.getContenido());
            h.tvHora.setText(mensaje.getHora());
        }
    }

    @Override
    public int getItemCount() { return listaMensajes.size(); }

    static class EnviadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido, tvHora;
        public EnviadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.contenidoDelMensajeEnviado); // Verifica este ID en tu XML
            tvHora = itemView.findViewById(R.id.horaDeEnvioEnviado);
        }
    }

    static class RecibidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido, tvHora, tvNombre;
        public RecibidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.contenidoDelMensajeRecibido); // Verifica este ID en tu XML
            tvHora = itemView.findViewById(R.id.horaDeEnvioRecibido);
            tvNombre = itemView.findViewById(R.id.tipoDeUsuario);
        }
    }
}