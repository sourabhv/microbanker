package com.metacode.mirobanker.util.rxfir;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metacode.mirobanker.data.model.ListItem;

import rx.Emitter;
import rx.Observable;

/*
 * Copyright 2017 Sourabh Verma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FirebaseRx {

    public static Observable<UploadTask.TaskSnapshot> uploadFile(StorageReference ref, Uri fileUri) {
        return Observable.create(uploadTaskEmitter -> {
            UploadTask task = ref.putFile(fileUri);
            task.addOnSuccessListener(taskSnapshot -> {
                uploadTaskEmitter.onNext(taskSnapshot);
                uploadTaskEmitter.onCompleted();
            });
            task.addOnFailureListener(uploadTaskEmitter::onError);
        }, Emitter.BackpressureMode.BUFFER);
    }

    public static Observable<Boolean> updateDatabase(DatabaseReference ref, ListItem value) {
        return Observable.create(uploadTaskEmitter -> {
            Task<Void> task = ref.setValue(value);
            task.addOnSuccessListener(aVoid -> {
                uploadTaskEmitter.onNext(true);
                uploadTaskEmitter.onCompleted();
            });
            task.addOnFailureListener(uploadTaskEmitter::onError);
        }, Emitter.BackpressureMode.BUFFER);
    }

}
