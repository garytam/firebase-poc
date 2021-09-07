package com.google.firebase.garypoc;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
// [START firestore_deps]
// [END firestore_deps]
import com.google.common.collect.ImmutableMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.garypoc.dataobject.City;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple Quick start application demonstrating how to connect to Firestore
 * and add and query documents.
 */
public class Quickstart {

    private Firestore db;

    public Quickstart(String projectId) throws Exception {
        // [START firestore_setup_client_create]
        // Option 1: Initialize a Firestore client with a specific `projectId` and
        //           authorization credential.
        // [START fs_initialize_project_id]
        // [START firestore_setup_client_create_with_project_id]
        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
        Firestore db = firestoreOptions.getService();
        // [END fs_initialize_project_id]
        // [END firestore_setup_client_create_with_project_id]
        // [END firestore_setup_client_create]
        this.db = db;
    }

    /**
     * Initialize Firestore using default project ID.
     */
    public Quickstart() {
        // [START firestore_setup_client_create]

        // Option 2: Initialize a Firestore client with default values inferred from
        //           your environment.
        // [START fs_initialize]
        Firestore db = FirestoreOptions.getDefaultInstance().getService();
        // [END firestore_setup_client_create]
        // [END fs_initialize]
        this.db = db;
    }

    Firestore getDb() {
        return db;
    }


    /**
     * Add named test documents with fields first, last, middle (optional), born.
     *
     * @param docName document name
     */
    void addDocument(String docName) throws Exception {
        switch (docName) {
            case "alovelace": {
                // [START fs_add_data_1]
                // [START firestore_setup_dataset_pt1]
                DocumentReference docRef = db.collection("users").document("alovelace");
                // Add document data  with id "alovelace" using a hashmap
                Map<String, Object> data = new HashMap<>();
                data.put("first", "Ada");
                data.put("last", "Lovelace");
                data.put("born", 1815);
                //asynchronously write data
                ApiFuture<WriteResult> result = docRef.set(data);
                // ...
                // result.get() blocks on response
                System.out.println("Update time : " + result.get().getUpdateTime());
                // [END firestore_setup_dataset_pt1]
                // [END fs_add_data_1]
                break;
            }
            case "aturing": {
                // [START fs_add_data_2]
                // [START firestore_setup_dataset_pt2]
                DocumentReference docRef = db.collection("users").document("aturing");
                // Add document data with an additional field ("middle")
                Map<String, Object> data = new HashMap<>();
                data.put("first", "Alan");
                data.put("middle", "Mathison");
                data.put("last", "Turing");
                data.put("born", 1912);

                ApiFuture<WriteResult> result = docRef.set(data);
                System.out.println("Update time : " + result.get().getUpdateTime());
                // [END firestore_setup_dataset_pt2]
                // [END fs_add_data_2]
                break;
            }
            case "cbabbage": {
                DocumentReference docRef = db.collection("users").document("cbabbage");
                Map<String, Object> data =
                        new ImmutableMap.Builder<String, Object>()
                                .put("first", "Charles")
                                .put("last", "Babbage")
                                .put("born", 1791)
                                .build();
                ApiFuture<WriteResult> result = docRef.set(data);
                System.out.println("Update time : " + result.get().getUpdateTime());
                break;
            }
            default:
        }
    }

    void updateDate() throws Exception{
        DocumentReference docRef = db.collection("users").document("cbabbage");
        Map<String, Object> update = new HashMap<>();

        update.put("born", 2792);

        ApiFuture<WriteResult> writeResult =
                db
                        .collection("users")
                        .document("cbabbage")
                        .set(update, SetOptions.merge());
// ...
        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }

    void garywtf() throws Exception{
        DocumentReference docRef = db.collection("cities").document("toronto");
        Map<String, Object> data =
                new ImmutableMap.Builder<String, Object>()
                        .put("province", "Ontario")
                        .put("capital", "Toronto")
                        .put("populate", 100000000)
                        .build();
        ApiFuture<WriteResult> result = docRef.set(data);
        System.out.println("Update time : " + result.get().getUpdateTime());
    }

    void garywtf2() throws Exception{
        City city = new City("Los Angeles", "CA", "USA", false, 3900000L,
                Arrays.asList("west_coast", "socal"));

        DocumentReference docRef = db.collection("cities").document("LA");
        ApiFuture<WriteResult> result = docRef.set(city);
        System.out.println("Update time : " + result.get().getUpdateTime());
    }

    void findUser() throws Exception{
        ApiFuture<QuerySnapshot> query =
                db.collection("users").whereEqualTo("last", "Babbage").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if (documents.isEmpty()){
            System.out.println("Not found");
        }
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
            System.out.println("First: " + document.getString("first"));
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born"));
        }
    }

    void getDocument(String id) throws Exception{
        DocumentReference docRef = db.collection("users").document(id); //"aturing");
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = future.get();
        if (document.exists()) {
            System.out.println("Document data: " + document.getData());
        } else {
            System.out.println("No such document!");
        }
    }

    void runQuery() throws Exception {
        // [START fs_add_query]
        // asynchronously query for all users born before 1900
        ApiFuture<QuerySnapshot> query =
                db.collection("users").whereLessThan("born", 1900).get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
            System.out.println("First: " + document.getString("first"));
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born"));
        }
        // [END fs_add_query]
    }

    void retrieveAllDocuments() throws Exception {
        // [START fs_get_all]
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
            System.out.println("First: " + document.getString("first"));
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born"));
        }
        // [END fs_get_all]
    }

    void run() throws Exception {
        String[] docNames = {"alovelace", "aturing", "cbabbage"};

        // Adding document 1
        System.out.println("########## Adding document 1 ##########");
        addDocument(docNames[0]);

        // Adding document 2
        System.out.println("########## Adding document 2 ##########");
        addDocument(docNames[1]);

        // Adding document 3
        System.out.println("########## Adding document 3 ##########");
        addDocument(docNames[2]);

        // retrieve all users born before 1900
        System.out.println("########## users born before 1900 ##########");
        runQuery();

        // retrieve all users
        System.out.println("########## All users ##########");
        retrieveAllDocuments();
        System.out.println("###################################");
    }

    /**
     * A quick start application to get started with Firestore.
     *
     * @param args firestore-project-id (optional)
     */
    public static void main(String[] args) throws Exception {
        // default project is will be used if project-id argument is not available
        String projectId = (args.length == 0) ? null : args[0];
        Quickstart quickStart = (projectId != null) ? new Quickstart(projectId) : new Quickstart();
//        quickStart.run();
//        quickStart.findUser();

//        quickStart.getDocument("aturing");

        quickStart.garywtf2();

        quickStart.close();

    }

    /** Closes the gRPC channels associated with this instance and frees up their resources. */
    void close() throws Exception {
        db.close();
    }
}