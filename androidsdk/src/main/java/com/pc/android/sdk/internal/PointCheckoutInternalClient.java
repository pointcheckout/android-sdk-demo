/************************************************************************
 * Copyright PointCheckout, Ltd.
 */
package com.pc.android.sdk.internal;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pc.android.sdk.Environment;
import com.pc.android.sdk.PointCheckoutEventListener;
import com.pc.android.sdk.PointCheckoutException;
import com.pc.android.sdk.R;

import java.util.Locale;

/**
 * @author pointcheckout
 */
public class PointCheckoutInternalClient {
    /**
     * Specifies whether the environment is test or production
     */
    private Environment environment;
    /**
     * Auto closes the modal on payment success or failure
     */
    private boolean autoDismiss;
    /**
     * The main modal that will show the payment page
     */
    private AlertDialog modal;
    /**
     * Indicates if whether the client is initialized or not
     */
    private boolean initialized;
    /**
     * Device language iso2
     */
    private String language;

    /**
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutInternalClient() throws PointCheckoutException {
        this(Environment.PRODUCTION, true);

    }

    /**
     * @param autoDismiss auto close the modal on payment success or failure
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutInternalClient(boolean autoDismiss) throws PointCheckoutException {
        this(Environment.PRODUCTION, autoDismiss);
    }

    /**
     * @param environment specifies whether the environment is test or production
     * @param autoDismiss auto close the modal on payment success or failure
     * @throws PointCheckoutException if the environment is null
     */
    public PointCheckoutInternalClient(Environment environment, boolean autoDismiss) throws PointCheckoutException {
        PointCheckoutUtils.assertNotNull(environment);
        this.environment = environment;
        this.autoDismiss = autoDismiss;

        setLanguage(Locale.getDefault().getLanguage());
    }

    public void initialize(Context context) {
        PointCheckoutUtils.evaluateSafetyNetAsync(context, new PointCheckoutSafetyNetListener() {
            @Override
            public void callback(boolean valid, String message) {
                initialized = valid;
            }
        });
    }

    /**
     * @param iso2 code of the language
     */
    public void setLanguage(String iso2) {
        language = iso2;
    }

    /**
     * @param checkoutKey of the payment
     * @return checkout url
     */
    private String getCheckoutUrl(String checkoutKey) {
        return String.format(getBaseUrl() + "/pay-mobile?checkoutKey=%s", checkoutKey);
    }

    private String getBaseUrl(){
        return String.format("%s/%s", environment.getUrl(), language);
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

        PointCheckoutUtils.assertNotNull(context);
        PointCheckoutUtils.assertNotNull(checkoutKey);

        if (initialized) {
            payUnsafe(context, checkoutKey, listener);
            return;
        }


        PointCheckoutUtils.evaluateSafetyNet(context, new PointCheckoutSafetyNetListener() {
            @Override
            public void callback(boolean valid, String message) {

                if (!valid) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.pointcheckout_error)
                            .setMessage(message)
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                initialized = valid;
                payUnsafe(context, checkoutKey, listener);

            }
        });
    }

    /**
     * @param context     of the activity to showing the modal
     * @param checkoutKey of the payment
     * @param listener    to be called when the modal gets dismissed
     */
    private void payUnsafe(
            final Context context,
            final String checkoutKey,
            final PointCheckoutEventListener listener) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(getCheckoutUrl(checkoutKey));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);

                if (!request.startsWith(environment.getUrl()) ||
                        request.startsWith(getBaseUrl() + "/complete")) {
                    try {
                        requestDismiss(listener);
                    } catch (PointCheckoutException e) {
                        e.printStackTrace();
                    }
                }

                if (request.startsWith(getBaseUrl() + "/cancel/")) {
                    try {
                        requestCancel(listener);
                    } catch (PointCheckoutException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
        });

        alert.setView(webView);
        alert.setNegativeButton(R.string.pointcheckout_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    requestCancel(listener);
                } catch (PointCheckoutException e) {
                    e.printStackTrace();
                }
            }
        });
        modal = alert.show();
    }

    /**
     * Dismiss the modal if autoDismiss is true
     *
     * @param listener to be called whether the modal is dismissed or not
     */
    private void requestDismiss(PointCheckoutEventListener listener) throws PointCheckoutException {
        if (autoDismiss)
            dismiss();

        if (listener != null)
            listener.onPaymentUpdate();


    }

    private void requestCancel(PointCheckoutEventListener listener) throws PointCheckoutException {
        if (autoDismiss)
            dismiss();

        if (listener != null)
            listener.onPaymentCancel();


    }

    /**
     * Dismisses the modal
     *
     * @throws PointCheckoutException if the modal is already dismissed
     */
    public void dismiss() throws PointCheckoutException {

        if (!modal.isShowing())
            throw new PointCheckoutException("Already dismissed");

        CookieManager.getInstance().removeAllCookie();
        modal.dismiss();
    }
}
