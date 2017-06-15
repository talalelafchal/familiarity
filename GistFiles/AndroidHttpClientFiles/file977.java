/**
 * Created by Fabio on 14.04.2017.
 */


package io.github.fabiomim.tollerBot;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Main_DiscordBot {
    public static void main(String[] args) throws IOException {






        BufferedReader br = new BufferedReader(new FileReader("Settings.txt"));
        String line = br.readLine();

        final String[] credential;

        credential = line.split(" ");

        br.close();


        String token = credential[0];
        DiscordAPI api = Javacord.getApi(token, true);

        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(final DiscordAPI api) {

                api.registerListener(new MessageCreateListener() {
                    public void onMessageCreate(DiscordAPI api, Message message) {
                        StringBuffer msg = new StringBuffer(message.getContent());

                        if(msg.toString().toUpperCase().contains("!REDDIT")){
                            RedditCmds redditCmds = new RedditCmds(msg);
                        }
                        else {
                            PlainChatCmds plainChatCmds =new PlainChatCmds(msg);
                        }


                    }//closes onMessageCreate


                }); //Closes MessageListener
            } //close onSuccess


            public void onFailure(Throwable t) {
                // login failed
                t.printStackTrace();

            } //closes On Failure
        }); // Closes api


    } //closes Run

} //closes class










