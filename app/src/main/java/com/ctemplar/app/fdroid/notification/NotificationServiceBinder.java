package com.ctemplar.app.fdroid.notification;

import android.os.Binder;

import java.util.ArrayList;
import java.util.List;

public class NotificationServiceBinder extends Binder {
    final List<NotificationServiceListener> listenerList = new ArrayList<>(2);

    public void addListener(NotificationServiceListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(NotificationServiceListener listener) {
        listenerList.remove(listener);
    }
}

