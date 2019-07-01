/************************************************************************
 * Copyright PointCheckout, Ltd.
 */
package com.pc.android.sdk;

import android.util.AndroidException;

/**
 * @author pointcheckout
 */
public class PointCheckoutException extends AndroidException {

    public PointCheckoutException() { }

    public PointCheckoutException(String name) {
        super(name);
    }

    public PointCheckoutException(String name, Throwable cause) {
        super(name, cause);
    }
}
