package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.repository.dto.headers.IncomingHeadersDTO;
import timber.log.Timber;

public class IncomingHeadersMapper {
    private static final String ENVELOPE_TO = "Envelope-to";
    private static final String RECEIVED = "Received";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MIME_VERSION = "MIME-Version";
    private static final String SUBJECT = "Subject";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String DATE = "Date";
    private static final String MESSAGE_ID = "Message-ID";
    private static final String RECEIVED_SPF = "Received-SPF";
    private static final String X_SPAM_REPORT = "X-Spam-Report";
    private static final String DKIM_SIGNATURE = "DKIM-Signature";
    private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";

    @Nullable
    public static IncomingHeadersDTO mapToDTO(@Nullable String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException e) {
            Timber.e("Parse error: %s", value);
            return null;
        }
        Map<String, String> incomingHeaders = new HashMap<>();
        int length = jsonArray.length();
        for (int i = 0; i < length; ++i) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                Timber.e("Parse error: %s", value);
                return null;
            }
            Iterator<String> iterator = jsonObject.keys();;
            while (iterator.hasNext()) {
                String nextKey = iterator.next();
                try {
                    incomingHeaders.put(nextKey, jsonObject.getString(nextKey));
                } catch (JSONException e) {
                    Timber.e("Parse error: %s", value);
                }
            }
        }
        return new IncomingHeadersDTO(
                incomingHeaders,
                incomingHeaders.get(ENVELOPE_TO),
                incomingHeaders.get(RECEIVED),
                incomingHeaders.get(CONTENT_TYPE),
                incomingHeaders.get(MIME_VERSION),
                incomingHeaders.get(SUBJECT),
                incomingHeaders.get(FROM),
                incomingHeaders.get(TO),
                incomingHeaders.get(DATE),
                incomingHeaders.get(MESSAGE_ID),
                incomingHeaders.get(RECEIVED_SPF),
                incomingHeaders.get(X_SPAM_REPORT),
                incomingHeaders.get(DKIM_SIGNATURE),
                incomingHeaders.get(LIST_UNSUBSCRIBE)
        );
    }
}
