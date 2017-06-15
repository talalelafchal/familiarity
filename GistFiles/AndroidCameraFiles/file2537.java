package com.android.server.pm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManagerInternal.PackagesProvider;
import android.content.pm.PackageManagerInternal.SyncAdapterPackagesProvider;
import android.content.pm.PackageParser.Package;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class DefaultPermissionGrantPolicy
{
  private static final String AUDIO_MIME_TYPE = "audio/mpeg";
  private static final Set<String> CALENDAR_PERMISSIONS;
  private static final Set<String> CAMERA_PERMISSIONS;
  private static final Set<String> CONTACTS_PERMISSIONS;
  private static final boolean DEBUG = false;
  private static final Set<String> LOCATION_PERMISSIONS;
  private static final Set<String> MICROPHONE_PERMISSIONS;
  private static final Set<String> PHONE_PERMISSIONS = new ArraySet();
  private static final Set<String> SENSORS_PERMISSIONS;
  private static final Set<String> SMS_PERMISSIONS;
  private static final Set<String> STORAGE_PERMISSIONS;
  private static final String TAG = "DefaultPermGrantPolicy";
  private PackageManagerInternal.PackagesProvider mDialerAppPackagesProvider;
  private PackageManagerInternal.PackagesProvider mImePackagesProvider;
  private PackageManagerInternal.PackagesProvider mLocationPackagesProvider;
  private final PackageManagerService mService;
  private PackageManagerInternal.PackagesProvider mSimCallManagerPackagesProvider;
  private PackageManagerInternal.PackagesProvider mSmsAppPackagesProvider;
  private PackageManagerInternal.SyncAdapterPackagesProvider mSyncAdapterPackagesProvider;
  private PackageManagerInternal.PackagesProvider mVoiceInteractionPackagesProvider;

  static
  {
    PHONE_PERMISSIONS.add("android.permission.READ_PHONE_STATE");
    PHONE_PERMISSIONS.add("android.permission.CALL_PHONE");
    PHONE_PERMISSIONS.add("android.permission.READ_CALL_LOG");
    PHONE_PERMISSIONS.add("android.permission.WRITE_CALL_LOG");
    PHONE_PERMISSIONS.add("com.android.voicemail.permission.ADD_VOICEMAIL");
    PHONE_PERMISSIONS.add("android.permission.USE_SIP");
    PHONE_PERMISSIONS.add("android.permission.PROCESS_OUTGOING_CALLS");
    CONTACTS_PERMISSIONS = new ArraySet();
    CONTACTS_PERMISSIONS.add("android.permission.READ_CONTACTS");
    CONTACTS_PERMISSIONS.add("android.permission.WRITE_CONTACTS");
    CONTACTS_PERMISSIONS.add("android.permission.GET_ACCOUNTS");
    LOCATION_PERMISSIONS = new ArraySet();
    LOCATION_PERMISSIONS.add("android.permission.ACCESS_FINE_LOCATION");
    LOCATION_PERMISSIONS.add("android.permission.ACCESS_COARSE_LOCATION");
    CALENDAR_PERMISSIONS = new ArraySet();
    CALENDAR_PERMISSIONS.add("android.permission.READ_CALENDAR");
    CALENDAR_PERMISSIONS.add("android.permission.WRITE_CALENDAR");
    SMS_PERMISSIONS = new ArraySet();
    SMS_PERMISSIONS.add("android.permission.SEND_SMS");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_SMS");
    SMS_PERMISSIONS.add("android.permission.READ_SMS");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_WAP_PUSH");
    SMS_PERMISSIONS.add("android.permission.RECEIVE_MMS");
    SMS_PERMISSIONS.add("android.permission.READ_CELL_BROADCASTS");
    MICROPHONE_PERMISSIONS = new ArraySet();
    MICROPHONE_PERMISSIONS.add("android.permission.RECORD_AUDIO");
    CAMERA_PERMISSIONS = new ArraySet();
    CAMERA_PERMISSIONS.add("android.permission.CAMERA");
    SENSORS_PERMISSIONS = new ArraySet();
    SENSORS_PERMISSIONS.add("android.permission.BODY_SENSORS");
    STORAGE_PERMISSIONS = new ArraySet();
    STORAGE_PERMISSIONS.add("android.permission.READ_EXTERNAL_STORAGE");
    STORAGE_PERMISSIONS.add("android.permission.WRITE_EXTERNAL_STORAGE");
  }

  public DefaultPermissionGrantPolicy(PackageManagerService paramPackageManagerService)
  {
    this.mService = paramPackageManagerService;
  }

  private static boolean doesPackageSupportRuntimePermissions(PackageParser.Package paramPackage)
  {
    return paramPackage.applicationInfo.targetSdkVersion > 22;
  }

  private PackageParser.Package getDefaultProviderAuthorityPackageLPr(String paramString, int paramInt)
  {
    paramString = this.mService.resolveContentProvider(paramString, 0, paramInt);
    if (paramString != null)
      return getSystemPackageLPr(paramString.packageName);
    return null;
  }

  private PackageParser.Package getDefaultSystemHandlerActivityPackageLPr(Intent paramIntent, int paramInt)
  {
    List localList = this.mService.mActivities.queryIntent(paramIntent, paramIntent.resolveType(this.mService.mContext.getContentResolver()), 512, paramInt);
    if (localList == null)
    {
      paramIntent = null;
      return paramIntent;
    }
    int i = localList.size();
    paramInt = 0;
    while (true)
    {
      if (paramInt >= i)
        break label92;
      PackageParser.Package localPackage = getSystemPackageLPr(((ResolveInfo)localList.get(paramInt)).activityInfo.packageName);
      paramIntent = localPackage;
      if (localPackage != null)
        break;
      paramInt += 1;
    }
    label92: return null;
  }

  private PackageParser.Package getDefaultSystemHandlerServicePackageLPr(Intent paramIntent, int paramInt)
  {
    List localList = this.mService.queryIntentServices(paramIntent, paramIntent.resolveType(this.mService.mContext.getContentResolver()), 512, paramInt);
    if (localList == null)
    {
      paramIntent = null;
      return paramIntent;
    }
    int i = localList.size();
    paramInt = 0;
    while (true)
    {
      if (paramInt >= i)
        break label89;
      PackageParser.Package localPackage = getSystemPackageLPr(((ResolveInfo)localList.get(paramInt)).serviceInfo.packageName);
      paramIntent = localPackage;
      if (localPackage != null)
        break;
      paramInt += 1;
    }
    label89: return null;
  }

  private List<PackageParser.Package> getHeadlessSyncAdapterPackagesLPr(String[] paramArrayOfString, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.LAUNCHER");
    int j = paramArrayOfString.length;
    int i = 0;
    if (i < j)
    {
      Object localObject = paramArrayOfString[i];
      localIntent.setPackage((String)localObject);
      if (!this.mService.mActivities.queryIntent(localIntent, localIntent.resolveType(this.mService.mContext.getContentResolver()), 512, paramInt).isEmpty());
      while (true)
      {
        i += 1;
        break;
        localObject = getSystemPackageLPr((String)localObject);
        if (localObject != null)
          localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }

  private PackageParser.Package getPackageLPr(String paramString)
  {
    return (PackageParser.Package)this.mService.mPackages.get(paramString);
  }

  private PackageParser.Package getSystemPackageLPr(String paramString)
  {
    paramString = getPackageLPr(paramString);
    if ((paramString != null) && (paramString.isSystemApp()))
    {
      if (!isSysComponentOrPersistentPlatformSignedPrivAppLPr(paramString))
        return paramString;
      return null;
    }
    return null;
  }

  private void grantDefaultPermissionsToDefaultSimCallManagerLPr(PackageParser.Package paramPackage, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to sim call manager for user:" + paramInt);
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, MICROPHONE_PERMISSIONS, paramInt);
    }
  }

  private void grantDefaultPermissionsToDefaultSystemDialerAppLPr(PackageParser.Package paramPackage, int paramInt)
  {
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, CONTACTS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, SMS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, MICROPHONE_PERMISSIONS, paramInt);
    }
  }

  private void grantDefaultPermissionsToDefaultSystemSmsAppLPr(PackageParser.Package paramPackage, int paramInt)
  {
    if (doesPackageSupportRuntimePermissions(paramPackage))
    {
      grantRuntimePermissionsLPw(paramPackage, PHONE_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, CONTACTS_PERMISSIONS, paramInt);
      grantRuntimePermissionsLPw(paramPackage, SMS_PERMISSIONS, paramInt);
    }
  }

  private void grantDefaultSystemHandlerPermissions(int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default platform handlers for user " + paramInt);
    Object localObject11;
    Object localObject7;
    Object localObject6;
    Object localObject8;
    Object localObject10;
    Object localObject9;
    synchronized (this.mService.mPackages)
    {
      localObject11 = this.mImePackagesProvider;
      localObject7 = this.mLocationPackagesProvider;
      localObject6 = this.mVoiceInteractionPackagesProvider;
      localObject8 = this.mSmsAppPackagesProvider;
      localObject10 = this.mDialerAppPackagesProvider;
      Object localObject1 = this.mSimCallManagerPackagesProvider;
      localObject9 = this.mSyncAdapterPackagesProvider;
      if (localObject11 != null)
      {
        ??? = ((PackageManagerInternal.PackagesProvider)localObject11).getPackages(paramInt);
        if (localObject6 == null)
          break label687;
        localObject6 = ((PackageManagerInternal.PackagesProvider)localObject6).getPackages(paramInt);
        label108: if (localObject7 == null)
          break label693;
        localObject7 = ((PackageManagerInternal.PackagesProvider)localObject7).getPackages(paramInt);
        label123: if (localObject8 == null)
          break label699;
        localObject8 = ((PackageManagerInternal.PackagesProvider)localObject8).getPackages(paramInt);
        label138: if (localObject10 == null)
          break label705;
        localObject10 = ((PackageManagerInternal.PackagesProvider)localObject10).getPackages(paramInt);
        label153: if (localObject1 == null)
          break label711;
        localObject11 = ((PackageManagerInternal.PackagesProvider)localObject1).getPackages(paramInt);
        label166: if (localObject9 == null)
          break label717;
        localObject1 = ((PackageManagerInternal.SyncAdapterPackagesProvider)localObject9).getPackages("com.android.contacts", paramInt);
        label183: if (localObject9 == null)
          break label722;
        localObject9 = ((PackageManagerInternal.SyncAdapterPackagesProvider)localObject9).getPackages("com.android.calendar", paramInt);
      }
    }
    while (true)
    {
      int i;
      synchronized (this.mService.mPackages)
      {
        Object localObject12 = getSystemPackageLPr(this.mService.mRequiredInstallerPackage);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        localObject12 = getSystemPackageLPr(this.mService.mRequiredVerifierPackage);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
        {
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, PHONE_PERMISSIONS, false, paramInt);
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, SMS_PERMISSIONS, false, paramInt);
        }
        localObject12 = new Intent("android.intent.action.MAIN");
        ((Intent)localObject12).addCategory("android.intent.category.SETUP_WIZARD");
        localObject12 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject12, paramInt);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
        {
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, PHONE_PERMISSIONS, paramInt);
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, CONTACTS_PERMISSIONS, paramInt);
        }
        localObject12 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.media.action.IMAGE_CAPTURE"), paramInt);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
        {
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, CAMERA_PERMISSIONS, paramInt);
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, MICROPHONE_PERMISSIONS, paramInt);
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, paramInt);
        }
        localObject12 = getDefaultProviderAuthorityPackageLPr("media", paramInt);
        if (localObject12 != null)
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        localObject12 = getDefaultProviderAuthorityPackageLPr("downloads", paramInt);
        if (localObject12 != null)
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        localObject12 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.intent.action.VIEW_DOWNLOADS"), paramInt);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        localObject12 = getDefaultProviderAuthorityPackageLPr("com.android.externalstorage.documents", paramInt);
        if (localObject12 != null)
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        localObject12 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.credentials.INSTALL"), paramInt);
        if ((localObject12 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject12)))
          grantRuntimePermissionsLPw((PackageParser.Package)localObject12, STORAGE_PERMISSIONS, true, paramInt);
        label687: label693: label699: label705: label711: label717: Object localObject3;
        if (localObject10 == null)
        {
          localObject10 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.intent.action.DIAL"), paramInt);
          if (localObject10 != null)
            grantDefaultPermissionsToDefaultSystemDialerAppLPr((PackageParser.Package)localObject10, paramInt);
          if (localObject11 == null)
            continue;
          j = localObject11.length;
          i = 0;
          if (i >= j)
            continue;
          localObject10 = getSystemPackageLPr(localObject11[i]);
          if (localObject10 != null)
            grantDefaultPermissionsToDefaultSimCallManagerLPr((PackageParser.Package)localObject10, paramInt);
          i += 1;
          continue;
          localObject2 = finally;
          throw localObject2;
          ??? = null;
          break;
          localObject6 = null;
          break label108;
          localObject7 = null;
          break label123;
          localObject8 = null;
          break label138;
          localObject10 = null;
          break label153;
          localObject11 = null;
          break label166;
          localObject3 = null;
          break label183;
          label722: localObject9 = null;
          continue;
        }
        int j = localObject10.length;
        i = 0;
        if (i >= j)
          continue;
        localObject12 = getSystemPackageLPr(localObject10[i]);
        if (localObject12 != null)
        {
          grantDefaultPermissionsToDefaultSystemDialerAppLPr((PackageParser.Package)localObject12, paramInt);
          break label1929;
          if (localObject8 == null)
          {
            localObject8 = new Intent("android.intent.action.MAIN");
            ((Intent)localObject8).addCategory("android.intent.category.APP_MESSAGING");
            localObject8 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject8, paramInt);
            if (localObject8 != null)
              grantDefaultPermissionsToDefaultSystemSmsAppLPr((PackageParser.Package)localObject8, paramInt);
            localObject8 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.provider.Telephony.SMS_CB_RECEIVED"), paramInt);
            if ((localObject8 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8)))
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, SMS_PERMISSIONS, paramInt);
            localObject8 = getDefaultSystemHandlerServicePackageLPr(new Intent("android.provider.Telephony.SMS_CARRIER_PROVISION"), paramInt);
            if ((localObject8 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8)))
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, SMS_PERMISSIONS, false, paramInt);
            localObject8 = new Intent("android.intent.action.MAIN");
            ((Intent)localObject8).addCategory("android.intent.category.APP_CALENDAR");
            localObject8 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject8, paramInt);
            if ((localObject8 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8)))
            {
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CALENDAR_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CONTACTS_PERMISSIONS, paramInt);
            }
            localObject8 = getDefaultProviderAuthorityPackageLPr("com.android.calendar", paramInt);
            if (localObject8 != null)
            {
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CONTACTS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CALENDAR_PERMISSIONS, true, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject8, STORAGE_PERMISSIONS, paramInt);
            }
            localObject8 = getHeadlessSyncAdapterPackagesLPr((String[])localObject9, paramInt);
            j = ((List)localObject8).size();
            i = 0;
            if (i < j)
            {
              localObject9 = (PackageParser.Package)((List)localObject8).get(i);
              if (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject9))
                break label1938;
              grantRuntimePermissionsLPw((PackageParser.Package)localObject9, CALENDAR_PERMISSIONS, paramInt);
              break label1938;
            }
          }
          else
          {
            j = localObject8.length;
            i = 0;
            if (i >= j)
              continue;
            localObject10 = getSystemPackageLPr(localObject8[i]);
            if (localObject10 == null)
              break label1947;
            grantDefaultPermissionsToDefaultSystemSmsAppLPr((PackageParser.Package)localObject10, paramInt);
            break label1947;
          }
          localObject8 = new Intent("android.intent.action.MAIN");
          ((Intent)localObject8).addCategory("android.intent.category.APP_CONTACTS");
          localObject8 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject8, paramInt);
          if ((localObject8 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8)))
          {
            grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CONTACTS_PERMISSIONS, paramInt);
            grantRuntimePermissionsLPw((PackageParser.Package)localObject8, PHONE_PERMISSIONS, paramInt);
          }
          localObject3 = getHeadlessSyncAdapterPackagesLPr((String[])localObject3, paramInt);
          j = ((List)localObject3).size();
          i = 0;
          if (i < j)
          {
            localObject8 = (PackageParser.Package)((List)localObject3).get(i);
            if (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8))
              break label1956;
            grantRuntimePermissionsLPw((PackageParser.Package)localObject8, CONTACTS_PERMISSIONS, paramInt);
            break label1956;
          }
          localObject3 = getDefaultProviderAuthorityPackageLPr("com.android.contacts", paramInt);
          if (localObject3 != null)
          {
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, true, paramInt);
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, PHONE_PERMISSIONS, true, paramInt);
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, STORAGE_PERMISSIONS, paramInt);
          }
          localObject3 = getDefaultSystemHandlerActivityPackageLPr(new Intent("android.app.action.PROVISION_MANAGED_DEVICE"), paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, paramInt);
          localObject3 = new Intent("android.intent.action.MAIN");
          ((Intent)localObject3).addCategory("android.intent.category.APP_MAPS");
          localObject3 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject3, paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, LOCATION_PERMISSIONS, paramInt);
          localObject3 = new Intent("android.intent.action.MAIN");
          ((Intent)localObject3).addCategory("android.intent.category.APP_GALLERY");
          localObject3 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject3, paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, STORAGE_PERMISSIONS, paramInt);
          localObject3 = new Intent("android.intent.action.MAIN");
          ((Intent)localObject3).addCategory("android.intent.category.APP_EMAIL");
          localObject3 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject3, paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, paramInt);
          localObject3 = null;
          localObject8 = this.mService.getDefaultBrowserPackageName(paramInt);
          if (localObject8 != null)
            localObject3 = getPackageLPr((String)localObject8);
          localObject8 = localObject3;
          if (localObject3 == null)
          {
            localObject3 = new Intent("android.intent.action.MAIN");
            ((Intent)localObject3).addCategory("android.intent.category.APP_BROWSER");
            localObject8 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject3, paramInt);
          }
          if ((localObject8 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject8)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject8, LOCATION_PERMISSIONS, paramInt);
          if (??? != null)
          {
            j = ???.length;
            i = 0;
            if (i < j)
            {
              localObject3 = getSystemPackageLPr(???[i]);
              if ((localObject3 == null) || (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
                break label1965;
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, paramInt);
              break label1965;
            }
          }
          if (localObject6 != null)
          {
            j = localObject6.length;
            i = 0;
            if (i < j)
            {
              localObject3 = getSystemPackageLPr(localObject6[i]);
              if ((localObject3 == null) || (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
                break label1974;
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CALENDAR_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, MICROPHONE_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, PHONE_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, SMS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, LOCATION_PERMISSIONS, paramInt);
              break label1974;
            }
          }
          localObject3 = new Intent("android.speech.RecognitionService");
          ((Intent)localObject3).addCategory("android.intent.category.DEFAULT");
          localObject3 = getDefaultSystemHandlerServicePackageLPr((Intent)localObject3, paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, MICROPHONE_PERMISSIONS, paramInt);
          if (localObject7 != null)
          {
            j = localObject7.length;
            i = 0;
            if (i < j)
            {
              localObject3 = getSystemPackageLPr(localObject7[i]);
              if ((localObject3 == null) || (!doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
                break label1983;
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CONTACTS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CALENDAR_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, MICROPHONE_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, PHONE_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, SMS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, LOCATION_PERMISSIONS, true, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, CAMERA_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, SENSORS_PERMISSIONS, paramInt);
              grantRuntimePermissionsLPw((PackageParser.Package)localObject3, STORAGE_PERMISSIONS, paramInt);
              break label1983;
            }
          }
          localObject3 = new Intent("android.intent.action.VIEW");
          ((Intent)localObject3).addCategory("android.intent.category.DEFAULT");
          ((Intent)localObject3).setDataAndType(Uri.fromFile(new File("foo.mp3")), "audio/mpeg");
          localObject3 = getDefaultSystemHandlerActivityPackageLPr((Intent)localObject3, paramInt);
          if ((localObject3 != null) && (doesPackageSupportRuntimePermissions((PackageParser.Package)localObject3)))
            grantRuntimePermissionsLPw((PackageParser.Package)localObject3, STORAGE_PERMISSIONS, paramInt);
          this.mService.mSettings.onDefaultRuntimePermissionsGrantedLPr(paramInt);
          return;
        }
      }
      label1929: i += 1;
      continue;
      label1938: i += 1;
      continue;
      label1947: i += 1;
      continue;
      label1956: i += 1;
      continue;
      label1965: i += 1;
      continue;
      label1974: i += 1;
      continue;
      label1983: i += 1;
    }
  }

  private void grantPermissionsToHtcPackages(int paramInt)
  {
    Log.d("DefaultPermGrantPolicy", "Granting permissions to HTC packages for user " + paramInt);
    TypedArray localTypedArray = null;
    BasePermission localBasePermission = null;
    Object localObject2 = localBasePermission;
    Object localObject1 = localTypedArray;
    while (true)
    {
      int i;
      int j;
      try
      {
        Resources localResources = this.mService.mContext.getResources();
        localObject2 = localBasePermission;
        localObject1 = localTypedArray;
        ArraySet localArraySet = new ArraySet();
        localObject2 = localBasePermission;
        localObject1 = localTypedArray;
        i = localResources.getIdentifier("htc_exception_list", "array", "com.htc.framework");
        if (i == 0)
        {
          localObject2 = localBasePermission;
          localObject1 = localTypedArray;
          Log.d("DefaultPermGrantPolicy", "grantPermissionsToHtcPackages: no htc_exception_list.");
          if (0 != 0)
            throw new NullPointerException();
          return;
        }
        localObject2 = localBasePermission;
        localObject1 = localTypedArray;
        localTypedArray = localResources.obtainTypedArray(i);
        i = 0;
        localObject2 = localTypedArray;
        localObject1 = localTypedArray;
        if (i < localTypedArray.length())
        {
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          j = localTypedArray.getResourceId(i, 0);
          if (j == 0)
            break label553;
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          String[] arrayOfString = localResources.getStringArray(j);
          if (arrayOfString == null)
            break label553;
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          if (arrayOfString.length <= 1)
            break label553;
          String str = arrayOfString[0];
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          PackageParser.Package localPackage = getPackageLPr(str);
          if (localPackage == null)
            break label553;
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          if (!doesPackageSupportRuntimePermissions(localPackage))
            break label553;
          localObject2 = localTypedArray;
          localObject1 = localTypedArray;
          if (!localPackage.isSystemApp())
          {
            localObject2 = localTypedArray;
            localObject1 = localTypedArray;
            if (!PackageManagerService.isPreloadCodePath(localPackage.codePath));
          }
          else
          {
            localObject2 = localTypedArray;
            localObject1 = localTypedArray;
            localArraySet.clear();
            j = 1;
            localObject2 = localTypedArray;
            localObject1 = localTypedArray;
            if (j < arrayOfString.length)
            {
              localObject2 = localTypedArray;
              localObject1 = localTypedArray;
              if (!localPackage.requestedPermissions.contains(arrayOfString[j]))
                break label562;
              localObject2 = localTypedArray;
              localObject1 = localTypedArray;
              localBasePermission = (BasePermission)this.mService.mSettings.mPermissions.get(arrayOfString[j]);
              if (localBasePermission == null)
                break label562;
              localObject2 = localTypedArray;
              localObject1 = localTypedArray;
              if (!localBasePermission.isRuntime())
                break label562;
              localObject2 = localTypedArray;
              localObject1 = localTypedArray;
              localArraySet.add(arrayOfString[j]);
              break label562;
            }
            localObject2 = localTypedArray;
            localObject1 = localTypedArray;
            Log.w("DefaultPermGrantPolicy", "Granting package " + str + " with permissions: " + Arrays.toString(localArraySet.toArray()));
            localObject2 = localTypedArray;
            localObject1 = localTypedArray;
            if (!localArraySet.isEmpty())
            {
              localObject2 = localTypedArray;
              localObject1 = localTypedArray;
              grantRuntimePermissionsLPw(localPackage, localArraySet, paramInt);
            }
          }
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        localObject1 = localObject2;
        Log.w("DefaultPermGrantPolicy", "grantPermissionsToHtcPackages: NotFoundException. e=" + localNotFoundException, localNotFoundException);
        return;
        if (localNotFoundException == null)
          continue;
        localNotFoundException.recycle();
        return;
      }
      finally
      {
        if (localObject1 != null)
          ((TypedArray)localObject1).recycle();
      }
      label553: i += 1;
      continue;
      label562: j += 1;
    }
  }

  private void grantPermissionsToSysComponentsAndPrivApps(int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to platform components for user " + paramInt);
    while (true)
    {
      int i;
      synchronized (this.mService.mPackages)
      {
        Iterator localIterator = this.mService.mPackages.values().iterator();
        if (localIterator.hasNext())
        {
          PackageParser.Package localPackage = (PackageParser.Package)localIterator.next();
          if ((!isSysComponentOrPersistentPlatformSignedPrivAppLPr(localPackage)) || (!doesPackageSupportRuntimePermissions(localPackage)) || (localPackage.requestedPermissions.isEmpty()))
            continue;
          ArraySet localArraySet = new ArraySet();
          int j = localPackage.requestedPermissions.size();
          i = 0;
          if (i < j)
          {
            String str = (String)localPackage.requestedPermissions.get(i);
            BasePermission localBasePermission = (BasePermission)this.mService.mSettings.mPermissions.get(str);
            if ((localBasePermission == null) || (!localBasePermission.isRuntime()))
              break label221;
            localArraySet.add(str);
            break label221;
          }
          if (localArraySet.isEmpty())
            continue;
          grantRuntimePermissionsLPw(localPackage, localArraySet, true, paramInt);
        }
      }
      return;
      label221: i += 1;
    }
  }

  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, int paramInt)
  {
    grantRuntimePermissionsLPw(paramPackage, paramSet, false, false, paramInt);
  }

  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, boolean paramBoolean, int paramInt)
  {
    grantRuntimePermissionsLPw(paramPackage, paramSet, paramBoolean, false, paramInt);
  }

  private void grantRuntimePermissionsLPw(PackageParser.Package paramPackage, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (paramPackage.requestedPermissions.isEmpty());
    Object localObject3;
    Object localObject4;
    PackageSetting localPackageSetting;
    do
    {
      return;
      localObject3 = paramPackage.requestedPermissions;
      localObject4 = null;
      localObject2 = localObject4;
      localObject1 = localObject3;
      if (!paramPackage.isUpdatedSystemApp())
        break;
      localPackageSetting = this.mService.mSettings.getDisabledSystemPkgLPr(paramPackage.packageName);
      localObject2 = localObject4;
      localObject1 = localObject3;
      if (localPackageSetting == null)
        break;
    }
    while (localPackageSetting.pkg.requestedPermissions.isEmpty());
    Object localObject2 = localObject4;
    Object localObject1 = localObject3;
    if (!((List)localObject3).equals(localPackageSetting.pkg.requestedPermissions))
    {
      localObject2 = new ArraySet((Collection)localObject3);
      localObject1 = localPackageSetting.pkg.requestedPermissions;
    }
    int k = ((List)localObject1).size();
    int i = 0;
    label137: if (i < k)
    {
      localObject3 = (String)((List)localObject1).get(i);
      if ((localObject2 == null) || (((Set)localObject2).contains(localObject3)))
        break label184;
    }
    while (true)
    {
      i += 1;
      break label137;
      break;
      label184: if (paramSet.contains(localObject3))
      {
        int j = this.mService.getPermissionFlags((String)localObject3, paramPackage.packageName, paramInt);
        if (((j == 0) || (paramBoolean2)) && ((j & 0x14) == 0))
        {
          this.mService.grantRuntimePermission(paramPackage.packageName, (String)localObject3, paramInt);
          j = 32;
          if (paramBoolean1)
            j = 0x20 | 0x10;
          this.mService.updatePermissionFlags((String)localObject3, paramPackage.packageName, j, j, paramInt);
        }
      }
    }
  }

  private boolean isSysComponentOrPersistentPlatformSignedPrivAppLPr(PackageParser.Package paramPackage)
  {
    boolean bool2 = true;
    boolean bool3 = false;
    if (UserHandle.getAppId(paramPackage.applicationInfo.uid) < 10000)
      bool1 = true;
    PackageSetting localPackageSetting;
    do
    {
      do
      {
        return bool1;
        bool1 = bool3;
      }
      while (!paramPackage.isPrivilegedApp());
      localPackageSetting = this.mService.mSettings.getDisabledSystemPkgLPr(paramPackage.packageName);
      if (localPackageSetting == null)
        break;
      bool1 = bool3;
    }
    while ((localPackageSetting.pkg.applicationInfo.flags & 0x8) == 0);
    if (PackageManagerService.compareSignatures(this.mService.mPlatformPackage.mSignatures, paramPackage.mSignatures) == 0);
    for (boolean bool1 = bool2; ; bool1 = false)
    {
      return bool1;
      if ((paramPackage.applicationInfo.flags & 0x8) != 0)
        break;
      return false;
    }
  }

  public void grantDefaultPermissions(int paramInt)
  {
    grantPermissionsToSysComponentsAndPrivApps(paramInt);
    grantDefaultSystemHandlerPermissions(paramInt);
    grantPermissionsToHtcPackages(paramInt);
  }

  public void grantDefaultPermissionsToDefaultBrowserLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default browser for user:" + paramInt);
    if (paramString == null);
    do
    {
      return;
      paramString = getSystemPackageLPr(paramString);
    }
    while ((paramString == null) || (!doesPackageSupportRuntimePermissions(paramString)));
    grantRuntimePermissionsLPw(paramString, LOCATION_PERMISSIONS, false, false, paramInt);
  }

  public void grantDefaultPermissionsToDefaultDialerAppLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default dialer app for user:" + paramInt);
    if (paramString == null);
    do
    {
      return;
      paramString = getPackageLPr(paramString);
    }
    while ((paramString == null) || (!doesPackageSupportRuntimePermissions(paramString)));
    grantRuntimePermissionsLPw(paramString, PHONE_PERMISSIONS, false, true, paramInt);
    grantRuntimePermissionsLPw(paramString, CONTACTS_PERMISSIONS, false, true, paramInt);
    grantRuntimePermissionsLPw(paramString, SMS_PERMISSIONS, false, true, paramInt);
    grantRuntimePermissionsLPw(paramString, MICROPHONE_PERMISSIONS, false, true, paramInt);
  }

  public void grantDefaultPermissionsToDefaultSimCallManagerLPr(String paramString, int paramInt)
  {
    if (paramString == null);
    do
    {
      return;
      paramString = getPackageLPr(paramString);
    }
    while (paramString == null);
    grantDefaultPermissionsToDefaultSimCallManagerLPr(paramString, paramInt);
  }

  public void grantDefaultPermissionsToDefaultSmsAppLPr(String paramString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to default sms app for user:" + paramInt);
    if (paramString == null);
    do
    {
      return;
      paramString = getPackageLPr(paramString);
    }
    while ((paramString == null) || (!doesPackageSupportRuntimePermissions(paramString)));
    grantRuntimePermissionsLPw(paramString, PHONE_PERMISSIONS, false, true, paramInt);
    grantRuntimePermissionsLPw(paramString, CONTACTS_PERMISSIONS, false, true, paramInt);
    grantRuntimePermissionsLPw(paramString, SMS_PERMISSIONS, false, true, paramInt);
  }

  public void grantDefaultPermissionsToEnabledCarrierAppsLPr(String[] paramArrayOfString, int paramInt)
  {
    Log.i("DefaultPermGrantPolicy", "Granting permissions to enabled carrier apps for user:" + paramInt);
    if (paramArrayOfString == null);
    while (true)
    {
      return;
      int j = paramArrayOfString.length;
      int i = 0;
      while (i < j)
      {
        PackageParser.Package localPackage = getSystemPackageLPr(paramArrayOfString[i]);
        if ((localPackage != null) && (doesPackageSupportRuntimePermissions(localPackage)))
        {
          grantRuntimePermissionsLPw(localPackage, PHONE_PERMISSIONS, paramInt);
          grantRuntimePermissionsLPw(localPackage, LOCATION_PERMISSIONS, paramInt);
          grantRuntimePermissionsLPw(localPackage, SMS_PERMISSIONS, paramInt);
        }
        i += 1;
      }
    }
  }

  public void setDialerAppPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mDialerAppPackagesProvider = paramPackagesProvider;
  }

  public void setImePackagesProviderLPr(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mImePackagesProvider = paramPackagesProvider;
  }

  public void setLocationPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mLocationPackagesProvider = paramPackagesProvider;
  }

  public void setSimCallManagerPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mSimCallManagerPackagesProvider = paramPackagesProvider;
  }

  public void setSmsAppPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mSmsAppPackagesProvider = paramPackagesProvider;
  }

  public void setSyncAdapterPackagesProviderLPw(PackageManagerInternal.SyncAdapterPackagesProvider paramSyncAdapterPackagesProvider)
  {
    this.mSyncAdapterPackagesProvider = paramSyncAdapterPackagesProvider;
  }

  public void setVoiceInteractionPackagesProviderLPw(PackageManagerInternal.PackagesProvider paramPackagesProvider)
  {
    this.mVoiceInteractionPackagesProvider = paramPackagesProvider;
  }
}

/* Location:           /home/dp/tmp/services-dex2jar.jar
 * Qualified Name:     com.android.server.pm.DefaultPermissionGrantPolicy
 * JD-Core Version:    0.6.2
 */