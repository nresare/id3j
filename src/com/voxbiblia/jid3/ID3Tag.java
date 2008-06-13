package com.voxbiblia.jid3;

/**
 * Holds properties that goes into an ID3 tag of an MP3 file.
 */
public class ID3Tag
{
    private String artist, title, album, genre, track, comment, lyrics;
    private byte[] picture;
    private boolean compilation;

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAlbum()
    {
        return album;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public String getTrack()
    {
        return track;
    }

    public void setTrack(String track)
    {
        this.track = track;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getLyrics()
    {
        return lyrics;
    }

    public void setLyrics(String lyrics)
    {
        this.lyrics = lyrics;
    }

    public byte[] getPicture()
    {
        return picture;
    }

    public void setPicture(byte[] picture)
    {
        this.picture = picture;
    }

    public boolean isCompilation()
    {
        return compilation;
    }

    public void setCompilation(boolean compilation)
    {
        this.compilation = compilation;
    }
}
