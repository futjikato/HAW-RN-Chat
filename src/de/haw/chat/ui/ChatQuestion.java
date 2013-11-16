package de.haw.chat.ui;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.ui
 */
public interface ChatQuestion {

    public String getQuestion();

    public void processAnswer(String userAnswer);

}
