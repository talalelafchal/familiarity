    private ContextImpl(ContextImpl container, ActivityThread mainThread,
            LoadedApk packageInfo, IBinder activityToken, UserHandle user, boolean restricted,
            Display display, Configuration overrideConfiguration) {
        mOuterContext = this;

        mMainThread = mainThread;
        mActivityToken = activityToken;
        mRestricted = restricted;

        if (user == null) {
            user = Process.myUserHandle();
        }
        mUser = user;

        mPackageInfo = packageInfo;
        mResourcesManager = ResourcesManager.getInstance();
        mDisplay = display;
        mOverrideConfiguration = overrideConfiguration;
        ...
    }
    
    /**
     * System private API for communicating with the application.  This is given to
     * the activity manager by an application  when it starts up, for the activity
     * manager to tell the application about things it needs to do.
     *
     * {@hide}
     */
    public interface IApplicationThread extends IInterface {
        void schedulePauseActivity(IBinder token, boolean finished, boolean userLeaving,
            int configChanges, boolean dontReport) throws RemoteException;
        void scheduleStopActivity(IBinder token, boolean showWindow,
                int configChanges) throws RemoteException;
        ...
    }

    /**
     * This manages the execution of the main thread in an
     * application process, scheduling and executing activities,
     * broadcasts, and other operations on it as the activity
     * manager requests.
     *
     * {@hide}
     */
    public final class ActivityThread {
        ...
    }
    
    /**
     * Base class for implementing application instrumentation code.  When running
     * with instrumentation turned on, this class will be instantiated for you
     * before any of the application code, allowing you to monitor all of the
     * interaction the system has with the application.  An Instrumentation
     * implementation is described to the system through an AndroidManifest.xml's
     * &lt;instrumentation&gt; tag.
     */
    public class Instrumentation {
        ...
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intent.getFlags()&Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
            throw new AndroidRuntimeException(
                    "Calling startActivity() from outside of an Activity "
                    + " context requires the FLAG_ACTIVITY_NEW_TASK flag."
                    + " Is this really what you want?");
        }
        mMainThread.getInstrumentation().execStartActivity(
            getOuterContext(), mMainThread.getApplicationThread(), null,
            (Activity)null, intent, -1, options);
    }
    
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        IApplicationThread whoThread = (IApplicationThread) contextThread;
        ...
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess();
            /* { Dual-Screen */
            if (DUALSCREEN_ENABLED) {
                DualScreenManager.parseDualScreenLaunchParams(who, intent);
            }
            /* Dual-Screen } */
            int result = ActivityManagerNative.getDefault()
                .startActivity(whoThread, who.getBasePackageName(), intent,
                        intent.resolveTypeIfNeeded(who.getContentResolver()),
                        token, target != null ? target.mEmbeddedID : null,
                        requestCode, 0, null, options);
            checkStartActivityResult(result, intent);
        } catch (RemoteException e) {
        }
        return null;
    }
    
    @Override
    public final int startActivity(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) {
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "startActivity");
        int s = startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, options,
            UserHandle.getCallingUserId());
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        return s;
    }
    
    @Override
    public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options, int userId) {
        enforceNotIsolatedCaller("startActivity");
        userId = handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId,
                false, ALLOW_FULL_ONLY, "startActivity", null);

        ...

        // TODO: Switch to user app stacks here.
        return mStackSupervisor.startActivityMayWait(caller, -1, callingPackage, intent,
                resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
                profilerInfo, null, null, options, userId, null, null);
    }
    
    final int startActivityMayWait(IApplicationThread caller, int callingUid,
            String callingPackage, Intent intent, String resolvedType,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode, int startFlags,
            ProfilerInfo profilerInfo, WaitResult outResult, Configuration config,
            Bundle options, int userId, IActivityContainer iContainer, TaskRecord inTask) {
        // Refuse possible leaked file descriptors
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        ActivityRecord sr = resultTo != null ? isInAnyStackLocked(resultTo) : null;
        boolean isNotFromResolverActivity = sr != null && sr.isNotResolverActivity();
        boolean componentSpecified = intent.getComponent() != null && isNotFromResolverActivity;
        
        ...

        // Don't modify the client's object!
        intent = new Intent(intent);

        // Collect information about the target of the Intent.
        ActivityInfo aInfo = resolveActivity(/* { Dual-Screen */caller, /* Dual-Screen } */intent, resolvedType, startFlags,
                profilerInfo, userId);

        ActivityContainer container = (ActivityContainer)iContainer;
        synchronized (mService) {
            final int realCallingPid = Binder.getCallingPid();
            final int realCallingUid = Binder.getCallingUid();
            int callingPid;
            if (callingUid >= 0) {
                callingPid = -1;
            } else if (caller == null) {
                callingPid = realCallingPid;
                callingUid = realCallingUid;
            } else {
                callingPid = callingUid = -1;
            }

            ActivityStack stack;
            if (container == null || container.mStack.isOnHomeDisplay()) {
                stack = getFocusedStack();
            } else {
                stack = container.mStack;
            }
            /* { Dual-Screen */
            if (DUALSCREEN_ENABLED) {
                stack.mConfigWillChange = config != null
                        && mService.mConfigurations.get(stack.getDisplayId()).diff(config) != 0;
            } else {
            /* Dual-Screen } */
                stack.mConfigWillChange = config != null
                        && mService.mConfiguration.diff(config) != 0;
            }
            if (DEBUG_CONFIGURATION) Slog.v(TAG,
                    "Starting activity when config will change = " + stack.mConfigWillChange);

            final long origId = Binder.clearCallingIdentity();

            ...

            int res = startActivityLocked(caller, intent, resolvedType, aInfo,
                    voiceSession, voiceInteractor, resultTo, resultWho,
                    requestCode, callingPid, callingUid, callingPackage,
                    realCallingPid, realCallingUid, startFlags, options,
                    componentSpecified, null, container, inTask);

            Binder.restoreCallingIdentity(origId);

            ...

            if (outResult != null) {
                outResult.result = res;
                if (res == ActivityManager.START_SUCCESS) {
                    mWaitingActivityLaunched.add(outResult);
                    do {
                        try {
                            mService.wait();
                        } catch (InterruptedException e) {
                        }
                    } while (!outResult.timeout && outResult.who == null);
                } else if (res == ActivityManager.START_TASK_TO_FRONT) {
                    ActivityRecord r = stack.topRunningActivityLocked(null);
                    if (r != null && r.nowVisible && r.state == ActivityState.RESUMED) {
                        outResult.timeout = false;
                        outResult.who = new ComponentName(r.info.packageName, r.info.name);
                        outResult.totalTime = 0;
                        outResult.thisTime = 0;
                    } else {
                        outResult.thisTime = SystemClock.uptimeMillis();
                        mWaitingActivityVisible.add(outResult);
                        do {
                            try {
                                mService.wait();
                            } catch (InterruptedException e) {
                            }
                        } while (!outResult.timeout && outResult.who == null);
                    }
                }
            }

            return res;
        }
    }
    
    ActivityInfo resolveActivity(IApplicationThread caller, Intent intent, String resolvedType, int startFlags,
            ProfilerInfo profilerInfo, int userId) {
        boolean displayChooserSelected = false;
        ProcessRecord callerApp = null;
        ActivityRecord callerActivityRecord = null;
        int callerActivitiesSize = 0;
        ...
        // Collect information about the target of the Intent.
        ActivityInfo aInfo;
        ResolveInfo rInfo = null;
        try {
            /* { Dual-Screen */
            Intent queryIntent = null;
            ...
                rInfo = AppGlobals.getPackageManager().resolveIntent(
                                intent, resolvedType,
                                PackageManager.MATCH_DEFAULT_ONLY
                                            | ActivityManagerService.STOCK_PM_FLAGS, userId);
                aInfo = rInfo != null ? rInfo.activityInfo : null;
            ...
        } catch (RemoteException e) {
            aInfo = null;
        }
        ...
        if (aInfo != null) {
            // Store the found target back into the intent, because now that
            // we have it we never want to do this again.  For example, if the
            // user navigates back to this point in the history, we should
            // always restart the exact same activity.
            ...
            /* Dual-Screen } */
            intent.setComponent(new ComponentName(
                    aInfo.applicationInfo.packageName, aInfo.name));

            ...

            // Don't debug things in the system process
            if ((startFlags&ActivityManager.START_FLAG_DEBUG) != 0) {
                if (!aInfo.processName.equals("system")) {
                    mService.setDebugApp(aInfo.processName, true, false);
                }
            }

            if ((startFlags&ActivityManager.START_FLAG_OPENGL_TRACES) != 0) {
                if (!aInfo.processName.equals("system")) {
                    mService.setOpenGlTraceApp(aInfo.applicationInfo, aInfo.processName);
                }
            }

            if (profilerInfo != null) {
                if (!aInfo.processName.equals("system")) {
                    mService.setProfileApp(aInfo.applicationInfo, aInfo.processName, profilerInfo);
                }
            }
        }
        return aInfo;
    }
    
    final int startActivityLocked(IApplicationThread caller,
            Intent intent, String resolvedType, ActivityInfo aInfo,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode,
            int callingPid, int callingUid, String callingPackage,
            int realCallingPid, int realCallingUid, int startFlags, Bundle options,
            boolean componentSpecified, ActivityRecord[] outActivity, ActivityContainer container,
            TaskRecord inTask) {
        int err = ActivityManager.START_SUCCESS;

        ProcessRecord callerApp = null;
        if (caller != null) {
            callerApp = mService.getRecordForAppLocked(caller);
            if (callerApp != null) {
                callingPid = callerApp.pid;
                callingUid = callerApp.info.uid;
            } else {
                Slog.w(TAG, "Unable to find app for caller " + caller
                      + " (pid=" + callingPid + ") when starting: "
                      + intent.toString());
                err = ActivityManager.START_PERMISSION_DENIED;
            }
        }

        ComponentName cmp = null;
        String className = "";
        ...

        final int userId = aInfo != null ? UserHandle.getUserId(aInfo.applicationInfo.uid) : 0; //WTL_EDM

        if (err == ActivityManager.START_SUCCESS) {
            Slog.secI(TAG, "START u" + userId + " {" + intent.toShortString(true, true, true, false)
                    + "} from uid " + callingUid + " pid " + realCallingPid
                    + " on display " + (container == null ? (mFocusedStack == null ?
                            Display.DEFAULT_DISPLAY : mFocusedStack.mDisplayId) :
                            (container.mActivityDisplay == null ? Display.DEFAULT_DISPLAY :
                                    container.mActivityDisplay.mDisplayId)));
            ...
        }

        ...

        ActivityRecord sourceRecord = null;
        ActivityRecord resultRecord = null;
        if (resultTo != null) {
            sourceRecord = isInAnyStackLocked(resultTo);
            if (DEBUG_RESULTS) Slog.v(
                TAG, "Will send result to " + resultTo + " " + sourceRecord);
            if (sourceRecord != null) {
                if (requestCode >= 0 && !sourceRecord.finishing) {
                    resultRecord = sourceRecord;
                }
            }
        }

        final int launchFlags = intent.getFlags();

        ...

        if (err == ActivityManager.START_SUCCESS && intent.getComponent() == null) {
            // We couldn't find a class that can handle the given Intent.
            // That's the end of that!
            err = ActivityManager.START_INTENT_NOT_RESOLVED;
        }

        if (err == ActivityManager.START_SUCCESS && aInfo == null) {
            // We couldn't find the specific class specified in the Intent.
            // Also the end of the line.
            err = ActivityManager.START_CLASS_NOT_FOUND;
        }

        ...

        final ActivityStack resultStack = resultRecord == null ? null : resultRecord.task.stack;

        if (err != ActivityManager.START_SUCCESS) {
            if (resultStack != null && resultRecord != null) {
                resultStack.sendActivityResultLocked(-1,
                    resultRecord, resultWho, requestCode,
                    Activity.RESULT_CANCELED, null);
            }
            ActivityOptions.abort(options);
            if (SecProductFeature_KNOX.SEC_PRODUCT_FEATURE_KNOX_SUPPORT_MDM) {
                AuditLog.logAsUser(AuditLog.NOTICE, AuditLog.AUDIT_LOG_GROUP_APPLICATION, false,
                        android.os.Process.myPid(), this.getClass().getSimpleName(),
                        AuditEvents.START_ACTIVITY + className + AuditEvents.FAILED, userId);
            }

            return err;
        }
        ...
        final int startAnyPerm = mService.checkPermission(
                START_ANY_ACTIVITY, callingPid, callingUid);

        int componentPerm =  PERMISSION_DENIED;
        ...

        if (startAnyPerm != PERMISSION_GRANTED && componentPerm != PERMISSION_GRANTED) {
            if (resultStack != null && resultRecord != null) {
                resultStack.sendActivityResultLocked(-1,
                    resultRecord, resultWho, requestCode,
                    Activity.RESULT_CANCELED, null);
            }
            String msg;
            if (!aInfo.exported) {
                msg = "Permission Denial: starting " + intent.toString()
                        + " from " + callerApp + " (pid=" + callingPid
                        + ", uid=" + callingUid + ")"
                        + " not exported from uid " + aInfo.applicationInfo.uid;
            } else {
                msg = "Permission Denial: starting " + intent.toString()
                        + " from " + callerApp + " (pid=" + callingPid
                        + ", uid=" + callingUid + ")"
                        + " requires " + aInfo.permission;
            }
            Slog.w(TAG, msg);
            if (SecProductFeature_KNOX.SEC_PRODUCT_FEATURE_KNOX_SUPPORT_MDM) {
                AuditLog.logAsUser(AuditLog.NOTICE, AuditLog.AUDIT_LOG_GROUP_APPLICATION, false,
                        android.os.Process.myPid(), this.getClass().getSimpleName(),
                        AuditEvents.START_ACTIVITY + className + AuditEvents.FAILED, userId);
            }
            throw new SecurityException(msg);
        }

        boolean abort = !mService.mIntentFirewall.checkStartActivity(intent, callingUid,
                callingPid, resolvedType, aInfo.applicationInfo);

        if (mService.mController != null) {
            try {
                // The Intent we give to the watcher has the extra data
                // stripped off, since it can contain private information.
                Intent watchIntent = intent.cloneFilter();
                abort |= !mService.mController.activityStarting(watchIntent,
                        aInfo.applicationInfo.packageName);
            } catch (RemoteException e) {
                mService.mController = null;
            }
        }

        if (abort) {
            if (resultStack != null && resultRecord != null) {
                resultStack.sendActivityResultLocked(-1, resultRecord, resultWho, requestCode,
                        Activity.RESULT_CANCELED, null);
            }
            // We pretend to the caller that it was really started, but
            // they will just get a cancel result.
            ActivityOptions.abort(options);
            ...
            return ActivityManager.START_SUCCESS;
        }

        ...

        ActivityRecord r;
        ...
             r = new ActivityRecord(mService, callerApp, callingUid, callingPackage,
                    intent, resolvedType, aInfo, mService.mConfiguration, resultRecord, resultWho,
                    requestCode, componentSpecified, this, container, options
                    /* { Multi-Window */, sourceRecord, resultTo /* Multi-Window } */);
        ...
        if (outActivity != null) {
            outActivity[0] = r;
        }

        ...

        final ActivityStack stack = getFocusedStack();
        if (voiceSession == null && (stack.mResumedActivity == null
                || stack.mResumedActivity.info.applicationInfo.uid != callingUid)) {
            if (!mService.checkAppSwitchAllowedLocked(callingPid, callingUid,
                    realCallingPid, realCallingUid, "Activity start")) {
                PendingActivityLaunch pal =
                        new PendingActivityLaunch(r, sourceRecord, startFlags, stack);
                mPendingActivityLaunches.add(pal);
                ActivityOptions.abort(options);
                ...
                return ActivityManager.START_SWITCHES_CANCELED;
            }
        }

        ...

        err = startActivityUncheckedLocked(r, sourceRecord, voiceSession, voiceInteractor,
                startFlags, true, options, inTask);

        ...
        return err;
    }
    
    final int startActivityUncheckedLocked(ActivityRecord r, ActivityRecord sourceRecord,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags,
            boolean doResume, Bundle options, TaskRecord inTask) {
        ...
        final Intent intent = r.intent;
        final int callingUid = r.launchedFromUid;

        ...

        // In some flows in to this function, we retrieve the task record and hold on to it
        // without a lock before calling back in to here...  so the task at this point may
        // not actually be in recents.  Check for that, and if it isn't in recents just
        // consider it invalid.
        if (inTask != null && !inTask.inRecents) {
            Slog.w(TAG, "Starting activity in task not in recents: " + inTask);
            inTask = null;
        }

        final boolean launchSingleTop = r.launchMode == ActivityInfo.LAUNCH_SINGLE_TOP;
        final boolean launchSingleInstance = r.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE;
        final boolean launchSingleTask = r.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK;

        int launchFlags = intent.getFlags();
        if ((launchFlags & Intent.FLAG_ACTIVITY_NEW_DOCUMENT) != 0 &&
                (launchSingleInstance || launchSingleTask)) {
            // We have a conflict between the Intent and the Activity manifest, manifest wins.
            Slog.i(TAG, "Ignoring FLAG_ACTIVITY_NEW_DOCUMENT, launchMode is " +
                    "\"singleInstance\" or \"singleTask\"");
            launchFlags &=
                    ~(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            switch (r.info.documentLaunchMode) {
                case ActivityInfo.DOCUMENT_LAUNCH_NONE:
                    break;
                case ActivityInfo.DOCUMENT_LAUNCH_INTO_EXISTING:
                    launchFlags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
                    break;
                case ActivityInfo.DOCUMENT_LAUNCH_ALWAYS:
                    launchFlags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
                    break;
                case ActivityInfo.DOCUMENT_LAUNCH_NEVER:
                    launchFlags &= ~Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
                    break;
            }
        }

        final boolean launchTaskBehind = r.mLaunchTaskBehind
                && !launchSingleTask && !launchSingleInstance
                && (launchFlags & Intent.FLAG_ACTIVITY_NEW_DOCUMENT) != 0;

        if (r.resultTo != null && (launchFlags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
            // For whatever reason this activity is being launched into a new
            // task...  yet the caller has requested a result back.  Well, that
            // is pretty messed up, so instead immediately send back a cancel
            // and let the new task continue launched as normal without a
            // dependency on its originator.
            Slog.w(TAG, "Activity is launching as a new task, so cancelling activity result.");
            r.resultTo.task.stack.sendActivityResultLocked(-1,
                    r.resultTo, r.resultWho, r.requestCode,
                    Activity.RESULT_CANCELED, null);
            r.resultTo = null;
        }

        ...

        ActivityStack targetStack = null;
        
        ...
        // things about TASK management, too complicated.
        ...

        ...
        
        ActivityStack.logStartActivity(EventLogTags.AM_CREATE_ACTIVITY, r, r.task);
        targetStack.mLastPausedActivity = null;
        targetStack.startActivityLocked(r, newTask, doResume, keepCurTransition, options
                /* { Dual-Screen */, createdNewTask/* Dual-Screen } */);
        if (!launchTaskBehind) {
            // Don't set focus on an activity that's going to the back.
            mService.setFocusedActivityLocked(r, "startedActivity");
        }
        ...
        return ActivityManager.START_SUCCESS;
    }
    
    final void startActivityLocked(ActivityRecord r, boolean newTask,
            boolean doResume, boolean keepCurTransition, Bundle options
            /* { Dual-Screen */, boolean createdNewTask/* Dual-Screen } */) {
        ...
        TaskRecord task = null;
        ...

        task = r.task;

        ....
        
        task.addActivityToTop(r);
        task.setFrontOfTask();

        r.putInHistory();
        ...

        if (!isHomeStack() || numActivities() > 0) {
            ...
            mWindowManager.addAppToken(task.mActivities.indexOf(r),
                    r.appToken, r.task.taskId, mStackId, r.info.screenOrientation, r.fullscreen,
                    (r.info.flags & ActivityInfo.FLAG_SHOW_ON_LOCK_SCREEN) != 0, r.userId,
                    r.info.configChanges, task.voiceSession != null, r.mLaunchTaskBehind
                    /* { Multi-Window */, r.multiWindowStyle/* Multi-Window } */
                    ,/* { Camera Rotation */ r.skipRotationAnimation /* Camera Rotation } */, r.noDisplay);
            ...
            ...
                mWindowManager.setAppStartingWindow(
                        r.appToken, r.packageName, r.theme,
                        mService.compatibilityInfoForPackageLocked(
                                r.info.applicationInfo), r.nonLocalizedLabel,
                        r.labelRes, r.icon, r.logo, r.windowFlags,
                        prev != null ? prev.appToken : null, showStartingIcon
                                /* { CustomStartingWindow */, customStartingWindowData/* CustomStartingWindow } */
                                /* { Dual-Screen */, r.getDisplayId()/* Dual-Screen } */
                                /* { Elastic Application Framework */, r.app!= null ? r.app.userId : UserHandle.getUserId(r.appInfo.uid) /* Elastic Application Framework } */);
                r.mStartingWindowShown = true;
            ...
        } else {
            // If this is the first activity, don't do any fancy animations,
            // because there is nothing for it to animate on top of.
            mWindowManager.addAppToken(task.mActivities.indexOf(r), r.appToken,
                    r.task.taskId, mStackId, r.info.screenOrientation, r.fullscreen,
                    (r.info.flags & ActivityInfo.FLAG_SHOW_ON_LOCK_SCREEN) != 0, r.userId,
                    r.info.configChanges, task.voiceSession != null, r.mLaunchTaskBehind
                    /* { Multi-Window */, r.multiWindowStyle/* Multi-Window } */
                    , /* { Camera Rotation */ r.skipRotationAnimation /* Camera Rotation } */);
            ActivityOptions.abort(options);
            options = null;
        }
        ...
        if (doResume) {
            mStackSupervisor.resumeTopActivitiesLocked(this, r, options);
        }
    }
    
    boolean resumeTopActivitiesLocked(ActivityStack targetStack, ActivityRecord target,
            Bundle targetOptions) {
        ...

        if (targetStack == null) {
            targetStack = getFocusedStack();
        }
        // Do targetStack first.
        boolean result = false;

        ...
        if (isFrontStack(targetStack)) {
            result = targetStack.resumeTopActivityLocked(target, targetOptions);
        }
        for (int displayNdx = mActivityDisplays.size() - 1; displayNdx >= 0; --displayNdx) {
            final ArrayList<ActivityStack> stacks = mActivityDisplays.valueAt(displayNdx).mStacks;
            for (int stackNdx = stacks.size() - 1; stackNdx >= 0; --stackNdx) {
                final ActivityStack stack = stacks.get(stackNdx);
                ...
                    stack.resumeTopActivityLocked(null);
                ...
            }
        }
        ...

        return result;
    }
    
    final boolean resumeTopActivityLocked(ActivityRecord prev, Bundle options) {
        ...
            result = resumeTopActivityInnerLocked(prev, options);
        ...
        return result;
    }
    
    final boolean resumeTopActivityInnerLocked(ActivityRecord prev, Bundle options) {
        ...

        cancelInitializingActivities();

        ...

        // We need to start pausing the current activity so the top one
        // can be resumed...
        boolean dontWaitForPause = (next.info.flags&ActivityInfo.FLAG_RESUME_WHILE_PAUSING) != 0;
        if (prev == null && (next.info.flags&ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH) != 0) {
            dontWaitForPause = false;
        }
        boolean pausing = mStackSupervisor.pauseBackStacks(userLeaving, true, dontWaitForPause);
        if (mResumedActivity != null) {
            if (DEBUG_STATES) Slog.d(TAG, "resumeTopActivityLocked: Pausing " + mResumedActivity);
            pausing |= startPausingLocked(userLeaving, false, true, dontWaitForPause);
        }
        ...

        return true;
    }
    
    final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping, boolean resuming,
            boolean dontWait) {
        ...

        if (prev.app != null && prev.app.thread != null) {
            if (DEBUG_PAUSE) Slog.v(TAG, "Enqueueing pending pause: " + prev);
            try {
                EventLog.writeEvent(EventLogTags.AM_PAUSE_ACTIVITY,
                        prev.userId, System.identityHashCode(prev),
                        prev.shortComponentName, prev.multiWindowStyle.getType());
                mService.updateUsageStats(prev, false);
                prev.app.thread.schedulePauseActivity(prev.appToken, prev.finishing,
                        userLeaving, prev.configChangeFlags, dontWait);
            } catch (Exception e) {
                // Ignore exception, if process died other code will cleanup.
                Slog.w(TAG, "Exception thrown during pause", e);
                mPausingActivity = null;
                mLastPausedActivity = null;
                mLastNoHistoryActivity = null;
                ...
            }
        } else {
            mPausingActivity = null;
            mLastPausedActivity = null;
            mLastNoHistoryActivity = null;
            ...
        }
        
        ...
        
            if (dontWait) {
                // If the caller said they don't want to wait for the pause, then complete
                // the pause now.
                completePauseLocked(false, prev);
                return false;

            } else {
                // Schedule a pause timeout in case the app doesn't respond.
                // We don't give it much time because this directly impacts the
                // responsiveness seen by the user.
                Message msg = mHandler.obtainMessage(PAUSE_TIMEOUT_MSG);
                msg.obj = prev;
                prev.pauseTime = SystemClock.uptimeMillis();
                mHandler.sendMessageDelayed(msg, PAUSE_TIMEOUT);
                if (DEBUG_PAUSE) Slog.v(TAG, "Waiting for pause to complete...");
                return true;
            }

        ...
    }
    
    public final void schedulePauseActivity(IBinder token, boolean finished,
            boolean userLeaving, int configChanges, boolean dontReport) {
        sendMessage(
                finished ? H.PAUSE_ACTIVITY_FINISHING : H.PAUSE_ACTIVITY,
                token,
                (userLeaving ? 1 : 0) | (dontReport ? 2 : 0),
                configChanges);
    }
    
    //H
    
    public void handleMessage(Message msg) {
        ...
        case PAUSE_ACTIVITY:
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPause");
        handlePauseActivity((IBinder)msg.obj, false, (msg.arg1&1) != 0, msg.arg2,
                (msg.arg1&2) != 0);
        maybeSnapshot();
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        break;
        ...
    }
    
    private void handlePauseActivity(IBinder token, boolean finished,
            boolean userLeaving, int configChanges, boolean dontReport) {
        ActivityClientRecord r = mActivities.get(token);
        if (r != null) {
            ...
            //Slog.v(TAG, "userLeaving=" + userLeaving + " handling pause of " + r);
            if (userLeaving) {
                performUserLeavingActivity(r);
            }

            r.activity.mConfigChangeFlags |= configChanges;
            performPauseActivity(token, finished, r.isPreHoneycomb());

            ...

            // Tell the activity manager we have paused.
            if (!dontReport) {
                try {
                    ActivityManagerNative.getDefault().activityPaused(token);
                } catch (RemoteException ex) {
                }
            }
            ...
        }
    }
    
    final Bundle performPauseActivity(ActivityClientRecord r, boolean finished,
            boolean saveState) {
        ...
        try {
            // Next have the activity save its current state and managed dialogs...
            if (!r.activity.mFinished && saveState) {
                callCallActivityOnSaveInstanceState(r);
            }
            // Now we are idle.
            r.activity.mCalled = false;
            mInstrumentation.callActivityOnPause(r.activity);
            EventLog.writeEvent(LOG_ON_PAUSE_CALLED, UserHandle.myUserId(),
                    r.activity.getComponentName().getClassName());
            if (!r.activity.mCalled) {
                throw new SuperNotCalledException(
                    "Activity " + r.intent.getComponent().toShortString() +
                    " did not call through to super.onPause()");
            }

        } catch (SuperNotCalledException e) {
            throw e;

        } catch (Exception e) {
            if (!mInstrumentation.onException(r.activity, e)) {
                throw new RuntimeException(
                        "Unable to pause activity "
                        + r.intent.getComponent().toShortString()
                        + ": " + e.toString(), e);
            }
        }
        r.paused = true;

        ...

        return !r.activity.mFinished && saveState ? r.state : null;
    }
    
    public void callActivityOnPause(Activity activity) {
        activity.performPause();
    }
    
    final void performPause() {
        mDoReportFullyDrawn = false;
        mFragments.dispatchPause();
        ...
        mCalled = false;
        onPause(); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mResumed = false;
        ...
    }
    
    @Override
    public final void activityPaused(IBinder token) {
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPaused");
        final long origId = Binder.clearCallingIdentity();
        synchronized(this) {
            ActivityStack stack = ActivityRecord.getStackLocked(token);
            if (stack != null) {
                stack.activityPausedLocked(token, false);
            }
        }
        Binder.restoreCallingIdentity(origId);
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
    }
    
    final void activityPausedLocked(IBinder token, boolean timeout) {
        if (DEBUG_PAUSE) Slog.v(
            TAG, "Activity paused: token=" + token + ", timeout=" + timeout);

        final ActivityRecord r = isInStackLocked(token);
        if (r != null) {
            mHandler.removeMessages(PAUSE_TIMEOUT_MSG, r);
            if (mPausingActivity == r
                ...
                completePauseLocked(true);
            } else {
                ...

                EventLog.writeEvent(EventLogTags.AM_FAILED_TO_PAUSE,
                        r.userId, System.identityHashCode(r), r.shortComponentName,
                        mPausingActivity != null
                            ? mPausingActivity.shortComponentName : "(none)");
            }
        }
    }
    
    private void completePauseLocked(boolean resumeNext) {
        ActivityRecord prev = mPausingActivity;
        completePauseLocked(resumeNext, prev);
    }

    private void completePauseLocked(boolean resumeNext, ActivityRecord prev) {
        ...
        if (resumeNext) {
            final ActivityStack topStack;
            ...
                if (!mService.isSleepingOrShuttingDown()) {
                    mStackSupervisor.resumeTopActivitiesLocked(topStack, prev, null);
                } else {
                    mStackSupervisor.checkReadyForSleepLocked();
                    ActivityRecord top = topStack.topRunningActivityLocked(null);
                    if (top == null || (prev != null && top != prev)) {
                        // If there are no more activities available to run,
                        // do resume anyway to start something.  Also if the top
                        // activity on the stack is not the just paused activity,
                        // we need to go ahead and resume it to ensure we complete
                        // an in-flight app switch.
                        mStackSupervisor.resumeTopActivitiesLocked(topStack, null, null);
                    }
                }
            ...
        }

        ...

        // Notfiy when the task stack has changed
        mService.notifyTaskStackChangedLocked();
    }
    
    boolean resumeTopActivitiesLocked(ActivityStack targetStack, ActivityRecord target,
            Bundle targetOptions) {
        ...

        if (targetStack == null) {
            targetStack = getFocusedStack();
        }
        // Do targetStack first.
        boolean result = false;

        ...
        if (isFrontStack(targetStack)) {
            result = targetStack.resumeTopActivityLocked(target, targetOptions);
        }
        ...

        return result;
    }

    final boolean resumeTopActivityLocked(ActivityRecord prev, Bundle options) {
        ...
            result = resumeTopActivityInnerLocked(prev, options);
        ...
        return result;
    }

    final boolean resumeTopActivityInnerLocked(ActivityRecord prev, Bundle options) {
        ...
            // Whoops, need to restart this activity!
            if (!next.hasBeenLaunched) {
                next.hasBeenLaunched = true;
            } else {
                if (SHOW_APP_STARTING_PREVIEW) {
                    /* { CustomStartingWindow */
                    CustomStartingWindowData customStartingWindowData = null; 
                    if(mStackSupervisor.CUSTOM_STARTING_WINDOW &&
                            (Intent.ACTION_MAIN.equals(next.intent.getAction()) 
                            ||Intent.ACTION_DIAL.equals(next.intent.getAction())
                            ||Intent.ACTION_CALL_BUTTON.equals(next.intent.getAction()))) {
                        customStartingWindowData = new CustomStartingWindowData(next.realActivity, next.info, next.userId);
                    }
                    /* CustomStartingWindow } */
                    mWindowManager.setAppStartingWindow(
                            next.appToken, next.packageName, next.theme,
                            mService.compatibilityInfoForPackageLocked(
                                    next.info.applicationInfo),
                            next.nonLocalizedLabel,
                            next.labelRes, next.icon, next.logo, next.windowFlags,
                            null, true
                            /* { CustomStartingWindow */, customStartingWindowData/* CustomStartingWindow } */
                            /* { Dual-Screen */, next.getDisplayId()/* Dual-Screen } */
                            /* { Elastic Application Framework */, next.app != null ? next.app.userId : UserHandle.getUserId(next.appInfo.uid) /* Elastic Application Framework } */);
                }
                if (DEBUG_SWITCH) Slog.v(TAG, "Restarting: " + next);
            }
            if (DEBUG_STATES) Slog.d(TAG, "resumeTopActivityLocked: Restarting " + next);
            mStackSupervisor.startSpecificActivityLocked(next, true, true);
        ...

        return true;
    }
    
    void startSpecificActivityLocked(ActivityRecord r,
            boolean andResume, boolean checkConfig) {
        // Is this activity's application already running?
        ProcessRecord app = mService.getProcessRecordLocked(r.processName,
                r.info.applicationInfo.uid, true);

        r.task.stack.setLaunchTime(r, false);

        if (app != null && app.thread != null) {
            ...
            try {
                if ((r.info.flags&ActivityInfo.FLAG_MULTIPROCESS) == 0
                        || !"android".equals(r.info.packageName)) {
                    // Don't add this if it is a platform component that is marked
                    // to run in multiple processes, because this is actually
                    // part of the framework so doesn't make sense to track as a
                    // separate apk in the process.
                    app.addPackage(r.info.packageName, r.info.applicationInfo.versionCode,
                            mService.mProcessStats);
                }
                realStartActivityLocked(r, app, andResume, checkConfig);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception when starting activity "
                        + r.intent.getComponent().flattenToShortString(), e);
            }

            // If a dead object exception was thrown -- fall through to
            // restart the application.
        }

        r.task.stack.setLaunchTime(r, true);
        ...
        mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
                "activity", r.intent.getComponent(), false, false, true
                /* { Dual-Screen */, r.getDisplayId()/* Dual-Screen } */);

        ...

    }
    
   final ProcessRecord startProcessLocked(String processName, ApplicationInfo info,
            boolean knownToBeDead, int intentFlags, String hostingType, ComponentName hostingName,
            boolean allowWhileBooting, boolean isolated, int isolatedUid, boolean keepIfLarge,
            String abiOverride, String entryPoint, String[] entryPointArgs, Runnable crashHandler
            /* { Dual-Screen */, int displayId/* Dual-Screen } */) {
        long startTime = SystemClock.elapsedRealtime();
        ProcessRecord app;
        if (!isolated) {
            app = getProcessRecordLocked(processName, info.uid, keepIfLarge);
            checkTime(startTime, "startProcess: after getProcessRecord");
        } else {
            // If this is an isolated process, it can't re-use an existing process.
            app = null;
        }
        
        ...

        if (app == null) {
            checkTime(startTime, "startProcess: creating new process record");
            ...
            app = newProcessRecordLocked(info, processName, isolated, isolatedUid);
            if (app == null) {
                Slog.w(TAG, "Failed making new process record for "
                        + processName + "/" + info.uid + " isolated=" + isolated);
                return null;
            }
            app.crashHandler = crashHandler;

            ...

            mProcessNames.put(processName, app.uid, app);
            if (isolated) {
                mIsolatedProcesses.put(app.uid, app);
            }
            checkTime(startTime, "startProcess: done creating new process record");
        } else {
            // If this is a new package in the process, add the package to the list
            app.addPackage(info.packageName, info.versionCode, mProcessStats);
            checkTime(startTime, "startProcess: added package to existing proc");
        }

        ...

        checkTime(startTime, "startProcess: stepping in to startProcess");
        startProcessLocked(
                app, hostingType, hostingNameStr, abiOverride, entryPoint, entryPointArgs);
        checkTime(startTime, "startProcess: done starting proc!");
        return (app.pid != 0) ? app : null;
    }

    private final void startProcessLocked(ProcessRecord app, String hostingType,
            String hostingNameStr, String abiOverride, String entryPoint, String[] entryPointArgs) {
        ...

            Process.ProcessStartResult startResult = null;

            if(userid>0 && (bbcId>0 && userid == bbcId) && app.info.bbcseinfo!=null){
                startResult = Process.start(entryPoint,
                app.processName, uid, uid, gids, debugFlags, mountExternal,
                app.info.targetSdkVersion, aasaSeInfo != null ? new String(aasaSeInfo) : app.info.bbcseinfo,  // AASA changed orginal : only "app.info.seinfo"
                app.info.bbccategory, app.info.accessInfo, requiredAbi, instructionSet,
                app.info.dataDir, entryPointArgs);
            }else {
                startResult = Process.start(entryPoint,
                app.processName, uid, uid, gids, debugFlags, mountExternal,
                app.info.targetSdkVersion, aasaSeInfo != null ? new String(aasaSeInfo) : app.info.seinfo,  // AASA changed orginal : only "app.info.seinfo"
                app.info.category, app.info.accessInfo, requiredAbi, instructionSet,
                app.info.dataDir, entryPointArgs);
            ...

            ...

            if (app.persistent) {
                Watchdog.getInstance().processStarted(app.processName, startResult.pid);
            }

            ...
            checkTime(startTime, "startProcess: starting to update pids map");
            synchronized (mPidsSelfLocked) {
                this.mPidsSelfLocked.put(startResult.pid, app);
                if (isActivityProcess) {
                    Message msg = mHandler.obtainMessage(PROC_START_TIMEOUT_MSG);
                    msg.obj = app;
                    mHandler.sendMessageDelayed(msg, startResult.usingWrapper
                            ? PROC_START_TIMEOUT_WITH_WRAPPER : PROC_START_TIMEOUT);
                }
            }
            checkTime(startTime, "startProcess: done updating pids map");
        ...
    }
    
    public static final ProcessStartResult start(final String processClass,
                                  final String niceName,
                                  int uid, int gid, int[] gids,
                                  int debugFlags, int mountExternal,
                                  int targetSdkVersion,
                                  String seInfo,
                                  int category,
                                  int accessInfo,
                                  String abi,
                                  String instructionSet,
                                  String appDataDir,
                                  String[] zygoteArgs) {
        try {
            return startViaZygote(processClass, niceName, uid, gid, gids,
                    debugFlags, mountExternal, targetSdkVersion, seInfo,
                    category, accessInfo, abi, instructionSet, appDataDir, zygoteArgs);
        } catch (ZygoteStartFailedEx ex) {
            Log.e(LOG_TAG,
                    "Starting VM process through Zygote failed");
            throw new RuntimeException(
                    "Starting VM process through Zygote failed", ex);
        }
    }
    
    private static ProcessStartResult startViaZygote(final String processClass,
                                  final String niceName,
                                  final int uid, final int gid,
                                  final int[] gids,
                                  int debugFlags, int mountExternal,
                                  int targetSdkVersion,
                                  String seInfo,
                                  int category,
                                  int accessInfo,                                  
                                  String abi,
                                  String instructionSet,
                                  String appDataDir,
                                  String[] extraArgs)
                                  throws ZygoteStartFailedEx {
        synchronized(Process.class) {
            ...

            return zygoteSendArgsAndGetResult(openZygoteSocketIfNeeded(abi), argsForZygote);
        }
    }
    
    private static ZygoteState openZygoteSocketIfNeeded(String abi) throws ZygoteStartFailedEx {
        if (primaryZygoteState == null || primaryZygoteState.isClosed()) {
            try {
                primaryZygoteState = ZygoteState.connect(ZYGOTE_SOCKET);
            } catch (IOException ioe) {
                throw new ZygoteStartFailedEx("Error connecting to primary zygote", ioe);
            }
        }

        if (primaryZygoteState.matches(abi)) {
            return primaryZygoteState;
        }

        ...
    }
    
    private static ProcessStartResult zygoteSendArgsAndGetResult(
            ZygoteState zygoteState, ArrayList<String> args)
            throws ZygoteStartFailedEx {
        try {
            /**
             * See com.android.internal.os.ZygoteInit.readArgumentList()
             * Presently the wire format to the zygote process is:
             * a) a count of arguments (argc, in essence)
             * b) a number of newline-separated argument strings equal to count
             *
             * After the zygote process reads these it will write the pid of
             * the child or -1 on failure, followed by boolean to
             * indicate whether a wrapper process was used.
             */
            final BufferedWriter writer = zygoteState.writer;
            final DataInputStream inputStream = zygoteState.inputStream;

            writer.write(Integer.toString(args.size()));
            writer.newLine();

            int sz = args.size();
            for (int i = 0; i < sz; i++) {
                String arg = args.get(i);
                if (arg.indexOf('\n') >= 0) {
                    throw new ZygoteStartFailedEx(
                            "embedded newlines not allowed");
                }
                writer.write(arg);
                writer.newLine();
            }

            writer.flush();

            // Should there be a timeout on this?
            ProcessStartResult result = new ProcessStartResult();
            result.pid = inputStream.readInt();
            if (result.pid < 0) {
                throw new ZygoteStartFailedEx("fork() failed");
            }
            result.usingWrapper = inputStream.readBoolean();
            return result;
        } catch (IOException ex) {
            zygoteState.close();
            throw new ZygoteStartFailedEx(ex);
        }
    }
    
    //ActivityThread.java
    public static void main(String[] args) {
        ...

        Looper.prepareMainLooper();

        ActivityThread thread = new ActivityThread();
        thread.attach(false);

        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }

        ...

        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
    
    final ApplicationThread mAppThread = new ApplicationThread();
    
    private void attach(boolean system) {
        sCurrentActivityThread = this;
        mSystemThread = system;
        if (!system) {
            ...
            RuntimeInit.setApplicationObject(mAppThread.asBinder());
            final IActivityManager mgr = ActivityManagerNative.getDefault();
            try {
                mgr.attachApplication(mAppThread);
            } catch (RemoteException ex) {
                // Ignore
            }
            ...
        } else {
            ...
        }

        ...
    }
    
    @Override
    public final void attachApplication(IApplicationThread thread) {
        synchronized (this) {
            Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "attachApplication");
            int callingPid = Binder.getCallingPid();
            final long origId = Binder.clearCallingIdentity();
            attachApplicationLocked(thread, callingPid);
            Binder.restoreCallingIdentity(origId);
            Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
        }
    }

    private final boolean attachApplicationLocked(IApplicationThread thread,
            int pid) {

        // Find the application record that is being attached...  either via
        // the pid if we are running in multiple processes, or just pull the
        // next app record if we are emulating process with anonymous threads.
        ProcessRecord app;
        if (pid != MY_PID && pid >= 0) {
            synchronized (mPidsSelfLocked) {
                app = mPidsSelfLocked.get(pid);
            }
        } else {
            app = null;
        }

        ...

        final String processName = app.processName;
        try {
            AppDeathRecipient adr = new AppDeathRecipient(
                    app, pid, thread);
            thread.asBinder().linkToDeath(adr, 0);
            app.deathRecipient = adr;
        } catch (RemoteException e) {
            app.resetPackageList(mProcessStats);
            startProcessLocked(app, "link fail", processName);
            return false;
        }

        EventLog.writeEvent(EventLogTags.AM_PROC_BOUND, app.userId, app.pid, app.processName);

        app.makeActive(thread, mProcessStats);
        app.curAdj = app.setAdj = -100;
        app.curSchedGroup = app.setSchedGroup = Process.THREAD_GROUP_DEFAULT;
        app.forcingToForeground = null;
        updateProcessForegroundLocked(app, false, false);
        app.hasShownUi = false;
        app.debugging = false;
        app.cached = false;
        app.killedByAm = false;
        app.startTime = SystemClock.uptimeMillis(); // Smart-DHA

        mHandler.removeMessages(PROC_START_TIMEOUT_MSG, app);

        ...
        try {
            ...

            ensurePackageDexOpt(app.instrumentationInfo != null
                    ? app.instrumentationInfo.packageName
                    : app.info.packageName);
            if (app.instrumentationClass != null) {
                ensurePackageDexOpt(app.instrumentationClass.getPackageName());
            }
            
            ...

           /* { SRUK DSS */
           // changed the parameter to boundConfig from mConfiguration
            thread.bindApplication(processName, appInfo, providers, app.instrumentationClass,
                    profilerInfo, app.instrumentationArguments, app.instrumentationWatcher,
                    app.instrumentationUiAutomationConnection, testMode, enableOpenGlTrace,
                    isRestrictedBackupMode || !normalMode, app.persistent,
                    /* { Dual-Screen */ configurations /* Dual-Screen } */,
                    app.compat, getCommonServicesLocked(app.isolated),
                    mCoreSettingsObserver.getCoreSettingsLocked()
                    /* { Dual-Screen */, app.displayId/* Dual-Screen } */);
           ...
           
        if (normalMode) {
            try {
                if (mStackSupervisor.attachApplicationLocked(app)) {
                    didSomething = true;
                }
            } catch (Exception e) {
                Slog.wtf(TAG, "Exception thrown launching activities in " + app, e);
                badApp = true;
            }
        }

        ...

        return true;
    }
    
    public final void bindApplication(String processName, ApplicationInfo appInfo,
                List<ProviderInfo> providers, ComponentName instrumentationName,
                ProfilerInfo profilerInfo, Bundle instrumentationArgs,
                IInstrumentationWatcher instrumentationWatcher,
                IUiAutomationConnection instrumentationUiConnection, int debugMode,
                boolean enableOpenGlTrace, boolean isRestrictedBackupMode, boolean persistent,
                /* { Dual-Screen */ UnRestrictedArrayList<Configuration> configs/* Dual-Screen } */, CompatibilityInfo compatInfo, Map<String, IBinder> services,
                Bundle coreSettings /* { Dual-Screen */ , int displayId /* Dual-Screen } */) {

            ...
            /* Dual-Screen } */
            sendMessage(H.BIND_APPLICATION, data);
        }
    
    // class H
    case BIND_APPLICATION:
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "bindApplication");
        AppBindData data = (AppBindData)msg.obj;
        handleBindApplication(data);
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
    break;
    
    
    private void handleBindApplication(AppBindData data) {
        ...

        final ContextImpl appContext = ContextImpl.createAppContext(this, data.info
                /* { Dual-Screen */,data.displayId/* Dual-Screen } */);

        ...

            try {
                java.lang.ClassLoader cl = instrContext.getClassLoader();
                mInstrumentation = (Instrumentation)
                    cl.loadClass(data.instrumentationName.getClassName()).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                    "Unable to instantiate instrumentation "
                    + data.instrumentationName + ": " + e.toString(), e);
            }

            mInstrumentation.init(this, instrContext, appContext,
                   new ComponentName(ii.packageName, ii.name), data.instrumentationWatcher,
                   data.instrumentationUiAutomationConnection);

            ...

        ...
            Application app = data.info.makeApplication(data.restrictedBackupMode, null);
            mInitialApplication = app;

            ...
            
            try {
                mInstrumentation.onCreate(data.instrumentationArgs);
            }
            ...

            try {
                mInstrumentation.callApplicationOnCreate(app);
            } 
            
        ...
    }
    
    public void callApplicationOnCreate(Application app) {
        app.onCreate(); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    
        
    boolean attachApplicationLocked(ProcessRecord app) throws RemoteException {
        final String processName = app.processName;
        boolean didSomething = false;
        for (int displayNdx = mActivityDisplays.size() - 1; displayNdx >= 0; --displayNdx) {
            ArrayList<ActivityStack> stacks = mActivityDisplays.valueAt(displayNdx).mStacks;
            for (int stackNdx = stacks.size() - 1; stackNdx >= 0; --stackNdx) {
                ...
                ActivityRecord hr = stack.topRunningActivityLocked(null);
                ...
                        try {
                            if (realStartActivityLocked(hr, app, true, true)) {
                                didSomething = true;
                            }
                            ...
                        }
                    }
                }
            }
        }
        ...
        return didSomething;
    }
    
    final boolean realStartActivityLocked(ActivityRecord r,
            ProcessRecord app, boolean andResume, boolean checkConfig)
            throws RemoteException {
            ...
            final ActivityStack stack = r.task.stack;
        try {
            ...

            app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
                    System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
                    r.compat, r.task.voiceInteractor, app.repProcState, r.icicle, r.persistentState,
                    results, newIntents, !andResume, mService.isNextTransitionForward(),
                    profilerInfo);
            ....
        } catch (RemoteException e) {
        }
        ...
    }
    
    public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident,
        ActivityInfo info, Configuration curConfig, CompatibilityInfo compatInfo,
        IVoiceInteractor voiceInteractor, int procState, Bundle state,
        PersistableBundle persistentState, List<ResultInfo> pendingResults,
        List<Intent> pendingNewIntents, boolean notResumed, boolean isForward,
        ProfilerInfo profilerInfo) {

        updateProcessState(procState, false);

        ActivityClientRecord r = new ActivityClientRecord();
        ...
        
        sendMessage(H.LAUNCH_ACTIVITY, r);
    }
    
    // class H
    case LAUNCH_ACTIVITY: {
        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
        final ActivityClientRecord r = (ActivityClientRecord) msg.obj;

        r.packageInfo = getPackageInfoNoCheck(
                r.activityInfo.applicationInfo, r.compatInfo);
        handleLaunchActivity(r, null);
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
    } break;
    
    private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ....
        Activity a = performLaunchActivity(r, customIntent);
        ....
        if (a != null) {
            handleResumeActivity(r.token, false, r.isForward,
                    !r.activity.mFinished && !r.startsNotResumed);

        }
    }
    
    private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ....
        Activity activity = null;
        try {
            java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
            
            activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent);
                    ...
           } catch (Exception e) {
        }

        try {
            Application app = r.packageInfo.makeApplication(false, mInstrumentation);

            if (activity != null) {
                ....
                if (r.isPersistable()) {
                    mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
                } else {
                    mInstrumentation.callActivityOnCreate(activity, r.state);
                }

            }
            r.paused = true;

            mActivities.put(r.token, r);

        } 
        return activity;
    }
    
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        prePerformCreate(activity);
        activity.performCreate(icicle);
        postPerformCreate(activity);
    }
    
    final void performCreate(Bundle icicle) {
        onCreate(icicle);
        mActivityTransitionState.readState(icicle);
        performCreateCommon();
    }

