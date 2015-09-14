package com.calialec.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.calialec.spotifystreamer.model.TrackParcelable;
import com.calialec.spotifystreamer.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;

public class TrackPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private Handler seekBarUpdateHandler;
    private Runnable seekBarUpdateRunnable;

    private int currentTrackPosition = 0;
    private ArrayList<TrackParcelable> topTracksList;

    private final IBinder trackPlayerBinder = new TrackPlayerBinder();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return trackPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        if (seekBarUpdateHandler != null)
            seekBarUpdateHandler.removeCallbacks(seekBarUpdateRunnable);
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ViewUtil.setMediaPlayerUiState(TrackPlayerDialogFragment.trackPlayerControlPlay, ViewUtil.MEDIA_PLAYER_UI_STATE_PAUSED);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        updateTrackPlayerUi();
    }

    public void setTopTracksList(ArrayList<TrackParcelable> topTracksList) {
        this.topTracksList = topTracksList;
    }

    public class TrackPlayerBinder extends Binder {
        TrackPlayerService getService() {
            return TrackPlayerService.this;
        }
    }

    public void playTopTrack(int index) {
        currentTrackPosition = index;
        mediaPlayer.reset();
        TrackParcelable track = topTracksList.get(currentTrackPosition);
        try {
            mediaPlayer.setDataSource(track.previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress);
        }
    }

    public void handlePlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ViewUtil.setMediaPlayerUiState(TrackPlayerDialogFragment.trackPlayerControlPlay, ViewUtil.MEDIA_PLAYER_UI_STATE_PAUSED);
        } else {
            mediaPlayer.start();
            ViewUtil.setMediaPlayerUiState(TrackPlayerDialogFragment.trackPlayerControlPlay, ViewUtil.MEDIA_PLAYER_UI_STATE_PLAYING);
        }
    }

    public TrackParcelable nextSong() {
        if (currentTrackPosition < topTracksList.size() - 1) {
            int pos = currentTrackPosition + 1;
            playTopTrack(pos);
            return topTracksList.get(pos);
        }
        return null;
    }

    public TrackParcelable previousSong() {
        if (currentTrackPosition > 0) {
            int pos = currentTrackPosition - 1;
            playTopTrack(pos);
            return topTracksList.get(pos);
        }
        return null;
    }

    public void updateTrackPlayerUi() {
        ViewUtil.setMediaPlayerUiState(TrackPlayerDialogFragment.trackPlayerControlPlay, ViewUtil.MEDIA_PLAYER_UI_STATE_PLAYING);
        seekBarUpdateHandler = new Handler();
        seekBarUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int progress = mediaPlayer.getCurrentPosition();
                    TrackPlayerDialogFragment.trackLapsedTv.setText(String.valueOf(ViewUtil.formatTrackLapsed(progress)));
                    TrackPlayerDialogFragment.seekBar.setProgress(progress);
                }
                seekBarUpdateHandler.postDelayed(this, 1000);
            }
        };
        seekBarUpdateHandler.postDelayed(seekBarUpdateRunnable, 0);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

}