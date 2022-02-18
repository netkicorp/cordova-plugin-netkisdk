/********* NetkiSDK.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <netkisdk_ios/netkisdk_ios.h>

static NSString *const kTokenApiParameter = @"tokenApi";
static NSString *const kCountryCodeParameter = @"countryCode";
static NSString *const kDocumentTypeParameter = @"documentType";
static NSString *const kDocTypeDriverLicense = @"driversLicense";
static NSString *const kDocTypePassport = @"passport";
static NSString *const kDocTypeGovernmentId = @"governmentID";
static NSString *const kDocTypeOther = @"other";

@interface NetkiSDK : CDVPlugin 

- (void)configureToken:(CDVInvokedUrlCommand *)command;

- (void)getCountryList:(CDVInvokedUrlCommand *)command;

- (void)setCountry:(CDVInvokedUrlCommand *)command;

- (void)getDocumentsType:(CDVInvokedUrlCommand *)command;

- (void)setDocumentType:(CDVInvokedUrlCommand *)command;

- (void)startIdentificationFlow:(CDVInvokedUrlCommand *)command;

- (void)validateInformation:(CDVInvokedUrlCommand *)command;

@end

@interface NetkiSDK() <NTKCameraFlowViewControllerDelegate>

@property (nonatomic, strong) NSString* callbackId;

@end

@implementation NetkiSDK

- (void)configureToken:(CDVInvokedUrlCommand *)command {
    
    NSDictionary *configureJson = [command.arguments objectAtIndex: 0];
    NSString *clientToken = [configureJson objectForKey:kTokenApiParameter];
    
    [NetkiClient.sharedClient configureWithClientToken:clientToken block:^(BOOL success, NSError * _Nullable error) {
        CDVPluginResult* pluginResult = nil;

        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];
        } else {
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            }
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getCountryList:(CDVInvokedUrlCommand *)command {
    
    NSArray *countries = NetkiClient.sharedClient.context.countries;
    
    NSMutableArray *jsonCountries = [NSMutableArray new];
    for (int i = 0; i < countries.count; i ++) {
        NTKCountry * country = [countries objectAtIndex:i];
        NSDictionary *json = @{
            @"alpha2Code": country.alpha2Code,
            @"alpha3Code": country.alpha3Code,
            @"name": country.name,
            @"banned": [NSString stringWithFormat:@"%s", country.banned ? "true" : "false"],
            @"countryCallingCode": country.countryCallingCode,
            @"flag": country.flag,
            @"has2dBarcode": [NSString stringWithFormat:@"%s", country.has2dBarcode ? "true" : "false"]
        };
        [jsonCountries addObject:json];
    }
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonCountries];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setCountry:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    NSDictionary *countryJson = [command.arguments objectAtIndex: 0];
    NSString *countryCode = [countryJson objectForKey:kCountryCodeParameter];
    
    NSArray *countries = NetkiClient.sharedClient.context.countries;

    NTKCountry * country = nil;
    for (int i = 0; i < countries.count; i++) {
        NTKCountry * searchCountry = [countries objectAtIndex:i];
        if ([searchCountry.alpha2Code.lowercaseString isEqualToString:countryCode.lowercaseString]) {
            country = searchCountry;
        }
    }
    
    if (country) {
        NetkiClient.sharedClient.issuingCountry = country;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Country not found"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
}

- (void)getDocumentsType:(CDVInvokedUrlCommand *)command {

    NSArray *documentTypes = @[kDocTypeDriverLicense, kDocTypePassport, kDocTypeGovernmentId, kDocTypeOther];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:documentTypes];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                    
}

- (void)setDocumentType:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;

    NSDictionary *documentTypeJson = [command.arguments objectAtIndex: 0];
    NSString *docType = [documentTypeJson objectForKey:kDocumentTypeParameter];
    if ([docType isEqualToString:kDocTypeDriverLicense]) {
        NetkiClient.sharedClient.docType = NTKDocTypeDriversLicense;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else if ([docType isEqualToString:kDocTypePassport]) {
        NetkiClient.sharedClient.docType = NTKDocTypePassport;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else if ([docType isEqualToString:kDocTypeGovernmentId]) {
        NetkiClient.sharedClient.docType = NTKDocTypeGovernmentID;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else if ([docType isEqualToString:kDocTypeOther]) {
        NetkiClient.sharedClient.docType = NTKDocTypeOther;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)startIdentificationFlow:(CDVInvokedUrlCommand *)command {
    NTKCameraFlowViewController *cameraFlowViewController = [[NTKCameraFlowViewController alloc] init];
    cameraFlowViewController.cameraFlowDelegate = self;
    self.viewController.modalPresentationStyle = UIModalPresentationFullScreen;
    self.callbackId = command.callbackId;
    [self.viewController presentViewController:cameraFlowViewController animated:YES completion:nil];
}

- (void)validateInformation:(CDVInvokedUrlCommand *)command {
    
    [NetkiClient.sharedClient validateAndCompleteWithBlock:^(BOOL success, NSString * _Nullable transactionId, NSError * _Nullable error) {
        CDVPluginResult* pluginResult = nil;
        
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:transactionId];
        } else {
            if (error) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: error.localizedDescription];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            }
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } onProgress:^(double progress) {
        //do nothing
    }];
}

#pragma mark - NTKCameraFlowViewControllerDelegate

- (void)cameraControllerDidCancelProcessing {
    //do nothing
}

- (void)cameraControllerDidDismiss {
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:self.callbackId];
    self.callbackId = nil;
}

@end
