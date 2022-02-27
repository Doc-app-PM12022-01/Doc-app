package com.application.pm1_proyecto_final.providers;

import com.application.pm1_proyecto_final.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {
    private CollectionReference collection;

    public UsersProvider() {
        collection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return collection.document(id).get();
    }

    public Task<Void> create(User user) {
        return collection.document(user.getId()).set(user);
    }

    public Task<QuerySnapshot> getUserByField(String value, String filter) {
        String field = filter.equals("email") ? "email" : "numberAccount";
        return collection.whereEqualTo(field, value).get();
    }

    public Task<Void> update(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", user.getAddress());
        map.put("birthDate", user.getBirthDate());
        map.put("carrera", user.getCarrera());
        map.put("image", user.getImage());
        map.put("lastname", user.getLastname());
        map.put("name", user.getName());
        map.put("numberAccount", user.getNumberAccount());
        map.put("phone", user.getPhone());
        map.put("status", user.getStatus());

        return collection.document(user.getId()).update(map);
    };

}