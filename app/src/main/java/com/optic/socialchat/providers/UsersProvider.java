package com.optic.socialchat.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.socialchat.models.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id){
        return mCollection.document(id).get();
    }
    public DocumentReference getUserRealTime(String id){
        return mCollection.document(id);
    }

    public Task<Void> create(Users users){
        return mCollection.document(users.getId()).set(users);
    }

    public Task<Void> update(Users users){
        Map<String, Object> map = new HashMap<>();
        map.put("username", users.getUsername());
        map.put("phone", users.getPhone());
        map.put("timestamp", new Date().getTime());
        map.put("image_profile", users.getImageProfile());
        map.put("image_cover", users.getImageCover());
        return mCollection.document(users.getId()).update(map);
    }

    public Task<Void> updateOnline(String idUser, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(idUser).update(map);
    }

}
