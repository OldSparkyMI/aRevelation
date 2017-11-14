aRevelation
===========
An android password manager based on Revelation Password Manager file format. It stores all your accounts and passwords in a single, secure place, and gives you access to it through a user-friendly graphical interface on your smartphone.

* very simple gui, easy to use
* no master password
* stores your information in a secured single file
* categorize your passwords
* simple, secure and powerful

https://revelation.olasagasti.info/

## Build (Ubuntu)

####Install JDK
```
apt-get install openjdk-8-jdk
```

####Download and extract android SDK
Go to http://developer.android.com/sdk/index.html
You don't need to download ADT Bundle, just sdk. For example "android-sdk_r24.1.2-linux.tgz".
After that you need to extract file by using tar:
```
tar -xzvf android-sdk_r24.1.2-linux.tgz
```
It will extract content of the archive to current directory.


####Update your sdk
```
cd android-sdk-linux/tools
./android
```
Check "Android SDK Build-tools" (22.0.1), suggested packages and click "Install"

####Install git
```
apt-get install git
```

####Clone aRevelation project
```
git clone https://github.com/MarmaladeSky/aRevelation.git
```

####Install ia-32-libs or gcc-multilib (x86_64 OS)
```
sudo apt-get install ia-32-libs
```

####Go to project directory and run build
```
cd aRevelation
export ANDROID_HOME="path/to/android-sdk-linux"
./gradlew build
```
You can find .apk files in aRevelation/build/apk

## About
* Initial Android project from MarmaladeSky  
https://github.com/MarmaladeSky/aRevelation
* Icon set from Alexandru Stoica  
https://dribbble.com/shots/2888226-1800-Free-Minimal-Icon-Pack-20x20
* Original Revelation project  
https://revelation.olasagasti.info/
* Current developer  
https://github.com/OldSparkyMI