package cristo.app.proyectodm.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Comparator;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.adapter.AdaptadorRanking;
import cristo.app.proyectodm.model.EntidadRanking;

/*Actividad que muestra el ranking del top 3 usuarios que tienen la app con mas pasos y la posicion
que ocupa el usuario registrado*/
public class RankingActivity extends ToolActivity {
    FirebaseAuth mAuth;
    DatabaseReference ref;

    ListView lvRanking;
    ArrayList<EntidadRanking> myArrayListRanking = new ArrayList<>();
    AdaptadorRanking myArrayAdapterRanking;

    TextView tuPuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        tool();

        ref = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();

        myArrayAdapterRanking = new AdaptadorRanking(this,myArrayListRanking);

        lvRanking = findViewById(R.id.list_ranking);
        lvRanking.setAdapter(myArrayAdapterRanking);

        tuPuesto=findViewById(R.id.tuPuesto);

        //Query que ordena la lista de los usuarios segun el numero de pasos de menor a mayor
        Query puesto = ref.orderByChild("pasosTotales");
        puesto.addChildEventListener(new ChildEventListener() {
            String email;
            int i=0;
            @Override
            //Con esto se sabe la posicion actual en el ranking del usuario logeado
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        if(childSnapshot.getKey().equals("email")) {
                            email = childSnapshot.getValue(String.class);
                            i++;
                            if(email.equals(userEmail)){
                                i=1;
                            }
                        }
                    }
                    tuPuesto.setText(String.valueOf(i));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Query que ordena de menor a mayor segun el numero de pasos, pero solo se cogen los 3 ultimos
        Query top3query = ref.orderByChild("pasosTotales").limitToLast(3);
        top3query.addChildEventListener(new ChildEventListener() {
            @Override
            //Guardamos en un array el nombre y pasos totales de los usuarios
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String nombreRanking="",pasosRanking="",foto="";
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                        if(childSnapshot.getKey().equals("name")) {
                            nombreRanking= childSnapshot.getValue().toString();

                        }
                        if(childSnapshot.getKey().equals("pasosTotales")) {
                            pasosRanking= childSnapshot.getValue().toString();
                        }
                        if(!pasosRanking.equals("")) {
                            myArrayListRanking.add(new EntidadRanking(nombreRanking, pasosRanking));
                            pasosRanking="";

                        }
                    }
                    //Se invierte el orden para que se muestre de mayor a menor
                    myArrayListRanking.sort(Comparator.comparing(EntidadRanking::getPasos).reversed());
                    myArrayAdapterRanking.notifyDataSetChanged();
                }

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}