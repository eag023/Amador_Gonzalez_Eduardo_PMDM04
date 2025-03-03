package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private final List<Character> list;
    private final Context context;
    private final OnSpyroLongPressListener spyroLongPressListener;

    public interface OnSpyroLongPressListener {
        void onSpyroLongPress(View view);
    }

    public CharactersAdapter(Context context, List<Character> charactersList, OnSpyroLongPressListener listener) {
        this.context = context;
        this.list = (charactersList != null) ? charactersList : List.of(); // Evita NullPointerException
        this.spyroLongPressListener = listener;
    }

    @NonNull
    @Override
    public CharactersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        // Cargar la imagen del personaje
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                character.getImage(), "drawable", holder.itemView.getContext().getPackageName()
        );
        holder.imageImageView.setImageResource(imageResId);

        // Asegurar que el nombre de la imagen no sea null antes de compararlo
        if (character.getImage() != null && character.getImage().equals("spyro")) {
            holder.itemView.setOnLongClickListener(v -> {
                if (spyroLongPressListener != null) {
                    spyroLongPressListener.onSpyroLongPress(v);
                }
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null); // Evita que otros personajes reaccionen
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}