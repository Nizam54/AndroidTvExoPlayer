/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package cs.nzm.atvexo.player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.widget.PlaybackControlsRow;

import com.google.android.exoplayer2.ui.SubtitleView;

import cs.nzm.atvexo.R;
import cs.nzm.atvexo.models.MediaMetaData;


public class VideoConsumptionExampleWithExoPlayerFragment extends VideoSupportFragment {

    private static final String URL = "https://storage.googleapis.com/android-tv/Sample videos/"
            + "April Fool's 2013/Explore Treasure Mode with Google Maps.mp4";
    public static final String TAG = "VideoConsExoPlayer";
    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    final VideoSupportFragmentGlueHost mHost = new VideoSupportFragmentGlueHost(this);


    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int state) {
        }
    };
    private ExoPlayerAdapter playerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerAdapter = new ExoPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);
        mMediaPlayerGlue = new VideoMediaPlayerGlue<>(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!");
        }

        MediaMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(
                VideoExampleWithExoPlayerActivity.TAG);
        if (intentMetaData != null) {
            mMediaPlayerGlue.setTitle(intentMetaData.getMediaTitle());
            mMediaPlayerGlue.setSubtitle(intentMetaData.getMediaArtistName());
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(
                    Uri.parse(intentMetaData.getMediaSourcePath()));
            if (intentMetaData.isLive()) {
                mMediaPlayerGlue.setSeekProvider(null);
                mMediaPlayerGlue.setSeekEnabled(false);
            }
        } else {
            mMediaPlayerGlue.setTitle("Diving with Sharks");
            mMediaPlayerGlue.setSubtitle("A Googler");
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(URL));
            mMediaPlayerGlue.setSeekProvider(new PlaybackSeekMetadataDataProvider(getActivity(), URL, 5000));
        }
        mMediaPlayerGlue.playWhenPrepared();
        setBackgroundType(BG_LIGHT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        SubtitleView subtitleView = view.findViewById(R.id.leanback_subtitles);
        if (playerAdapter != null) {
            playerAdapter.setSubtitleView(subtitleView);
        }
        return view;
    }

    @Override
    public void onPause() {
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue.pause();
        }
        super.onPause();
    }

}
