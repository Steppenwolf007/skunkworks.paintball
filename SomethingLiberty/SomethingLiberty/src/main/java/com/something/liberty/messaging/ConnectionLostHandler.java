package com.something.liberty.messaging;

interface ConnectionLostHandler
{
    void onConnectionLost(Throwable throwable);
}
