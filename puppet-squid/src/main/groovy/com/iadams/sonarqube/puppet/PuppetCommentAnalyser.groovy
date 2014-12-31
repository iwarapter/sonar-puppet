package com.iadams.sonarqube.puppet

import org.sonar.squidbridge.CommentAnalyser

/**
 * Created by iwarapter
 */
class PuppetCommentAnalyser extends CommentAnalyser{

    @Override
    boolean isBlank(String line) {
        !line?.trim()
    }

    @Override
    String getContents(String comment){
        if (comment.startsWith("#")) {
            return comment.substring(1)
        } else if (comment.startsWith("/*")) {
            return comment.substring(2, comment.length() - 2)
        } else {
            throw new IllegalArgumentException()
        }
    }
}
