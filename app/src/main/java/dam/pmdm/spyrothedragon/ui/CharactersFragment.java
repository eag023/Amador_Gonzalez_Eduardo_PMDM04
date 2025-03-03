package dam.pmdm.spyrothedragon.ui;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.FlameView;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;
import dam.pmdm.spyrothedragon.models.Character;

public class CharactersFragment extends Fragment {

    private FragmentCharactersBinding binding;
    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList;
    private FlameView flameView; // Ahora se usa el mismo FlameView siempre

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCharactersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar RecyclerView
        recyclerView = binding.recyclerViewCharacters;
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            Log.e("CharactersFragment", "RecyclerView es nulo");
        }

        // Inicializar FlameView desde el XML
        flameView = binding.flameView;
        flameView.setVisibility(View.GONE); // Asegurar que está oculto al inicio

        // Inicializar lista y adaptador
        charactersList = new ArrayList<>();
        adapter = new CharactersAdapter(getContext(), charactersList, this::onSpyroLongPress);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }

        // Cargar los personajes desde XML
        loadCharacters();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCharacters() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.characters);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character currentCharacter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("character".equals(tagName)) {
                            currentCharacter = new Character();
                        } else if (currentCharacter != null) {
                            if ("name".equals(tagName)) {
                                currentCharacter.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCharacter.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCharacter.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("character".equals(tagName) && currentCharacter != null) {
                            charactersList.add(currentCharacter);
                        }
                        break;
                }
                eventType = parser.next();
            }

            Log.d("CharactersFragment", "Personajes cargados: " + charactersList.size());
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("CharactersFragment", "Error al cargar los personajes", e);
        }
    }

    private void onSpyroLongPress(View view) {
        // Obtener la vista padre del personaje (CardView o Layout del ítem)
        ViewGroup itemView = (ViewGroup) view;

        // Buscar la imagen de Spyro dentro del itemView
        ImageView spyroImage = itemView.findViewById(R.id.image);
        if (spyroImage == null) return; // Salir si no se encuentra la imagen

        // Crear la vista de la llama
        FlameView flameView = new FlameView(getContext());

        // Quitar cualquier fondo negro (por si acaso)
        flameView.setBackgroundColor(Color.TRANSPARENT);

        // Definir el tamaño del fuego
        int flameSize = spyroImage.getWidth() / 2; // La mitad del tamaño de la imagen de Spyro
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(flameSize, flameSize);

        // Posicionar la llama sobre la boca de Spyro
        params.startToStart = spyroImage.getId();
        params.topToTop = spyroImage.getId();
        params.leftMargin = spyroImage.getWidth() / 2; // Centrado en la boca
        params.topMargin = spyroImage.getHeight() / 3; // Ajuste vertical

        flameView.setLayoutParams(params);

        // Agregar la llama dentro del mismo `itemView` de Spyro
        itemView.addView(flameView);

        // Reproducir sonido
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.fire_sound);
        mediaPlayer.start();

        // Eliminar la llama después de 1.5 segundos
        flameView.postDelayed(() -> {
            itemView.removeView(flameView);
            mediaPlayer.release();
        }, 1000);
    }


}
