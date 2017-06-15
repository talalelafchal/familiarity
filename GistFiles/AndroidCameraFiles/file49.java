package app.vitorueda.com.urnaeletronica;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TecladoFragment extends Fragment{

    int posicao = 0;

    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;
    Button mButton6;
    Button mButton7;
    Button mButton8;
    Button mButton9;
    Button mButton0;
    Button mButtonBranco;
    Button mButtonCorrige;
    Button mButtonConfirma;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teclado, container, false);
        mButton1 = (Button) view.findViewById(R.id.button_1);
        mButton2 = (Button) view.findViewById(R.id.button_2);
        mButton3 = (Button) view.findViewById(R.id.button_3);
        mButton4 = (Button) view.findViewById(R.id.button_4);
        mButton5 = (Button) view.findViewById(R.id.button_5);
        mButton6 = (Button) view.findViewById(R.id.button_6);
        mButton7 = (Button) view.findViewById(R.id.button_7);
        mButton8 = (Button) view.findViewById(R.id.button_8);
        mButton9 = (Button) view.findViewById(R.id.button_9);
        mButton0 = (Button) view.findViewById(R.id.button_0);
        mButtonBranco = (Button) view.findViewById(R.id.button_branco);
        mButtonCorrige = (Button) view.findViewById(R.id.button_corrige);
        mButtonConfirma = (Button) view.findViewById(R.id.button_confirma);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setTextToX
            }
        });

        return view;
    }
}
