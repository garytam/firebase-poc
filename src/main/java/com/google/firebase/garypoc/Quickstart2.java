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
public class Quickstart2 {

    private Firestore db;

    public Quickstart2(String projectId) throws Exception {
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
    public Quickstart2() {
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

    City addSimpleDocumentAsEntity() throws Exception {
        // [START fs_add_simple_doc_as_entity]
        // [START firestore_data_set_from_custom_type]
        City city = new City("Hamilton", "ON", "CANADA", false, 3900000L,
                Arrays.asList("region", "east"));

        DocumentReference docRef = db.collection("cities").document("HAM");
        ApiFuture<WriteResult> result = docRef.set(city);
        System.out.println("Update time : " + result.get().getUpdateTime());


        return city;
    }

    String addDocumentDataWithAutoGeneratedId() throws Exception {
        // [START fs_add_doc_data_with_auto_id]
        // [START firestore_data_set_id_random_collection]
        // Add document data with auto-generated id.
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Tokyo");
        data.put("country", "Japan");
        ApiFuture<DocumentReference> addedDocRef = db.collection("cities").add(data);
        System.out.println("Added document with ID: " + addedDocRef.get().getId());
        // [END firestore_data_set_id_random_collection]
        // [END fs_add_doc_data_with_auto_id]

        return addedDocRef.get().getId();
    }

    /** Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

    void deleteCollection(String collectionName) throws Exception{
        CollectionReference collectionReference = db.collection(collectionName);
        deleteCollection(collectionReference, 10);

    }

    void updateSimpleDocument() throws Exception {
//        db.collection("cities").document("HAM").set(new City("Washington D.C.")).get();
        // [START fs_update_doc]
        // [START firestore_data_set_field]
        // Update an existing document
        DocumentReference docRef = db.collection("cities").document("HAM");

        // (async) Update multiple fields

        City hamilton = new City();
        hamilton.setPopulation(500l);
        hamilton.setCapital(new Boolean(false));
        hamilton.setCountry("Bermuda");
//        ApiFuture<WriteResult> future = docRef.update("population", 90000, "capital", true);

        ApiFuture<WriteResult> future = docRef.set(hamilton);
        // ...
        WriteResult result = future.get();
        System.out.println("Write result: " + result);
        // [END firestore_data_set_field]
        // [END fs_update_doc]
    }

    public static void main(String[] args) throws Exception {
        // default project is will be used if project-id argument is not available
        String projectId = (args.length == 0) ? null : args[0];
        Quickstart2 quickStart = (projectId != null) ? new Quickstart2(projectId) : new Quickstart2();

//        quickStart.addDocumentDataWithAutoGeneratedId();
//        quickStart.deleteCollection("cities");
//        quickStart.addSimpleDocumentAsEntity();
        quickStart.updateSimpleDocument();
        quickStart.close();

    }

    /** Closes the gRPC channels associated with this instance and frees up their resources. */
    void close() throws Exception {
        db.close();
    }
}