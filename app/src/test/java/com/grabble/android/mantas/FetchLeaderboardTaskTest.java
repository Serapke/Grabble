package com.grabble.android.mantas;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Mantas on 19/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class FetchLeaderboardTaskTest {

    static final Long USER_ID = 2L;
    static final String USER_NICKNAME = "Grabbster";
    static final Integer USER_SCORE = 0;
    static final Integer USER_PLACE = 1;


    FetchLeaderboardTask fetchLeaderboardTask;

    @Mock
    Context mockContext;
    LeaderboardAdapter leaderboardAdapter;

    @Before
    public void setUp() {
        fetchLeaderboardTask = new FetchLeaderboardTask(mockContext, leaderboardAdapter);
    }

    @Test
    public void getLeaderboardDataFromJson_ReturnsListOfUsers() throws JSONException {
        when(mockContext.getString(R.string.json_user_id_key)).thenReturn("id");
        when(mockContext.getString(R.string.json_user_nickname_key)).thenReturn("nickname");
        when(mockContext.getString(R.string.json_user_score_key)).thenReturn("score");
        when(mockContext.getString(R.string.json_user_place_key)).thenReturn("place");

        JSONArray jsonArray = new JSONArray();
        JSONObject user = new JSONObject();
        user.put("id", USER_ID);
        user.put("nickname", USER_NICKNAME);
        user.put("score", USER_SCORE);
        user.put("place", USER_PLACE);
        jsonArray.put(user);

        List<User> result = fetchLeaderboardTask.getLeaderboardDataFromJson(jsonArray);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).getId());
        assertEquals(USER_NICKNAME, result.get(0).getNickname());
        assertEquals(USER_SCORE, result.get(0).getScore());
        assertEquals(USER_PLACE, result.get(0).getPlace());
    }

}
