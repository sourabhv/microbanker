package com.metacode.mirobanker.ui.list;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseStorage;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.data.model.ListItem;
import com.metacode.mirobanker.databinding.ActivityAddItemBinding;
import com.metacode.mirobanker.util.Database;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;

import retrofit2.http.Url;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

// NOTE For a known bug https://code.google.com/p/android/issues/detail?id=235661
@SuppressWarnings("VisibleForTests")
public class AddItemActivity extends AppCompatActivity {

    private ActivityAddItemBinding mBinding;

    private ListItem mListItem = new ListItem();
    private Uri mLocalImageUri;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_item);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = Database.get().getReference();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_item);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBinding.photoPickCamera.setOnClickListener(v -> RxImagePicker.with(getApplicationContext())
                .requestImage(Sources.CAMERA)
                .subscribe(uri -> {
                    mLocalImageUri = uri;
                    mBinding.photo.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .placeholder(R.drawable.placeholder_image)
                            .into(mBinding.photo);
                }));

        mBinding.photoPickGallery.setOnClickListener(v -> RxImagePicker.with(getApplicationContext())
                .requestImage(Sources.GALLERY)
                .subscribe(uri -> {
                    mLocalImageUri = uri;
                    mBinding.photo.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .placeholder(R.drawable.placeholder_image)
                            .into(mBinding.photo);
                }));

        mBinding.managerEditText.setInputType(InputType.TYPE_NULL);
        mBinding.managerEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showManagerBottomSheet();
            }
        });
        mBinding.managerEditText.setOnClickListener(v -> showManagerBottomSheet());

        mBinding.saveItem.setOnClickListener(v -> {
            if (mLocalImageUri == null) {
                mBinding.error.setVisibility(View.VISIBLE);
                mBinding.error.setText(R.string.add_item_error_image_empty);
                mBinding.scrollview.scrollTo(0, mBinding.error.getBottom());
                return;
            } else {
                mBinding.error.setVisibility(View.GONE);
            }

            String name = mBinding.nameEditText.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mBinding.nameLayout.setError(getString(R.string.add_item_error_name_empty));
                mBinding.scrollview.scrollTo(0, mBinding.nameLayout.getBottom());
                return;
            } else {
                mListItem.setName(name);
                mBinding.nameLayout.setErrorEnabled(false);
            }

            String age = mBinding.ageEditText.getText().toString();
            if (TextUtils.isEmpty(age)) {
                mBinding.ageLayout.setError(getString(R.string.add_item_error_age_empty));
                mBinding.scrollview.scrollTo(0, mBinding.ageLayout.getBottom());
                return;
            } else {
                try {
                    mListItem.setAge(Integer.parseInt(age));
                } catch (NumberFormatException ignored) {

                }
                mBinding.ageLayout.setErrorEnabled(false);
            }

            int gender = mBinding.genderMale.isChecked() ? 0 : mBinding.genderFemale.isChecked() ? 1 : -1;
            if (gender == -1) {
                mBinding.genderError.setVisibility(View.VISIBLE);
                mBinding.genderError.setText(getString(R.string.add_item_error_gender_empty));
                mBinding.scrollview.scrollTo(0, mBinding.genderLabel.getBottom());
                return;
            } else {
                mListItem.setGender(gender);
                mBinding.genderError.setVisibility(View.GONE);
            }

            String phoneNumber = mBinding.phoneEditText.getText().toString();
            if (TextUtils.isEmpty(phoneNumber)) {
                mBinding.phoneLayout.setError(getString(R.string.add_item_error_phone_empty));
                mBinding.scrollview.scrollTo(0, mBinding.phoneLayout.getBottom());
                return;
            } else {
                mListItem.setPhoneNumber(phoneNumber);
                mBinding.phoneLayout.setErrorEnabled(false);
            }

            if (mListItem.getManager() == null || mListItem.getManager().mName == null || mListItem.getManager().mName.isEmpty()) {
                mBinding.managerLayout.setError(getString(R.string.add_item_error_manager_empty));
                mBinding.scrollview.scrollTo(0, mBinding.managerLayout.getBottom());
                return;
            } else {
                mBinding.managerLayout.setErrorEnabled(false);
            }

            if (mListItem.getImageUrl() == null || mListItem.getImageUrl().isEmpty())
                initUpload();
            else
                updateDatabase();
        });

    }

    public void showManagerBottomSheet() {
        Timber.d("showManagerBottomSheet");
        ManagerBottomSheet fragment = new ManagerBottomSheet();
        fragment.setOnManagerSelectListener(manager -> {
            mListItem.setManager(manager);
            mBinding.managerEditText.setText(manager.getName());
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, null)
                .commitAllowingStateLoss();
    }

    public void initUpload() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.uploading_text), true, false);
        UploadTask uploadTask = mStorageRef.child("images/" + mLocalImageUri.getLastPathSegment())
                .putFile(mLocalImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            mListItem.setImageUrl(taskSnapshot.getDownloadUrl().toString());
            updateDatabase();
        });
        uploadTask.addOnFailureListener(e -> {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mBinding.error.setVisibility(View.VISIBLE);
            mBinding.error.setText(e.getLocalizedMessage());
            mBinding.scrollview.scrollTo(0, mBinding.error.getBottom());
        });
    }

    private void updateDatabase() {
        Task<Void> task = mDatabaseRef.child("records").child(mListItem.getPhoneNumber()).setValue(mListItem);
        task.addOnSuccessListener(aVoid -> {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            finish();
        });
        task.addOnFailureListener(e -> {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mBinding.error.setVisibility(View.VISIBLE);
            mBinding.error.setText(e.getLocalizedMessage());
            mBinding.scrollview.scrollTo(0, mBinding.error.getBottom());
        });
    }

}
