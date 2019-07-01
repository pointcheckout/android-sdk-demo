package com.pc.android.sdk;


import android.content.Context;

import com.pc.android.sdk.internal.PointCheckoutInternalClient;

/**
 * @author pointcheckout
 */
public class PointCheckoutClient {

    private PointCheckoutInternalClient client;

    /**
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutClient() throws PointCheckoutException {
        client = new PointCheckoutInternalClient();

    }

    /**
     * @param autoDismiss auto close the modal on payment success or failure
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutClient(boolean autoDismiss) throws PointCheckoutException {
        client = new PointCheckoutInternalClient(Environment.PRODUCTION, autoDismiss);
    }

    /**
     * @param environment specifies whether the environment is test or production
     * @param autoDismiss auto close the modal on payment success or failure
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutClient(Environment environment, boolean autoDismiss) throws PointCheckoutException {
        client = new PointCheckoutInternalClient(environment, autoDismiss);
    }

    public void initialize(Context context) {
        client.initialize(context);
    }

    /**
     * @param iso2 code of the language
     */
    public void setLanguage(String iso2) throws PointCheckoutException {
        client.setLanguage(iso2);
    }

    /**
     * @param context     of the activity to showing the modal
     * @param checkoutKey of the payment
     * @param listener    to be called when the modal gets dismissed
     * @throws PointCheckoutException if the context or checkoutKey is null
     */
    public void pay(
            final Context context,
            final String checkoutKey,
            final PointCheckoutEventListener listener) throws PointCheckoutException {

        client.pay(context, checkoutKey, listener);
    }

    /**
     * Dismisses the modal
     *
     * @throws PointCheckoutException if the modal is already dismissed
     */
    public void dismiss() throws PointCheckoutException {
        client.dismiss();
    }
}

