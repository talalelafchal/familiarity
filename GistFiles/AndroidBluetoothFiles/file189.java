@Override
public void onRequestStartTransaction(String s) {
    //Comenzando transacción...
    Log.e(LOG_CAT, "onRequestStartTransaction(): " + s);
}

@Override
public void onRequestOnlineProcess() {
    //Realizando transacción...
    Log.e(LOG_CAT, "onRequestOnlineProcess(): ");
}

@Override
public void onRequestOnlineProcessResult(final String jsonResponse) {
    //Transacción exitosa...
    Log.e(LOG_CAT, "onRequestOnlineProcessResult(): " + jsonResponse);
}

@Override
public void OnErrorRequestProcessResult(NetPayError.Error error) {
    //Transacción NO exitosa...
    Log.e(LOG_CAT, "OnErrorRequestProcessResult(): " + error);
}
