package forexconnect.common;/* Copyright 2019 FXCM Global Services, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use these files except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

public class LoginParams {
    public static final String LOGIN_NOT_SPECIFIED = "'Login' is not specified (/l|-l|/login|--login)";
    public static final String PASSWORD_NOT_SPECIFIED = "'Password' is not specified (/p|-p|/password|--password)";
    public static final String URL_NOT_SPECIFIED = "'URL' is not specified (/u|-u|/url|--url)";
    public static final String CONNECTION_NOT_SPECIFIED = "'Connection' is not specified (/c|-c|/connection|--connection)";

    // Getters

    public String getLogin() {
        return mLogin;
    }

    private String mLogin;

    public String getPassword() {
        return mPassword;
    }

    private String mPassword;

    public String getURL() {
        return mURL;
    }

    private String mURL;

    public String getConnection() {
        return mConnection;
    }

    private String mConnection;

    public String getSessionID() {
        return mSessionID;
    }
    private String mSessionID;

    public String getPin() {
        return mPin;
    }
    private String mPin;

    public LoginParams(String[] args) {
        // Get parameters with short keys
        mLogin = args[0];
        mPassword = args[1];
        mURL = args[2];
        mConnection = args[3];

        // Get optional parameters
        mSessionID = "";
        mPin = "";
    }

}
