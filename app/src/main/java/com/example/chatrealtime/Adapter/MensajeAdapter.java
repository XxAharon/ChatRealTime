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

    // 1. ESTE MÉTODO ES LA CLAVE: Decide qué diseño usar
    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = listaMensajes.get(position);

        if (mensaje.getEmisor() != null && mensaje.getEmisor().equals(miUid)) {
            return TIPO_ENVIADO;
        } else {
            return TIPO_RECIBIDO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_ENVIADO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.burbuja_mensaje_enviado, parent, false);
            return new EnviadoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.burbuja_mensaje_recibido, parent, false);
            return new RecibidoViewHolder(view);
        }
    }

    // 3. Pone los datos en los textos
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);

        if (holder.getItemViewType() == TIPO_ENVIADO) {
            // Configurar ViewHolder Enviado
            EnviadoViewHolder enviadoHolder = (EnviadoViewHolder) holder;
            enviadoHolder.tvContenido.setText(mensaje.getContenido());
            enviadoHolder.tvHora.setText(mensaje.getHora());
        } else {
            // Configurar ViewHolder Recibido
            RecibidoViewHolder recibidoHolder = (RecibidoViewHolder) holder;
            recibidoHolder.tvContenido.setText(mensaje.getContenido());
            recibidoHolder.tvHora.setText(mensaje.getHora());
            // Si tienes un TextView para el nombre en el XML de recibido:
            // recibidoHolder.tvNombre.setText(mensaje.getNombreRemitente());
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    // --- CLASES INTERNAS (ViewHolders) ---

    // Clase para manejar tus mensajes (XML: burbuja_mensaje_enviado)
    static class EnviadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido, tvHora;

        public EnviadoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate que estos ID existan en 'burbuja_mensaje_enviado.xml'
            tvContenido = itemView.findViewById(R.id.contenidoDelMensajeEnviado);
            tvHora = itemView.findViewById(R.id.horaDeEnvioEnviado);
        }
    }

    // Clase para manejar mensajes de otros (XML: burbuja_mensaje_recibido)
    static class RecibidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContenido, tvHora, tvNombre;

        public RecibidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.contenidoDelMensajeRecibido);
            tvHora = itemView.findViewById(R.id.horaDeEnvioRecibido);
            tvNombre = itemView.findViewById(R.id.tipoDeUsuario);
        }
    }
}