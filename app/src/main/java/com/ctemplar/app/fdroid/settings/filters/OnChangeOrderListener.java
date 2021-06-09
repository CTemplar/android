package com.ctemplar.app.fdroid.settings.filters;

import com.ctemplar.app.fdroid.net.request.filters.EmailFilterOrderListRequest;

public interface OnChangeOrderListener {
    void onChange(EmailFilterOrderListRequest request);
}
