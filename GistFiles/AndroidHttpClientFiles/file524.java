package io.github.fabiomim.tollerBot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

/**
 * Created by fabio on 10.05.2017.
 */
public class RedditCmds {


                        if (msg.toString().contains("!Reddit") && !message.getAuthor().isBot()) {

        UserAgent myUserAgent = UserAgent.of("desktop", "io.github.fabiomim.tollerBot", "v0.0.1", credential[1]);

        final RedditClient redditClient = new RedditClient(myUserAgent);

        Credentials credentials = Credentials.script(credential[1], credential[2], credential[3], credential[4]);

        OAuthData authData = null;

        try {
            authData = redditClient.getOAuthHelper().easyAuth(credentials);
        } catch (OAuthException e) {
            e.printStackTrace();
        }

        SubredditPaginator paginator = new SubredditPaginator(redditClient);

        redditClient.authenticate(authData);

        String[] reddarray;

        reddarray = msg.toString().split(" ");

        paginator.setLimit(5);

        paginator.setSubreddit(reddarray[1]);

        if (reddarray[1].equals("all")) {

            Listing<Submission> submissions = paginator.next();

            for (Submission s : submissions) {

                message.reply(" \n" + " \n" + " \n" + " \n" + "**" + s.getTitle() + "**" + "\n" + s.getUrl() + "\n" + "**" + s.getScore() + " Punkte." + "**"
                        + "\n" + "**/r/" + s.getSubredditName() + "**");

            }

        } else {

            Listing<Submission> submissions = paginator.next();

            for (Submission s : submissions) {

                message.reply(" \n" + " \n" + " \n" + " \n" + "**" + s.getTitle() + "**" + "\n" + s.getUrl() + "\n" + "**" + s.getScore() + " Punkte." + "**");

            }
        }

    }

}
