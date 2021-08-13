// cordova.define("cordova-plugin-netkisdk.netkisdk", function(require, exports, module) {
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

var argscheck = require('cordova/argscheck');
var channel = require('cordova/channel');
var exec = require('cordova/exec');
var cordova = require('cordova');

channel.createSticky('onCordovaInfoReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onCordovaInfoReady');


function NetkiSDK () {
    channel.onCordovaReady.subscribe(function () {
        channel.onCordovaInfoReady.fire();
    });
}

NetkiSDK.prototype.isAvailable = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "isAvailable", []);
}

NetkiSDK.prototype.configure = function(appToken, serviceCode, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'NetkiSDK', 'configure', [appToken, serviceCode]);
}

NetkiSDK.prototype.configureToken = function (tokenApi, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "configureToken", [{"tokenApi": tokenApi}]);
}

NetkiSDK.prototype.getCountryList = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "getCountryList", []);
}

NetkiSDK.prototype.setCountry = function (countryCode, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "setCountry", [{"countryCode": countryCode}]);
}

NetkiSDK.prototype.getDocumentsType = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "getDocumentsType", []);
}

NetkiSDK.prototype.setDocumentType = function (documentType, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "setDocumentType", [{"documentType": documentType}]);
}

NetkiSDK.prototype.startIdentificationFlow = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "startIdentificationFlow", []);
}

NetkiSDK.prototype.validateInformation = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "NetkiSDK", "validateInformation", []);
}

module.exports = new NetkiSDK();
// });
