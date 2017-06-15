package com.m360.android.presentation_layer.player.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.m360.android.R;
import com.m360.android.activity.ElementViewerActivity;
import com.m360.android.ancestors.ActivityPresenter;
import com.m360.android.ancestors.FragmentPresenter;
import com.m360.android.fragment.chat.PlayerElementChatFragment;
import com.m360.android.model.PlayerSession.CourseSession;
import com.m360.android.model.course.CourseDetailed;
import com.m360.android.model.course.CourseMode;
import com.m360.android.model.courseElement.Element;
import com.m360.android.model.courseElement.question.QuestionAnswerResponse;
import com.m360.android.model.courseElement.question.QuestionType;
import com.m360.android.model.courseElement.vRealm.CourseElement;
import com.m360.android.model.courseElement.vRealm.Question;
import com.m360.android.model.enums.PostCollectionType;
import com.m360.android.presentation_layer.chat.ReplyFormatted;
import com.m360.android.presentation_layer.player.model.collectablesanswers.CollectedAnswers;
import com.m360.android.presentation_layer.player.view.ElementFragment;
import com.m360.android.presentation_layer.player.view.MediaFragment;
import com.m360.android.presentation_layer.player.view.ModulePlayerActivity;
import com.m360.android.presentation_layer.player.view.PlayerViewContract;
import com.m360.android.presentation_layer.player.view.QuestionFragment;
import com.m360.android.presentation_layer.player.view.SheetFragment;
import com.m360.android.presentation_layer.presentations_models.NavItem;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.realm.Realm;

/**
 * Created by renaud on 16/08/16.
 */
public abstract class PlayerPresenter extends ActivityPresenter {


    protected Realm realm;
    protected CourseLauncherPresenter courseLauncherPresenter;

    public static PlayerPresenter get(boolean onLine, ModulePlayerActivity modulePlayerActivity, String courseId, String programId, CourseMode courseMode) {

        if (onLine) {
            return new OnlinePlayerPresenter(modulePlayerActivity, courseId, programId, courseMode);
        } else {
            return new OfflinePlayerPresenter(modulePlayerActivity, courseId, programId, courseMode);
        }
    }


    @Override
    public void registerFragmentPresenter(FragmentPresenter presenter) {
        if (presenter instanceof CourseLauncherPresenter) {
            courseLauncherPresenter = (CourseLauncherPresenter) presenter;
        } else {
            super.registerFragmentPresenter(presenter);
        }
    }

  //????
    public void unregisterCourseLauncherPresenter() {
        courseLauncherPresenter = null;
    }


    protected PlayerPresenter(ModulePlayerActivity modulePlayerActivity, String courseId, String programId, CourseMode courseMode) {

        super(modulePlayerActivity);

        //This gets the main thread instance ie the activity's instance (factory de singletons par thread)
        this.realm = Realm.getDefaultInstance();
        this.ctx = modulePlayerActivity;
        this.playerViewContract = modulePlayerActivity;
        handler = new Handler();
    }

      

  

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        ...

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        ...

    }


}
