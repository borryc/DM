package cristo.app.proyectodm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cristo.app.proyectodm.R;
import cristo.app.proyectodm.model.Entidad;


public class Adaptador extends BaseAdapter {
    Context context;
    ArrayList<Entidad> listPeticiones;

    //FirebaseAuth mAuth;
    DatabaseReference mRef;

    public Adaptador(Context context, ArrayList<Entidad> listPeticiones) {
        this.context = context;
        this.listPeticiones = listPeticiones;
    }

    @Override
    public int getCount() {
        return listPeticiones.size();
    }

    @Override
    public Object getItem(int i) {  //i=position
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView usuario_amigo;
        Entidad Item = listPeticiones.get(i);

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.list_item_vic, null);

        ImageView aceptar_peticion = view.findViewById(R.id.confirmar_amistad);
        ImageView denegar_peticion = view.findViewById(R.id.denegar_amistad);

        String correoEnvia = Item.getCorreoEnvia();
        String UIDEnvia = Item.getUIDEnvia();
        String UIDRecibe = Item.getUIDRecibe();
        String correoRecibe=Item.getCorreoRecibe();

        mRef = FirebaseDatabase.getInstance().getReference();

        //Opcion de aceptar solicitud
        aceptar_peticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Elimino el item de la vista
                listPeticiones.remove(Item);
                notifyDataSetChanged();

                //Creo la nueva amistad
                Map<String, Object> map1 = new HashMap<>();
                map1.put(UIDEnvia,correoEnvia);
                mRef.child("Amigos").child(UIDRecibe).updateChildren(map1);

                Map<String, Object> map2 = new HashMap<>();
                map2.put(UIDRecibe,correoRecibe);
                mRef.child("Amigos").child(UIDEnvia).updateChildren(map2);


                Toast.makeText(context, R.string.adaptador_tienes_nuevo_amigo, Toast.LENGTH_SHORT).show();

                //Elimino la peticion aceptada

                mRef.child("Peticiones").child(UIDRecibe).child(UIDEnvia).removeValue();
                }
            });

                //Opcion denegar solicitud
                denegar_peticion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        listPeticiones.remove(Item);
                        notifyDataSetChanged();
                        mRef.child("Peticiones").child(UIDRecibe).child(UIDEnvia).removeValue();

                        Toast.makeText(context, R.string.adaptador_solicitud_eliminada, Toast.LENGTH_SHORT).show();

                    }
                });


                usuario_amigo = view.findViewById(R.id.nombrePeti);

                usuario_amigo.setText(Item.getCorreoEnvia());

                return view;
            }
    }