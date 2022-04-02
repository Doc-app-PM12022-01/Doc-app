package com.application.pm1_proyecto_final.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.pm1_proyecto_final.R;
import com.application.pm1_proyecto_final.activities.CreateGroupActivity;
import com.application.pm1_proyecto_final.activities.EditActivityNote;
import com.application.pm1_proyecto_final.activities.NotesActivity;
import com.application.pm1_proyecto_final.activities.PublicationActivity;
import com.application.pm1_proyecto_final.adapters.GroupAdapter;
import com.application.pm1_proyecto_final.adapters.NoteAdapter;
import com.application.pm1_proyecto_final.api.NoteApiMethods;
import com.application.pm1_proyecto_final.listeners.Notelistener;
import com.application.pm1_proyecto_final.models.Note;
import com.application.pm1_proyecto_final.providers.GroupsProvider;
import com.application.pm1_proyecto_final.utils.Constants;
import com.application.pm1_proyecto_final.utils.PreferencesManager;
import com.application.pm1_proyecto_final.utils.ResourceUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FrangmentApuntes extends Fragment implements Notelistener {

    FloatingActionButton buttonaddNote;
    PreferencesManager preferencesManager;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    AlertDialog.Builder pBuilderSelector;
    CharSequence options[];
    SweetAlertDialog pDialog;
    TextView textViewMessage;
    EditText textSearchNote;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_apuntes, container, false);

        preferencesManager = new PreferencesManager(getContext());

        buttonaddNote = (FloatingActionButton) view.findViewById(R.id.btnaddNote);

        recyclerView = (RecyclerView) view.findViewById(R.id.myNotesRecyclerView);

        progressBar = (ProgressBar) view.findViewById(R.id.myNotesProgressBar);


        pDialog = ResourceUtil.showAlertLoading(getContext());

        textViewMessage = (TextView) view.findViewById(R.id.textMessageNotes);

        textSearchNote = (EditText) view.findViewById(R.id.textSearchNote);

        pBuilderSelector = new AlertDialog.Builder(getContext());
        pBuilderSelector.setTitle("Seleccione una opción");
        options = new CharSequence[]{"Ver/Actualizar", "Eliminar"};

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Mis Apuntes");

        setListeners();
        getMyNotes();
        return view;
    }



    public void getMyNotes(){
        loading(true);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                (NoteApiMethods.GET_NOTE_USER_CREATE+preferencesManager.getString(Constants.KEY_USER_ID)),
                null,
                new com.android.volley.Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject  jsonObject = null;

                            Note noteTemp = null;

                            List<Note> notes = new ArrayList<>();


                            if(response.getString("res").equals("true")){
//                               t = response.getJSONObject("data").getString("name");


                                JSONArray array = response.getJSONObject("data").getJSONArray("notas_creates");
                                for (int i = 0; i < array.length(); i++) {
                                    jsonObject = new JSONObject(array.get(i).toString());


                                    noteTemp = new Note();
                                    noteTemp.setId(jsonObject.getString("id"));
                                    noteTemp.setTitle(jsonObject.getString("title"));
                                    noteTemp.setDescription(jsonObject.getString("content"));
                                    noteTemp.setStatus(jsonObject.getString("status"));
                                    noteTemp.setDate(jsonObject.getString("updated_at"));
                                    noteTemp.setUser_create(jsonObject.getString("user_id"));

                                    notes.add(noteTemp);

                                }
                                //loading(false);

                                if(notes.size() == 0){
                                    textViewMessage.setVisibility(View.VISIBLE);

                                }else{
                                    textViewMessage.setVisibility(View.GONE);
                                }
                                noteAdapter = new NoteAdapter(notes, FrangmentApuntes.this);
                                recyclerView.setAdapter(noteAdapter);
                                loading(false);

                            }else{
                                Toast.makeText(getContext(), "Error: "+response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


        );

        requestQueue.add(request);

    }

    private void loading(boolean isLoading) {

        if(isLoading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setListeners(){
        buttonaddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NotesActivity.class);
                startActivity(intent);
            }
        });

        textSearchNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    noteAdapter.getFilter().filter(charSequence);
                }catch (Exception e){}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void onResume() {
        super.onResume();
        getMyNotes();
    }


    @Override
    public void onClickNote(Note note) {
        pBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(getContext(), EditActivityNote.class);
                    intent.putExtra("noteEdit", note);
                    startActivity(intent);
                } else if(i == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setMessage("¿Seguro que desea eliminar la nota?").setTitle("Alerta");

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteNote(note.getId());
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        pBuilderSelector.show();

    }

    private void deleteNote(String idnota) {
       if (pDialog.isShowing()) {
           pDialog.show();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.DELETE, NoteApiMethods.DELETE_NOTE+idnota, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResourceUtil.showAlert("Mensaje", "Nota Eliminada.",getContext(), "success");
                getMyNotes();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ResourceUtil.showAlert("Advertencia", "Se produjo un error al eliminar la nota.",getContext(), "error");
                Log.d("ERROR_NOTE", "Error Delete: "+error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}