package cristo.app.proyectodm.activities;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import cristo.app.proyectodm.R;

//Actividad que muestra consejos de manera aleatoria
public class ConsejosActivity extends ToolActivity{
    TextView consejo;
    ArrayList<String> lista_consejos;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);

        tool();

        consejo = findViewById(R.id.consejo);
        lista_consejos = new ArrayList<>();
        lista_consejos.addAll(Arrays.asList(getResources().getStringArray(R.array.advices)));

        int n = (int) (Math.random() * (21 - 1)) + 1;
        consejo.setText(lista_consejos.get(n));
    }
}