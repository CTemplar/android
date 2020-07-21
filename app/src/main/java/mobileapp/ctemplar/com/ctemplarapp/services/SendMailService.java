package mobileapp.ctemplar.com.ctemplarapp.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.SendMessageRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessageAttachment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.EncryptionMessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageAttachmentProvider;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.SendMessageRequestProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.LaunchUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import timber.log.Timber;

public class SendMailService extends IntentService {
    private static final String TAG = "SendMailService";
    private static final String SEND_MAIL_ACTION = "com.ctemplar.service.mail.send";

    public SendMailService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (LaunchUtils.needForeground(intent)) {
            runAsForeground();
        }
    }

    private void runAsForeground() {

    }
}
