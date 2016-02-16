/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ufop.smd.controle.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Maycon Fernando Silva Brito @email mayconfsbrito@gmail.com
 */
public class Datas {

	/**
	 * Soma dias em uma data
	 *
	 * @param hoje - data a ser somada
	 * @param dias - dias a serem incrementados
	 * @return -
	 */
	public static Date addDias(Date hoje, int dias) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(hoje);
		calendar.add(Calendar.DATE, dias);

		java.util.Date dataUtil = calendar.getTime();
		java.sql.Date dataSql = new java.sql.Date(dataUtil.getTime());

		return dataSql;
	}

	/**
	 * Subtrai dias em uma data
	 *
	 * @param hoje - data a ser somada
	 * @param dias - dias a serem incrementados
	 * @return -
	 */
	public static Date subDias(Date hoje, int dias) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(hoje);
		calendar.add(Calendar.DATE, -dias);

		java.util.Date dataUtil = calendar.getTime();
		java.sql.Date dataSql = new java.sql.Date(dataUtil.getTime());

		return dataSql;
	}

	/**
	 * Subtrai uma determinada quantidade de anos a data
	 *
	 * @param dateStart
	 * @return
	 */
	public static Date subYears(Date dateStart, int years) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateStart);
		cal.add(Calendar.YEAR, -years);

		java.util.Date dataUtil = cal.getTime();
		java.sql.Date dataSql = new java.sql.Date(dataUtil.getTime());

		return dataSql;
	}

	/**
	 * Adiciona dois mil anos a uma determinada data
	 *
	 * @param dateStart
	 * @return
	 */
	public static Date addYears(Date dateStart, int years) {

		//SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateStart);
		cal.add(Calendar.YEAR, years);	//Adding 1 year to current date

		java.util.Date dataUtil = cal.getTime();
		java.sql.Date dataSql = new java.sql.Date(dataUtil.getTime());

		return dataSql;
	}

	/**
	 * Captura a data de hoje
	 *
	 * @return
	 */
	public static Date getDataHoje() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * Retorna uma determinada data a partir da string @data
	 *
	 * @param data
	 * @return
	 */
	public static Date getDate(String data) {
		return getDate(data, "yyyy-MM-dd");
	}

	public static Date getDate(String data, String formato) {
		try {

			DateFormat dateFormat = new SimpleDateFormat(formato);  
			if (data != null) {
				Date venc = dateFormat.parse(data);

				return venc;

			} else {
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	//Retorna formatada a data que está no BD
	public static String getText(Date data, String Formato) {

		if (data != null) {
			SimpleDateFormat df = new SimpleDateFormat(Formato);
			return df.format(data);
		} else {
			return null;
		}

	}
	
    //Retorna o ano da data atual
    public static Integer getYear(){
        return getYear(Datas.getDataHoje());
    }
    
    //Retorna o ano da data enviada
    public static Integer getYear(Date date){
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(simpleDateformat.format(date));
    }

	//Retorna formatada a data que estão no BD
	public static String toString(Date data) {

		if (data != null) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			return df.format(data);
		}
		return "";
	}

	/**
	 * Verifica se o número de dígitos do ano foram apenas 2 e passa para 2000 anos
	 */
	public static Date verificaDigitosAno(Date data) {

		//Verifica se o ano não tem quatro dígitos
		if (data.getTime() <= Datas.subYears(Datas.getDataHoje(), 1000).getTime()) {
			//Soma dois mil anos ao ano do vencimento
			data = Datas.addYears(data, 2000);
		}

		return data;
	}
}
