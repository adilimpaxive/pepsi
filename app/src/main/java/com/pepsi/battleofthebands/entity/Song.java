package com.pepsi.battleofthebands.entity;

import java.io.Serializable;

/**
 * This class might need refactoring as there are not getter and setters in it
 *
 * @author Sheraz Khilji
 */
public class Song implements Serializable {

    public static final String KEY = "Song";
    private static final long serialVersionUID = -2585217742395148035L;

    public int _id = 0;
    public String id = "1";
    public String episode_id = "1";
    public String name = "Kashan";
    public String thumbnail = "Allah";
    public String video_code = "2020";
    public String audio = "5424a2690d67e20200b3737a";
    public String duration = "40:39";
    public String singer_id = "1";
    public String singer_type = "Gitar";
    public Singer singer;
    public String number;
    public Episode episode;

    public String getnumber() {
        return number;
    }

    public void setnumber(String number) {
        this.number = number;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSongID() {
        return id;
    }

    public void setSongID(String songID) {
        this.id = songID;
    }

    public String getEpisode_id() {
        return episode_id;
    }

    public void setEpisode_id(String episode_id) {
        this.episode_id = episode_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_code() {
        return video_code;
    }

    public void setVideo_code(String video_code) {
        this.video_code = video_code;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSinger_id() {
        return singer_id;
    }

    public void setSinger_id(String singer_id) {
        this.singer_id = singer_id;
    }

    public String getSinger_type() {
        return singer_type;
    }

    public void setSinger_type(String singer_type) {
        this.singer_type = singer_type;
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public DownloadedSong toDownloadedSong() {
        DownloadedSong downloadedSong = new DownloadedSong();
        downloadedSong.setSongID(id);
        downloadedSong.setEpisode_id(episode_id);
        downloadedSong.setName(name);
        downloadedSong.setThumbnail(thumbnail);
        downloadedSong.setVideo_code(video_code);
        downloadedSong.setAudio(audio);
        downloadedSong.setDuration(duration);
        downloadedSong.setSinger_id(singer_id);
        downloadedSong.setSinger_type(singer_type);
        downloadedSong.setSinger(singer);
        downloadedSong.setnumber(number);
        return downloadedSong;
    }

    public class Episode {
        private String id;
        private String number;
        private String title;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        private String group;
        private String thumbnail;
        private String link;
        private String duration;
        private String season_id;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getSeason_id() {
            return season_id;
        }

        public void setSeason_id(String season_id) {
            this.season_id = season_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
