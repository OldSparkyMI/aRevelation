# Releases
## 1.10
- Migration to AndroidX
- Fix: https://github.com/OldSparkyMI/aRevelation/issues/16 - an entry with empty name will be displayed, name not mandatory anymore
- Update: Gradle to 5.1.1
- Update: Lombok to 1.18.8

## 1.9
- Fix: android.content.res.Resources$NotFoundException for Nokia Nokia 8 (NB1), Android 8.1
- Minor bugfixes

## 1.8
- Fix: java.lang.IllegalStateException crashes for Huawei Y6II (HWCAM-H)
- Fix: java.lang.NullPointerException crashes for A1 PRO
- Minor bugfixes
- Gradle update
- Library updates

## 1.7.1
- Fix: java.lang.RuntimeException: like 1.7

## 1.7
- Fix: java.lang.RuntimeException: 
         at android.app.ActivityThread.performStopActivityInner (ActivityThread.java:4035)
         at android.app.ActivityThread.handleStopActivity (ActivityThread.java:4084)
         at android.app.ActivityThread.-wrap24 (Unknown Source)
         at android.app.ActivityThread$H.handleMessage (ActivityThread.java:1622)
         at android.os.Handler.dispatchMessage (Handler.java:106)
         at android.os.Looper.loop (Looper.java:164)
         at android.app.ActivityThread.main (ActivityThread.java:6494)
         at java.lang.reflect.Method.invoke (Native Method)
         at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run (RuntimeInit.java:438)
         at com.android.internal.os.ZygoteInit.main (ZygoteInit.java:807)
       Caused by: java.lang.IllegalStateException: 
         at android.app.FragmentManagerImpl.checkStateLoss (FragmentManager.java:1858)
         at android.app.FragmentManagerImpl.popBackStackImmediate (FragmentManager.java:811)
         at com.github.marmaladesky.ARevelation.clearUI (ARevelation.java:356)
         at com.github.marmaladesky.ARevelation.onStop (ARevelation.java:191)
         at android.app.Instrumentation.callActivityOnStop (Instrumentation.java:1375)
         at android.app.Activity.performStop (Activity.java:7181)
         at android.app.ActivityThread.performStopActivityInner (ActivityThread.java:4032)

## 1.6
- Fix: Open multiple files, multiple backs required to quit application
- Fix: https://github.com/OldSparkyMI/aRevelation/issues/14 - Toast tip only showed when application in foreground

## 1.5
- https://github.com/OldSparkyMI/aRevelation/issues/10 - Added entry deletion confirmation dialog

## 1.4
- Change: correct presentation of date (in aRevelation and in Revelation)
- Fix: update status will now be updated after changes are made
- Add: create a new file
- Add: dismiss changes
- Fix: wrong time after entry creation
- Fix: New File -> Add Something -> Delete Something -> Save File -> Open (empty) File --> ERROR
- Fix: minor bug fixes

## 1.3
- Add: Password generation
- Improvement: Settings description

## 1.2
- Change: Lint improvements 
- Change: Moved inner classes to normal classes (AskPasswordDialog)
- Add: partially folder support
  - ability add folder
  - ability add subentries for folders
  - ability add subfolders for folders
  - rename folder
- fix: the display of multiple encoding dialogs and other bugs related to this issue
- fix: https://github.com/OldSparkyMI/aRevelation/issues/7
- fix: several bug fixes
- Add: ability to delete entries

## 1.1.3
- Bugfix: https://github.com/OldSparkyMI/aRevelation/issues/6
- Change: using android.R.string.ok and android.R.string.cancel instead of own strings

## 1.1.2
- Bugfix: https://github.com/OldSparkyMI/aRevelation/issues/2

## 1.1.1
- Bugfix: Revelation can't read aRevelations files

## 1.1
- Ability to change the password for the file
- Backup-function before every save
  - with auto restore if something went wrong

## 1.0RC2
- Removed useless permission
  - android.permission.MANAGE_DOCUMENTS
  - android.permission.READ_PHONE_STATE
- Changed README.md
- new version set

## 1.0RC1 (Dezember 2017)
- Added 3 security settings
  - Hide / show secrets
  - Quick unlock password (if hide secrets are activated)
  - Lock file after moving password list activity into background with different timers
- Some bug fixes
- Some lint improvements