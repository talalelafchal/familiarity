// change text every 7s, chạy tính từ mốc thời gian lúc đầu
@Override
protected void onResume() {
    super.onResume();
    for (int count = 0; count < arraylength; count++){
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                mytexts.setText(myarray[count]);
            }
        }, 7000 * (count + 1));
    }
}