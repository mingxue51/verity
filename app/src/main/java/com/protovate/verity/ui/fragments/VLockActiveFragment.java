package com.protovate.verity.ui.fragments;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.TypeEvent;
import com.protovate.verity.data.events.Unlock;
import com.protovate.verity.data.responses.LockResponse;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.service.BackgroundLocationService;
import com.protovate.verity.service.GeofenceService;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.ui.vlock.active.AddAudioActivity;
import com.protovate.verity.ui.vlock.active.AddNoteActivity;
import com.protovate.verity.ui.vlock.active.AddPhotosActivity;
import com.protovate.verity.ui.vlock.active.AddVideosActivity;
import com.protovate.verity.ui.vlock.active.ViewAudiosActivity;
import com.protovate.verity.ui.vlock.active.ViewNotesActivity;
import com.protovate.verity.ui.vlock.active.ViewPhotosActivity;
import com.protovate.verity.ui.vlock.active.ViewVideosActivity;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 6/22/15.
 */
@SuppressWarnings("unchecked")
public class VLockActiveFragment extends Fragment
//        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>
{
    public static final int PHOTO_REQUEST_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 200;
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.location) TextView mLocation;
    @InjectView(R.id.locationName) TextView mLocationName;
    @InjectView(R.id.btnAddPhotos) ImageView mBtnAddPhotos;
    @InjectView(R.id.btnAddVideo) ImageView mBtnAddVideo;
    @InjectView(R.id.btnAddAudio) ImageView mBtnAddAudio;
    @InjectView(R.id.btnTextNote) ImageView mBtnAddTextNote;
    @InjectView(R.id.noMediaFiles) TextView mNoMediaFiles;
    @InjectView(R.id.tvPhotoCount) TextView mTvPhotoCount;
    @InjectView(R.id.tvVideoCount) TextView mTvVideoCount;
    @InjectView(R.id.tvAudioCount) TextView mTvAudioCount;
    @InjectView(R.id.tvNoteCount) TextView mTvNoteCount;
    @InjectView(R.id.photosCount) TextView mPhotosCountPull;
    @InjectView(R.id.videosCount) TextView mVideosCountPull;
    @InjectView(R.id.audioCount) TextView mAudioCountPull;
    @InjectView(R.id.notesCount) TextView mNotesCountPull;

//    protected GoogleApiClient mGoogleApiClient;
//    private PendingIntent mGeofencePendingIntent;

    private LockResponse.Data lockData;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_vlock_active, container, false);

        ((MainActivity) getActivity()).setTitle(((MainActivity) getActivity()).getSupportActionBar(), "V-Lock Active");
        ((App) getActivity().getApplication()).component().inject(this);
        ButterKnife.inject(this, v);

        lockData = ((LockResponse.Data) getActivity().getIntent().getSerializableExtra("vlock_data"));

        try {
            mLocation.setText(String.format("Verity has locked your location at\n" + lockData.getLat() + " lat by " + lockData.getLng() + " lon", lockData.getLat(), lockData.getLng()));
            mLocationName.setText(lockData.getAddressLine1() + " " + lockData.getAddressLine2() + "\n" + lockData.getCity() + ", " + lockData.getState() + " " + lockData.getZip());

            Intent intent = new Intent(getActivity(), GeofenceService.class);
            intent.putExtra("latitude", Double.valueOf(lockData.getLat()));
            intent.putExtra("longitude", Double.valueOf(lockData.getLng()));
            getActivity().startService(intent);

            getActivity().startService(new Intent(getActivity(), BackgroundLocationService.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ButterKnife.inject(this, v);

        setAudioButton(0);
        setNoteButton(0);
        setPhotoButton(0);
        setVideoButton(0);

        if (profile.shouldUnlock()) {
            unlockVlock();
        }

        return v;
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        getData();
    }

    private void getData() {
        mNoMediaFiles.setVisibility(View.INVISIBLE);
        apiClient.getLock(String.valueOf(profile.getLatestLockId()), profile.getAccessToken(), "audio_count, video_count, photo_count, note_count")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        int audioCount = response.getData().getAudioCount();
                        int videoCount = response.getData().getVideoCount();
                        int noteCount = response.getData().getNoteCount();
                        int photoCount = response.getData().getPhotoCount();
                        setPhotoButton(photoCount);
                        setVideoButton(videoCount);
                        setNoteButton(noteCount);
                        setAudioButton(audioCount);

                        if ((audioCount + videoCount + noteCount + photoCount) == 0) {
                            mNoMediaFiles.setVisibility(View.VISIBLE);
                        }
                    }
                }, error -> System.out.println("error = " + error));
    }

    private void setAudioButton(int count) {
        mBtnAddAudio.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.audio_icon_with_container));
        mTvAudioCount.setVisibility(View.VISIBLE);
        mTvAudioCount.setText(String.valueOf(count));
        mAudioCountPull.setVisibility(View.VISIBLE);
        mAudioCountPull.setText(String.valueOf(count));
    }

    private void setNoteButton(int count) {
        mBtnAddTextNote.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.notes_icon_with_container));
        mTvNoteCount.setVisibility(View.VISIBLE);
        mTvNoteCount.setText(String.valueOf(count));
        mNotesCountPull.setVisibility(View.VISIBLE);
        mNotesCountPull.setText(String.valueOf(count));
    }

    private void setVideoButton(int count) {
        mBtnAddVideo.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.video_icon_with_container));
        mTvVideoCount.setVisibility(View.VISIBLE);
        mTvVideoCount.setText(String.valueOf(count));
        mVideosCountPull.setVisibility(View.VISIBLE);
        mVideosCountPull.setText(String.valueOf(count));
    }

    private void setPhotoButton(int count) {
        mBtnAddPhotos.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.photos_icon_with_container));
        mTvPhotoCount.setVisibility(View.VISIBLE);
        mTvPhotoCount.setText(String.valueOf(count));
        mPhotosCountPull.setVisibility(View.VISIBLE);
        mPhotosCountPull.setText(String.valueOf(count));
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe public void onUnlock(Unlock unlock) {
        Toast.makeText(getActivity(), "V-Lock activity has been unlocked, you have left your initial location", Toast.LENGTH_LONG).show();
        unlockVlock();
    }

    @Subscribe public void enableButton(TypeEvent event) {
        getData();
    }

    public void unlockVlock() {
        try {
            if(profile.getLatestLockId() != 0) {
                apiClient.unloackVlock(String.valueOf(lockData.getId()), lockData.getAddressLine1(), lockData.getAddressLine2(),
                        lockData.getCity(), lockData.getState(), lockData.getCountry(), lockData.getZip(),
                        lockData.getLat(), lockData.getLng(), profile.getAccessToken(), new Callback<User>() {
                            @Override public void success(User user, Response response) {
                                try {
                                    if (user.isSuccess()) {
                                        profile.setUnlock(false);
                                        profile.setLatestLockId(0);
                                        ((MainActivity) getActivity()).showNotifications();
                                        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
                                    } else {
                                        for (User.Error e : user.getErrors()) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override public void failure(RetrofitError error) {

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btnUnlock) void unlock() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to close your V-Lock Activity?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> unlockVlock())
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.btnTextNote) void textNote() {
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra("id", lockData.getId());

        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setExitTransition(new Slide(Gravity.TOP));
            getActivity().getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnAddPhotos) void addPhoto() {
        startActivityForResult(new Intent(getActivity(), AddPhotosActivity.class), PHOTO_REQUEST_CODE);
    }

    @OnClick(R.id.btnViewPhotos) void viewPhotos() {
        Intent intent = new Intent(getActivity(), ViewPhotosActivity.class);
        intent.putExtra("editable", true);
        startActivity(intent);
    }

    @OnClick(R.id.btnAddAudio) void addAudio() {
        final Intent intent = new Intent(getActivity(), AddAudioActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnViewNotes) void viewNotes() {
        Intent intent = new Intent(getActivity(), ViewNotesActivity.class);
        intent.putExtra("editable", true);
        startActivity(intent);
    }

    @OnClick(R.id.btnAddVideo) void addVideos() {
        startActivityForResult(new Intent(getActivity(), AddVideosActivity.class), VIDEO_REQUEST_CODE);
    }

    @OnClick(R.id.btnViewVideos) void viewVideos() {
        Intent intent = new Intent(getActivity(), ViewVideosActivity.class);
        intent.putExtra("editable", true);
        startActivity(intent);
    }

    @OnClick(R.id.btnViewAudios) void viewAudios() {
        Intent intent = new Intent(getActivity(), ViewAudiosActivity.class);
        intent.putExtra("editable", true);
        startActivity(intent);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
