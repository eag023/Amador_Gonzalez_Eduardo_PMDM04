package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.VideoActivity;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private Context context;
    private int gemClickCount = 0;
    private long lastClickTime = 0;
    private static final int CLICK_THRESHOLD = 1000; // Máximo 1 seg entre clics

    public CollectiblesAdapter(Context context, List<Collectible> collectibleList) {
        this.context = context;
        this.list = collectibleList;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Detectar si es la Gema (gems)
        if ("gems".equals(collectible.getImage())) {
            holder.itemView.setOnClickListener(v -> handleGemClick());
        }
    }

    private void handleGemClick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastClickTime > CLICK_THRESHOLD) {
            gemClickCount = 0; // Reiniciar si pasa demasiado tiempo entre toques
        }

        gemClickCount++;
        lastClickTime = currentTime;

        if (gemClickCount == 4) {
            gemClickCount = 0; // Reiniciar el contador

            // Reproducir sonido de la gema
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gem_sound);
            mediaPlayer.start();

            // Lanzar la actividad del video
            Intent intent = new Intent(context, VideoActivity.class);
            context.startActivity(intent);

            // Liberar el MediaPlayer cuando termine el sonido
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
