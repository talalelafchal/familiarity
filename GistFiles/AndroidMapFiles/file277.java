Intent intent = new Intent(Intent.ACTION_VIEW);
intent.setData(Uri.parse("geo:0,0?q=Rua Sem Saida, 100, Vila Pobre, City - State, Brasil"));
if(intent.resolveActivity(getPackageManager()) != null){
    startActivity(intent);
}