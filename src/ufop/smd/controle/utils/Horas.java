/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ufop.smd.controle.utils;

import java.sql.Time;
import java.text.ParseException;

/**
 *
 * @author Maycon Fernando Silva Brito @email mayconfsbrito@gmail.com
 */
public class Horas {

	/**
	 * Captura o tempo/hora de agora
	 *
	 * @return
	 */
	public static Time getHoraAgora() {

		return new Time(System.currentTimeMillis());
	}

	/**
	 * Converte uma determinada string em time
	 *
	 * @param str
	 * @return
	 */
	public static Time getTime(String str) {

		try{
			if (str != null && !str.equals("")) {
				return Time.valueOf(str);
			} else {
				return null;
			}
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}

	}
}
