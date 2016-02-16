package ufop.smd.controle;

import ufop.smd.modelo.Agente;
import android.content.Context;

public class Login {

	//Usuário logado no sistema
	private static Agente user = null;
	
	
    /**
     * Realiza os procedimentos de conexão de um usuário ao sistema
     *
     * @param user - Usuario a ser conectado
     * @return - Confirma que o usuário está conectado
     */
    public static boolean conectaUsuario(Agente user) {
        
        //Instancia o usuário
        Login.user = user;

        return true;
    }

    /**
     * Realiza os procedimentos de conexão de um usuário ao sistema
     *
     * @param user - Usuario a ser conectado
     * @return - Confirma que o usuário está desconectado
     */
    public static boolean desconectaUsuario(Context context) {

    	//Desconecta o usuário
    	Login.user = null;

        return true;
    }
    
    public static Agente getAgente(){
    	return Login.user;
    }
}
