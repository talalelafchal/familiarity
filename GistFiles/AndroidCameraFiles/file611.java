package com.example.leonardo.zxing;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;

public class Decoder {
    /**
     * Tenta decodificar uma imagem em texto do código de barras
     * @param bitmap Imagem a ser decodificada
     * @return texto encontrado no código de barras
     * @throws FormatException O código foi lido, mas algum parâmetro não está nos conformes
     * @throws ChecksumException O código foi lido mas o checksum falhou (possível erro de decodificação na imagem)
     * @throws NotFoundException Código de barras não encontrado na imagem passada;
     */
    public static String decodeBarcode(Bitmap bitmap) throws FormatException, ChecksumException, NotFoundException {
        String text;

        //pegando parametros para RGBLuminanceSource
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);


        LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();

        Result result = reader.decode(binaryBitmap);
        text = result.getText(); //Se tudo deu certo, nessa linha text possui o resultado da leitura do código de barras da imagem;

        ///é possível extrair mais parâmetros do result, caso seja necessário
        byte[] rawBytes = result.getRawBytes();
        BarcodeFormat format = result.getBarcodeFormat();
        ResultPoint[] points = result.getResultPoints();
        ///////////////////////////////////////////////////////////////
        
        return text;
    }

}




