package mobileapp.ctemplar.com.ctemplarapp.billing.model;

import android.content.Context;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

public class PlanInfo {
    private final PlanType planType;
    private final String name;
    private final PlanData planData;

    public PlanInfo(PlanType planType, String jsonData) {
        this.planType = planType;
        this.name = planType.name();
        this.planData = getPlanDataByName(name, jsonData);
    }

    @Nullable
    private PlanData getPlanDataByName(String name, String jsonData) {
        try {
            Object jsonObject = new JSONObject(jsonData).get(name);
            return GENERAL_GSON.fromJson(jsonObject.toString(), PlanData.class);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Nullable
    public static String getJSON(Context context) {
        StringBuilder jsonStringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets()
                .open("plans.json")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }
        return jsonStringBuilder.toString();
    }

    public PlanType getPlanType() {
        return planType;
    }

    public String getName() {
        return name;
    }

    public PlanData getPlanData() {
        return planData;
    }
}
