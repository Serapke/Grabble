package com.grabble.android.mantas;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mantas on 08/01/2017.
 */

/**
 *  Enum corresponding to the letter point values table in the coursework description
 */
public enum LetterPointValues {

    A('A', 3),
    B('B', 20),
    C('C', 13),
    D('D', 10),
    E('E', 1),
    F('F', 15),
    G('G', 18),
    H('H', 9),
    I('I', 5),
    J('J', 25),
    K('K', 22),
    L('L', 11),
    M('M', 14),
    N('N', 6),
    O('O', 4),
    P('P', 19),
    Q('Q', 24),
    R('R', 8),
    S('S', 7),
    T('T', 2),
    U('U', 12),
    V('V', 21),
    W('W', 17),
    X('X', 23),
    Y('Y', 16),
    Z('Z', 26);

    private final Character letter;
    private final Integer value;

    private static final Map<Character, Integer> table = new HashMap<>();
    static {
        for (LetterPointValues v : LetterPointValues.values()) {
            table.put(v.letter, v.value);
        }
    }

    LetterPointValues(Character letter, Integer value) {
        this.letter = letter;
        this.value = value;
    }

    public Character getLetter() {
        return letter;
    }

    public Integer getValue() {
        return value;
    }

    public static Integer getValueByLetter(Character letter) {
        return table.get(letter);
    }
}
