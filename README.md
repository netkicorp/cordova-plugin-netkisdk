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

In your app/repositories.gradle add the following repos is missing
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
        renderscriptTargetApi 28
        renderscriptSupportModeEnabled true
    }
}
```

In your app/build.gradle, under the section for dependencies, add the netkiSDK android dependency
```
implementation('com.netki:netkisdk:{NETKI_SDK_VERSION}')
```

Sync your gradle files and rebuild.

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
