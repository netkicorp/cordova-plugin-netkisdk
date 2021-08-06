# Integration instructions

### Clone the plugin

The first step is to clone the repo containing the cordova plugin in the desired path 

```
git clone https://github.com/netkicorp/cordova-plugin-netkisdk.git
```

### Link the plugin 

Once you have the plugin in one specific path, go to the root folder of your cordova project and link it with the following command:

```
cordova plugin add path/cordova-plugin-netkisdk/ --link
```

### Android specific configuration

In your app/repositories.gradle add the following repos if missing
```
    mavenCentral()
    maven { url 'https://jitpack.io' }
    maven {
        url 'https://maven.google.com/'
        name 'Google'
    }
    maven {
        url "https://art.myverify.io//netki/libs-release-local/"
        credentials {
            username = "YOUR_USER"
            password = "YOUR_PASSWORD"
        }
    }
```
* Note: username and password are going to be provided by Netki after creating a user in our private artifactory, please contact us to create an account for you.

In your app/build.gradle, under the section android, defaultConfig add the following
```
android {
    defaultConfig {
        ...
        multiDexEnabled true
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
        renderscriptTargetApi 28
        renderscriptSupportModeEnabled true
    }
}
```

In your app/build.gradle, under the section for dependencies, add the netkiSDK android dependency and make sure you have multidex
```
implementation('com.netki:netkisdk:{NETKI_SDK_VERSION}')

compile "com.android.support:multidex:1.0.0"
```

Sync your gradle files and rebuild.

#### Troubleshooting Android

##### Android Theme

Depending the configuration of your project you can get an error regarding android:allowBackup and/or android:theme not correct

In that case add the following in your AndroidManifest.xml file
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.netkiapp">
    ...
    <application
        ...
        tools:replace="android:allowBackup,android:theme">
        ...
    </application>

</manifest>
```

##### Material Design

If you encounter this error:

```
Binary XML file line #37: Binary XML file line #37: Error inflating class
Make sure that your theme extends from Material Design theme.
```

For example:
```
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="@style/Theme.MaterialComponents.Light.DarkActionBar">
        <!-- Customize your theme here. -->
    </style>

</resources>
```

### Usage

After that you are ready to start using the plugin in your javascript files.

The available methods for the SDK are:

Configure the bussiness context with the token provided
```
netkiSDK.configureToken(
  token,
  successCallback,
  errorCallback
);
```

Retrieve list of all countries available
```
netkisdk.getCountryList(
  successCallback,
  errorCallback
);
```

// Set the country before starting the identificationFlow
```
netkiSDK.setCountry(
  twoLettercountryCode,
  successCallback,
  errorCallback
);
```

Retrieve all the documents type
```
netkisdk.getDocumentsType(
  successCallback,
  errorCallback
);
```

// Set the document type before starting the identificationFlow
```
netkiSDK.setDocumentType(
  documentType,
  successCallback,
  errorCallback
);
```

// Start the identificationFlow
```
netkiSDK.identificationFlow(
  successCallback,
  errorCallback
);
```

// Submit all the information
```
netkiSDK.validateInformation(
  successCallback,
  errorCallback
);
```
