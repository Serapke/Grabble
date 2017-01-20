package com.grabble.android.mantas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by Mantas on 17/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    private static final String VALID_NICKNAME = "grabbster9000";
    private static final String TOO_SHORT_NICKNAME = "use";
    private static final String TOO_LONG_NICKNAME = "veryverylongnickname1";
    private static final String NICKNAME_STARTING_WITH_NONALPHA_CHAR = "2nickname";
    private static final String INVALID_CHARACTERS_NICKNAME = "superduper!nickname$";

    LoginActivity loginActivity;

    @Before
    public void initialize() {
        loginActivity = new LoginActivity();
    }

    @Test
    public void isNicknameValid_CorrectNickname_ReturnsTrue() {
        assertTrue(loginActivity.isNicknameValid(VALID_NICKNAME));
    }

    @Test
    public void isNicknameValid_IncorrectNickname_ReturnsFalse() {
        assertFalse(loginActivity.isNicknameValid(null));
        assertFalse(loginActivity.isNicknameValid(TOO_SHORT_NICKNAME));
        assertFalse(loginActivity.isNicknameValid(TOO_LONG_NICKNAME));
        assertFalse(loginActivity.isNicknameValid(INVALID_CHARACTERS_NICKNAME));
        assertFalse(loginActivity.isNicknameValid(NICKNAME_STARTING_WITH_NONALPHA_CHAR));
    }
}
