package com.example.tenarse.firebase;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tenarse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMsgService";
    private String token_personal;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message){
        super.onMessageReceived(message);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    System.out.println("FAILED");
                    System.out.println(task.getException());
                }
                token_personal = task.getResult();

                String[] partsTitulo = message.getNotification().getTitle().split(",", 2);

                if (token_personal.equals(partsTitulo[1])){
                    // Obtener los datos de la notificación
                    String title = partsTitulo[0];
                    String body = message.getNotification().getBody();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "My notification");
                    builder.setContentText(body);
                    builder.setContentTitle(title);
                    builder.setSmallIcon(R.drawable.logo_claro_small);
                    builder.setAutoCancel(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    managerCompat.notify(1, builder.build());
                }
            }
        });
    }

}