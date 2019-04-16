package com.wow.musicapi.model;

import com.wow.musicapi.api.MusicProvider;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haohua on 2018/2/9.
 */
public interface Song extends Serializable {
    String getName();

    String getSongId();

    List<? extends Artist> getArtists();

    String getFormattedArtistsString();

    Album getAlbum();

    MusicProvider getMusicProvider();

    void setMusicLink(MusicLink musicLink);

    MusicLink getMusicLink();

    String getPicUrl();

    Lyric getLyric();
}
