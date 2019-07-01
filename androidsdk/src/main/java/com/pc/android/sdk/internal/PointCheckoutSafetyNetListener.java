/************************************************************************
 * Copyright PointCheckout, Ltd.
 */
package com.pc.android.sdk.internal;

/**
 * @author pointcheckout
 */
public interface PointCheckoutSafetyNetListener {
    void callback(boolean valid, String message);
}
