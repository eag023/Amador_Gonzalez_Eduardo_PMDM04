package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class TutorialOverlayView extends FrameLayout {
    private int currentStep = 0;
    private View[] tutorialViews;
    private int firstButtonId = R.id.bComenzar;
    private int secondButtonId = R.id.bocadillo;
    private int lastButtonId = R.id.bTerminar;
    private int skipButtonId = R.id.bOmitir;
    private int arrowDown = R.id.arrowDown;
    private int arrowUp = R.id.arrowUp;


    public TutorialOverlayView(Context context) {
        super(context);
        init();
    }

    public TutorialOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClickable(true);  // Bloquea toques en la app mientras el tutorial está activo
        setFocusable(true);
        setFocusableInTouchMode(true);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Cargar los layouts del tutorial
        tutorialViews = new View[]{
                inflater.inflate(R.layout.pantalla1, this, false),
                inflater.inflate(R.layout.pantalla2, this, false),
                inflater.inflate(R.layout.pantalla3, this, false),
                inflater.inflate(R.layout.pantalla4, this, false),
                inflater.inflate(R.layout.pantalla5, this, false),
                inflater.inflate(R.layout.pantalla6, this, false)
        };

        // Agregar la primera pantalla con animación fade in
        addView(tutorialViews[currentStep]);
        tutorialViews[currentStep].startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        // Configurar el clic en el botón correspondiente
        setupClickListener();
    }

    private void setupClickListener() {
        int buttonId;
        if (currentStep == 0) {
            buttonId = firstButtonId; // Primer botón en la primera pantalla
        } else if (currentStep == tutorialViews.length - 1) {
            buttonId = lastButtonId; // Último botón en la última pantalla
        } else {
            buttonId = secondButtonId; // Segundo botón en todas las demás pantallas
        }

        View clickableElement = tutorialViews[currentStep].findViewById(buttonId);
        View skipElement = tutorialViews[currentStep].findViewById(skipButtonId);
        View arrowDownElement = tutorialViews[currentStep].findViewById(arrowDown);
        View arrowUpElement = tutorialViews[currentStep].findViewById(arrowUp);

        if (clickableElement != null) {
            clickableElement.setOnClickListener(v -> {nextStep(); playSound();});

            // Aplicar la animación de rebote al segundo botón
            if (buttonId == secondButtonId) {
                Animation bounceAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                clickableElement.startAnimation(bounceAnim);
            }
        }

        if (skipElement != null) {
            skipElement.setOnClickListener(v -> {closeTutorial(); playSound();}); // Salta el tutorial
        }

        // Aplicar animación de movimiento continuo a la flecha abajo
        if (arrowDownElement != null) {
            Animation floatAnim = AnimationUtils.loadAnimation(getContext(), R.anim.arrow_float);
            arrowDownElement.startAnimation(floatAnim);
        }

        // Aplicar animación de movimiento continuo a la flecha arriba
        if (arrowUpElement != null) {
            Animation floatAnim = AnimationUtils.loadAnimation(getContext(), R.anim.arrow_float);
            arrowUpElement.startAnimation(floatAnim);
        }
    }


    private void nextStep() {
        if (currentStep < tutorialViews.length - 1) {
            // Animación de fade out para la pantalla actual
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            tutorialViews[currentStep].startAnimation(fadeOut);

            // Esperar a que termine la animación antes de cambiar de pantalla
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeView(tutorialViews[currentStep]); // Eliminar pantalla actual
                    currentStep++;

                    // ⚡ Cambiar de fragmento en las pantallas indicadas
                    if (getContext() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getContext();
                        if (currentStep == 2) { // Pantalla 3 del tutorial
                            mainActivity.changeFragment(R.id.navigation_worlds);
                        } else if (currentStep == 3) { // Pantalla 4 del tutorial
                            mainActivity.changeFragment(R.id.navigation_collectibles);
                        } else if (currentStep == 5) { // Última pantalla del tutorial
                            mainActivity.changeFragment(R.id.navigation_characters);
                        }
                    }

                    // Agregar la nueva pantalla con animación fade in
                    addView(tutorialViews[currentStep]);
                    tutorialViews[currentStep].startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

                    setupClickListener(); // Volver a configurar los clics en la nueva pantalla
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

        } else {
            closeTutorial();
        }
    }

    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.gem_sound);
        mediaPlayer.setOnCompletionListener(mp -> mp.release()); // Liberar memoria al finalizar
        mediaPlayer.start();
    }

    private void closeTutorial() {
        ((ViewGroup) getParent()).removeView(this);

        // Guardar en SharedPreferences para no mostrarlo de nuevo
        SharedPreferences prefs = getContext().getSharedPreferences("TutorialPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("tutorial_completed", true).apply();
    }
}
