/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.netki;

import java.util.List;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.netki.netkisdk.v2.Netki;
import com.netki.netkisdk.v2.NetkiCallback;
import com.netki.netkisdk.v2.NetkiVerifyDataCallback;
import com.netki.netkisdk.v2.identification.IdentificationProcessActivity;
import com.netki.netkisdk.v2.model.Country;
import com.netki.netkisdk.v2.model.DocumentType;

import android.app.Activity;
import android.content.Intent;

public class NetkiSDK extends CordovaPlugin {
    private final String TAG = "NetkiSDK";
    private final int REQUEST_CODE = 87;
    private final Gson gson = new Gson();

    private final String IS_AVAILABLE_METHOD = "isAvailable";
    private final String CONFIGURE_TOKEN_METHOD = "configureToken";
    private final String GET_COUNTRY_LIST_METHOD = "getCountryList";
    private final String SET_COUNTRY_METHOD = "setCountry";
    private final String GET_DOCUMENTS_TYPE_METHOD = "getDocumentsType";
    private final String SET_DOCUMENT_TYPE_METHOD = "setDocumentType";
    private final String START_IDENTIFICATION_FLOW_METHOD = "startIdentificationFlow";
    private final String VALIDATE_INFORMATION_METHOD = "validateInformation";

    private final String TOKEN_API_PARAMETER = "tokenApi";
    private final String COUNTRY_CODE_PARAMETER = "countryCode";
    private final String DOCUMENT_TYPE_PARAMETER = "documentType";

    private CallbackContext callbackIdentificationFlow = null;

    /**
     * Constructor.
     */
    public NetkiSDK() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Netki.INSTANCE.initialize(this.cordova.getActivity().getApplicationContext());
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param arguments       JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray arguments, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case IS_AVAILABLE_METHOD:
                isAvailable(callbackContext);
                return true;
            case CONFIGURE_TOKEN_METHOD:
                configureToken(arguments, callbackContext);
                return true;
            case GET_COUNTRY_LIST_METHOD:
                getCountryList(callbackContext);
                return true;
            case SET_COUNTRY_METHOD:
                setCountry(arguments, callbackContext);
                return true;
            case GET_DOCUMENTS_TYPE_METHOD:
                getDocumentsType(callbackContext);
                return true;
            case SET_DOCUMENT_TYPE_METHOD:
                setDocumentType(arguments, callbackContext);
                return true;
            case START_IDENTIFICATION_FLOW_METHOD:
                startIdentificationFlow(callbackContext);
                return true;
            case VALIDATE_INFORMATION_METHOD:
                validateInformation(callbackContext);
                return true;
            default:
                return false;
        }
    }


    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    private void isAvailable(final CallbackContext callbackContext) {
        callbackContext.success("NetkiSdk ready to use");
    }

    private void configureToken(final JSONArray arguments, final CallbackContext callbackContext) throws JSONException {
        String tokenApi = arguments.getJSONObject(0).getString(TOKEN_API_PARAMETER);
        Netki.INSTANCE.configureWithClientToken(tokenApi, new NetkiCallback() {
            @Override
            public void onComplete() {
                callbackContext.success();
            }

            @Override
            public void onError(@NotNull Error error) {
                callbackContext.error(error.getMessage());
            }
        });
    }

    private void getCountryList(final CallbackContext callbackContext) {
        try {
            List<Country> countryList = Netki.INSTANCE.getBusinessContext().getCountryList();
            String countriesJson = gson.toJson(countryList);
            callbackContext.success(countriesJson);
        } catch (Exception exception) {
            callbackContext.error(exception.getMessage());
        }
    }

    private void setCountry(final JSONArray arguments, final CallbackContext callbackContext) throws JSONException {
        String countryCode = arguments.getJSONObject(0).getString(COUNTRY_CODE_PARAMETER);
        try {
            List<Country> countryList = Netki.INSTANCE.getBusinessContext().getCountryList();
            Country countrySelected = null;
            for (Country country : countryList) {
                if (country.getAlpha2().toLowerCase().equals(countryCode.toLowerCase())) {
                    countrySelected = country;
                    break;
                }
            }
            if (countrySelected != null) {
                Netki.INSTANCE.setIssuingCountry(countrySelected);
                callbackContext.success();
            } else {
                callbackContext.error("Country not found");
            }
        } catch (Exception exception) {
            callbackContext.error(exception.getMessage());
        }
    }

    private void getDocumentsType(final CallbackContext callbackContext) {
        try {
            DocumentType[] documentTypesList = DocumentType.values();
            String documentTypesJson = gson.toJson(documentTypesList);
            callbackContext.success(documentTypesJson);
        } catch (Exception exception) {
            callbackContext.error(exception.getMessage());
        }
    }

    private void setDocumentType(final JSONArray arguments, final CallbackContext callbackContext) throws JSONException {
        String documentType = arguments.getJSONObject(0).getString(DOCUMENT_TYPE_PARAMETER);
        try {
            DocumentType documentTypeEnum = Enum.valueOf(DocumentType.class, documentType);
            Netki.INSTANCE.setDocumentType(documentTypeEnum);
            callbackContext.success();
        } catch (Exception exception) {
            callbackContext.error(exception.getMessage());
        }
    }

    private void startIdentificationFlow(final CallbackContext callbackContext) {
        callbackIdentificationFlow = callbackContext;
        Intent intent = IdentificationProcessActivity.createIntent(this.cordova.getActivity());
        cordova.startActivityForResult(this, intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            PluginResult result;
            if (resultCode == Activity.RESULT_OK) {
                result = new PluginResult(PluginResult.Status.OK);
            } else {
                result = new PluginResult(PluginResult.Status.ERROR);
            }
            result.setKeepCallback(true);
            callbackIdentificationFlow.sendPluginResult(result);
        }
    }

    private void validateInformation(final CallbackContext callbackContext) {
        Netki.INSTANCE.validateAndComplete(new NetkiVerifyDataCallback() {
            @Override
            public void onProgress(double progress) {
                //nothing to do here
            }

            @Override
            public void onComplete(String transactionId) {
                callbackContext.success(transactionId);
            }

            @Override
            public void onError(Error error) {
                callbackContext.error(error.getMessage());
            }
        });
    }
}
