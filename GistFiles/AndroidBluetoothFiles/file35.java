@Override
public void onReadCardResult(String typeEvent) {
    //Leyendo tarjeta bancaria
}

@Override
public void onErrorReadCardResult(String typeEvent) {
    mNetPayBluetooth.resetTransaction();
}
