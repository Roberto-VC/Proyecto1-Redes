package com.redes;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;

public class temp {
    XMPPTCPConnectionConfiguration config = null;
    {
        try {

            config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("val20441", "gamesfan10")
                    .setXmppDomain("alumchat.xyz")
                    .setHost("alumchat.xyz")
                    .setPort(5222)
                    .build();

            AbstractXMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            System.out.println(":)");
            connection.login();

            ChatManager chatManager = ChatManager.getInstanceFor(connection);

            Chat chat = chatManager.chatWith(JidCreate.from("echobat@alumchat.xyz").asEntityBareJidOrThrow());

            chat.send("Hello!");

        } catch (Exception e) {
            System.out.println("Sad");
        }
    }
}
