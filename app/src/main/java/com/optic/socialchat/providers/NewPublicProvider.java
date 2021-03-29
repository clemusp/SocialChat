package com.optic.socialchat.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.socialchat.models.NewPublic;

import java.nio.channels.SelectableChannel;

public class NewPublicProvider {

    CollectionReference mCollection;


    public NewPublicProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(NewPublic newPublic){
        return mCollection.document().set(newPublic);
    }

    public Query getAll(){
        return mCollection.orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Query getPostByMessagePublic(String menssagepublic){
        return mCollection.orderBy("menssagepublic").startAt(menssagepublic).endAt(menssagepublic+'\uf8ff');
    }

    public Query getPostByCategoryAndTimestamp(String category){
        return mCollection.whereEqualTo("category", category).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Query getPostByUser(String id){
        return mCollection.whereEqualTo("idUser",id);
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }



}
