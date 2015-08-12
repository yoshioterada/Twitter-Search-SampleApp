/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoshio3.javaee6.sampleapp;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author tyoshio2002
 */
@ServerEndpoint("/twitter")
public class TwitterEndpoint {

    TwitterStreamImpl twitter;

    @OnOpen

    public void onOpen(Session session) throws IOException {
        System.out.println("CONNECTED");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message :" + message);
        if (message.equals("start")) {
            twitter = new TwitterStreamImpl();
            twitter.initTwitterStream(session, "Java");
        } else if (message.equals("stop")) {
            twitter.destroyStream();
        } else {
            String filter = message;
            System.out.println("Message :" + filter);
            twitter.destroyStream();
            twitter = new TwitterStreamImpl();
            twitter.initTwitterStream(session, filter);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        twitter.destroyStream();
    }
}
