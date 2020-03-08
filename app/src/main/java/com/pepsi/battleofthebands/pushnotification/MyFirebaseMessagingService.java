/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepsi.battleofthebands.pushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        try {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if message contains a data payload.
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                //            sendNotification(remoteMessage.getData().get("title"));
//                sendNotification(remoteMessage.getData().get("body"), remoteMessage.getNotification().getTitle());
                sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if message contains a notification payload.
//        try {
//            if (remoteMessage.getNotification() != null) {
//                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//                sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), "", "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

//        String title = data.getString("title");
//        String body = data.getString("body");
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        try {
//            JSONObject object = new JSONObject();
//            object.put("alert", messageBody);
//            object.put("title", title);
//            String alert = object.getString("alert");
//            ParseNotification notification = new Gson().fromJson(messageBody, ParseNotification.class);
//            ParseNotification notification = new ParseNotification();
//            notification.setMessage(messageBody);
            // Only launch main screen if the user is logged in
//            if (Prefs.getBoolean(PatariApplication.getAppContext(), Prefs.KEY_USER_LOGEDIN, false)) {
            // Dont add extra in intent incase, its a simple push notification
            Intent intent = new Intent(this, MainActivity.class);
//            if (notification.getItemType() != -1) {
//                Bundle bundle = new Bundle();
//                bundle.putString(ParseNotification.KEY_PUSH_DATA, object.toString());
//                intent.putExtra(ParseNotification.PARSE_PUSH_NOTIFIACTION, bundle);
//            }
//            } else {
//                intent = new Intent(this, SplashActivity.class);
//            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(messageBody);
            bigText.setBigContentTitle(title);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = null;
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setStyle(bigText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
