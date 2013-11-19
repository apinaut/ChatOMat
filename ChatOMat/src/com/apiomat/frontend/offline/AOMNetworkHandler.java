package com.apiomat.frontend.offline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashSet;
import java.util.Set;

/*
 * Copyright (c) 2011-2013, Apinauten GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY IT.
 */
public class AOMNetworkHandler extends BroadcastReceiver {

    private static AOMNetworkHandler myInstance = new AOMNetworkHandler();

    private Set<AOMNetworkListener> listeners = new HashSet<AOMNetworkListener>();
    private boolean isRegistered = false;


    static AOMNetworkHandler getInstance()
    {
        return  myInstance;
    }

    public void addListener(AOMNetworkListener _listener, Context _context)
    {
        this.listeners.add(_listener);
        registerForNetworkChange(_context);
    }

    private void registerForNetworkChange(Context _context) {
        /* let's try to register broadcast receiver for intent */
        if(this.isRegistered == false && _context != null)
        {
            _context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.isRegistered = true;
        }
    }

    public boolean isConnected(Context _context)
    {
        ConnectivityManager cmManager = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cmManager != null)
        {
            NetworkInfo activeNetwork = cmManager.getActiveNetworkInfo();
            return activeNetwork == null? false: activeNetwork.isConnected();
        }
        return true;
    }

    @Override
    public void onReceive(Context _context, Intent _intent) {
        for (AOMNetworkListener listener : listeners)
            listener.networkStateChanged(isConnected(_context));
    }

    protected static abstract interface AOMNetworkListener
    {
        public void networkStateChanged(final boolean _isConnected);
    }
}
