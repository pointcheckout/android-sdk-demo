/************************************************************************
 * Copyright PointCheckout, Ltd.
 */
package com.pc.android.sdk;

import com.pc.android.sdk.internal.PointCheckoutInternalClient;

/**
 * The environment of {@link PointCheckoutInternalClient}
 *
 * @author pointcheckout
 */
public enum Environment {
    /**
     * Use for production
     */
    PRODUCTION("https://pay.pointcheckout.com"),
    /**
     * Use for testing
     */
    TEST("https://pay.test.pointcheckout.com");

    private Environment(String url) {
        this.url = url;
    }

    private String url;

    /**
     * @return the string index of the environment
     */
    public String getUrl(){
        return url;
    }

}
