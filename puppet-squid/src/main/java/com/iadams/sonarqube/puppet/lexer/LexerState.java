package com.iadams.sonarqube.puppet.lexer;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by iwarapter
 */
public class LexerState {

    final Deque<Integer> indentationStack = new ArrayDeque<Integer>();

    int brackets;
    boolean joined;

    public void reset() {
        indentationStack.clear();
        indentationStack.push(0);

        brackets = 0;
        joined = false;
    }

}
