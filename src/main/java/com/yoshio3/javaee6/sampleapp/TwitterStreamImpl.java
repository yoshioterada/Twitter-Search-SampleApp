package com.yoshio3.javaee6.sampleapp;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;


/**
 *
 * @author tyoshio2002
 */
public class TwitterStreamImpl extends StatusAdapter {

    private TwitterStream twStream = null;
    private Session session;

    public void initTwitterStream(Session session,String filterString) {
        this.session = session;

        //Twitter Stream の初期化
        twStream = TwitterStreamFactory.getSingleton();
        FilterQuery filter = new FilterQuery();
        filter.track(new String[]{filterString});
        filter.language(new String[]{"ja"});
        twStream.addListener(this);
        twStream.filter(filter);
    }

    @Override
    public void onStatus(Status status) {
        try {
            //Twitter のフィルターに引っかかった場合
            User user = status.getUser();
            String resStr = "@" + user.getScreenName() + " : " + status.getText();
            String encodedString = encode(resStr);
            session.getBasicRemote().sendText(encodedString);
            System.out.println(encodedString);
        } catch (IOException ex) {
            Logger.getLogger(TwitterStreamImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * HTMLエンコードが必要な文字 *
     */
    static char[] htmlEncChar = {'&', '"', '<', '>'};
    /**
     * HTMLエンコードした文字列 *
     */
    static String[] htmlEncStr = {"&amp;", "&quot;", "&lt;", "&gt;"};

    /**
     * HTMLエンコード処理。 &,",<,>の置換
  *
     */
    public String encode(String strIn) {
        if (strIn == null) {
            return (null);
        }

        // HTMLエンコード処理
        StringBuffer strOut = new StringBuffer(strIn);
        // エンコードが必要な文字を順番に処理
        for (int i = 0; i < htmlEncChar.length; i++) {
            // エンコードが必要な文字の検索
            int idx = strOut.toString().indexOf(htmlEncChar[i]);

            while (idx != -1) {
                // エンコードが必要な文字の置換
                strOut.setCharAt(idx, htmlEncStr[i].charAt(0));
                strOut.insert(idx + 1, htmlEncStr[i].substring(1));

                // 次のエンコードが必要な文字の検索
                idx = idx + htmlEncStr[i].length();
                idx = strOut.toString().indexOf(htmlEncChar[i], idx);
            }
        }
        return (strOut.toString());
    }

    public void destroyStream() {
        twStream.removeListener(this);
        twStream.shutdown();
    }
}
