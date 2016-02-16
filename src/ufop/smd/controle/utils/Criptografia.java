/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ufop.smd.controle.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import android.util.Log;

/**
 *
 * @author Maycon Fernando Silva Brito
 * @email mayconfsbrito@gmail.com
 */
public class Criptografia {

    /**
     * Função para criar hash da senha informada
     * @param senha
     * @return - retorna a senha criptografada
     */
    public static String md5(String senha){
        String sen = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");

            BigInteger hash = new BigInteger(1, md.digest(senha.getBytes("UTF-8")));
            sen = hash.toString(16);

        } catch (Exception e) {
        	Log.d("ufop.smd", "Erro ao criptografar a senha!");
            e.printStackTrace();
        }
        return sen;
    }
    
    /**
     * Compara uma string normal com uma senha criptografada com MD5 e retorna se são iguais ou diferentes
     * @param string - string normal, ainda não criptograda
     * @param senha  - senha já criptografada
     */
    public static boolean verificaSenhasMD5(String string, String senha){
        
        senha = Criptografia.md5(string);
        string = Criptografia.md5(string);
        return senha.equals(string);
        
    }

}
