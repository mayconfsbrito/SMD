package ufop.smd.controle.utils;

import android.util.Log;

public class Excecao {

	public static void notificacao(String mensagem, Exception ex){
		if(ex != null){
			Log.d("smd", mensagem, ex.fillInStackTrace());
			System.exit(1);
		}
	}
}
