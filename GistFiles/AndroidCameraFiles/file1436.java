//...
do
{
    Bitmap bMap = getBitmapFromCamera();
    String text = null;
    try {
        text = decodeBarcode(Bitmap bitmap);
    } catch (NotFoundException e) { /*código não encontrado, pegar nova imagem*/ }
      catch (ChecksumException e) { /*imagem lida incorretamente, tentar de novo*/ }
      catch (FormatException e) { /*imagem lida, mas fora dos conformes para o tipo de código detectado*/ }
      
}while (text == null);



