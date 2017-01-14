package com.grabble.android.mantas;

/**
 * Created by Mantas on 14/01/2017.
 */

public class User {
    private final Long id;
    private final String nickname;
    private final Integer score;
    private final Integer place;

    public User(Long id, String nickname, Integer score, Integer place) {
        this.id = id;
        this.nickname = nickname;
        this.score = score;
        this.place = place;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getPlace() {
        return place;
    }
}
