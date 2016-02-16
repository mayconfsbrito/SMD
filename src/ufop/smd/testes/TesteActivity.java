package ufop.smd.testes;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ufop.smd.R;
import ufop.smd.controle.utils.Datas;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import ufop.smd.modelo.Quadralogradouro;
import ufop.smd.modelo.Visitapesquisa;
import ufop.smd.persistencia.dao.AbstractDAO;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class TesteActivity extends Activity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teste);

		Log.d("smd", "Iniciou TesteActivity");

		executarTeste();

		Log.d("smd", "Encerrou TesteActivity");
	}


	public void executarTeste(){

		testeCadastro();
				

	}

	public void testeCRUD(){

		AbstractDAO dao = new AbstractDAO(this);
		Date data = Datas.getDataHoje();
		Time hora = Horas.getHoraAgora();




	}

	public void testeCadastro(){

		Log.d("smd", "Cadastrando objetos...");

		AbstractDAO dao = new AbstractDAO(this);

		Date data = Datas.getDataHoje();
		Time hora = Horas.getHoraAgora();

		Agente age = new Agente(1, "Maycon Brito", true, "maycon", "123", data, hora, 0, 0);
		dao.cadastrar("agente", age.getContentValues());

		Municipio mun = new Municipio("3100001", "Monlevade", "MG");
		dao.cadastrar("municipio", mun.getContentValues());

		Localidade loc = new Localidade(1, 1, "Loanda", false, data, hora);
		dao.cadastrar("localidade", loc.getContentValues());

		Logradouro log = new Logradouro(1, "Loanda");
		dao.cadastrar("logradouro", log.getContentValues());

		Quadra qua  = new Quadra(1, 1);
		dao.cadastrar("quadra", qua.getContentValues());
		
		Quadralogradouro ql = new Quadralogradouro(1, 1);
		dao.cadastrar("quadralogradouro", ql.getContentValues());

		Imovel imo = new Imovel(1, 1, "151");
		dao.cadastrar("imovel", imo.getContentValues());

		//Boletimpesquisa bolPes = new Boletimpesquisa(1, 1, 1, "cord", "3100001", "cat", "1234", data, "1/2012", "1/2012", 33, "tipo", true);
		//dao.cadastrar("boletimpesquisa", bolPes.getContentValues());

		//Boletimtratamento bolTra = new Boletimtratamento(1, 1, 1, "cord", "3100001", "cat", "1234", data, "1/2012", "1/2012", "trat", true);
		//dao.cadastrar("boletimtratamento", bolTra.getContentValues());

		//Visitapesquisa visP = new Visitapesquisa(1, 1, 1, 1, "1", 1, 1, 1, 1, 1, true, 1, true, hora);
		//visP.setIdVisitaPesquisa(10);
		//dao.cadastrar("visitapesquisa", visP.getContentValues());

		String str = "insert into visitapesquisa (idBoletimPesquisa, idQuadra, idLogradouro, idImovel, tipoUnidade, numeroTubitos, numeroAmostra, " +
				"larvasAeg, larvasAlb, larvasOut, examinadoLaboratorio, depositosInspecionados, ultimaVisitaQuadra, hora)" +
				"values (1, 1, 1, 1, '1', 1, 1, 1, 1, 1, 'true', 1, 'true', '00:00:00');";
		//dao.executarSQL(str);

		//Visitatratamento visT = new Visitatratamento(1, 1, 1, 1, "tipo", 1, 1, false, false, false, 3, false, hora);
		//dao.cadastrar("visitatratamento", visT.getContentValues());

	}

	public void testeAlteracao(){

		Log.d("smd", "Alterando objetos...");

		AbstractDAO dao = new AbstractDAO(this);
		Time hora = Horas.getHoraAgora();

	}

	public void testeExclusao(){

		Log.d("smd", "Excluindo objetos...");

		AbstractDAO dao = new AbstractDAO(this);
		dao.excluir("Agente", "idAgente=?", new String[] {2 +""});

	}

	public void testeListagem(){

		Log.d("smd", "Listando objetos...");

		List<String> listStrings = new ArrayList<String>();

		listStrings.add("agente");
		listStrings.add("boletimpesquisa");
		listStrings.add("boletimtratamento");
		listStrings.add("imovel");
		listStrings.add("localidade");
		listStrings.add("logradouro");
		listStrings.add("municipio");
		listStrings.add("permissao");
		listStrings.add("quadra");
		listStrings.add("quadralogradouro");
		listStrings.add("visitapesquisa");
		listStrings.add("visitatratamento");

		AbstractDAO dao = new AbstractDAO(this);

		while(!listStrings.isEmpty()){
			Cursor cursor = dao.listar(listStrings.get(0).toString(), "*", null);

			Log.d("smd", listStrings.get(0).toString());

			while(cursor != null && !cursor.isAfterLast()){

				Log.d("smd", cursor.getInt(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
				cursor.moveToNext();
			}
			cursor.close();
			listStrings.remove(0);
		}

	}

	public void testeConsulta(){

		Log.d("smd", "Consultando objetos...");

		AbstractDAO dao = new AbstractDAO(this);
		Cursor cursor = dao.consultar("Agente", null, null, null, null, null, null);

		while(cursor != null && !cursor.isAfterLast()){
			Log.d("smd", cursor.getInt(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
			cursor.moveToNext();
		}
		cursor.close();
	}

}
