package com.example.chatrealtime.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chatrealtime.Model.Usuario;
import com.example.chatrealtime.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarios;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
    }

    public ContactosAdapter(List<Usuario> usuarios, OnUsuarioClickListener Listener) {
        this.usuarios = usuarios;
        this.listener = Listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());

        holder.itemView.setOnClickListener(v -> {
            listener.onUsuarioClick(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail;
        ImageView imgPerfil;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.nombreUsuario);
            tvEmail = itemView.findViewById(R.id.emailUsuario);
            imgPerfil = itemView.findViewById(R.id.imgPerfilUsuario);
        }
    }
}
