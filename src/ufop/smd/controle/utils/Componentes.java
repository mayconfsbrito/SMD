package ufop.smd.controle.utils;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 *      Classe que altera as propriedades de um determinado componente View
 *
 * @author Maycon Fernando Silva Brito
 * @email mayconfsbrito@gmail.com
 */
public class Componentes {

        public static void enabledJComponent(View... args) {

                for (int i = 0; i < args.length; i++) {
                        args[i].setEnabled(true);
                }
        }

        public static void disabledJComponent(View... args) {

                for (int i = 0; i < args.length; i++) {
                        args[i].setEnabled(false);
                }
        }

        public static void apagaEditText(EditText... args){

                for(int i = 0; i < args.length; i++)
                        args[i].setText("");

        }

        public static void apagaCheckBox(CheckBox... args){

                for(int i = 0; i < args.length; i++)
                        args[i].setSelected(false);

        }
        
        public static void apagaSpinner(Spinner... sp){
                
                for(int i = 0; i < sp.length; i++){
                        setSelectedItemOnSpinner(sp[i], "");
                }
                
        }

        public static boolean setSelectedItemOnSpinner(Spinner sp, String item){

                for (int i = 0; i < sp.getCount(); i++) {  
                        if (sp.getItemAtPosition(i).toString().equals(item)) {  
                                sp.setSelection(i);  
                                return true;
                        }  
                }

                return false;
        }
        
        public static boolean setSelectedItemOnSpinnerDebug(Spinner sp, String item){

        	Log.d("ufop.smd", "setSelectedItemOnSpinnerDebug");
        	Log.d("ufop.smd", "spinner.getCount()= " + sp.getCount() +".");
            for (int i = 0; i < sp.getCount(); i++) {
            	Log.d("smd", "spinner item=" + sp.getItemAtPosition(i).toString() +"." + " objeto item=" + item +".");
                    if (sp.getItemAtPosition(i).toString().equals(item)) {  
                            sp.setSelection(i);
                            Log.d("ufop.smd", "Escolheu - spinner item=" + sp.getItemAtPosition(i).toString() +"." + " objeto item=" + item +".");
                            return true;
                    }  
            }

            return false;
    }

}
