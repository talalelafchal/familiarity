package com.devniel.braph.listeners;

public interface HttpResponseListener {
	public void onResponse(String result, Integer status);
    public void onError(Exception error);
}
