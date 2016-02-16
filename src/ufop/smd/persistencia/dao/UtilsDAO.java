package ufop.smd.persistencia.dao;

import java.util.StringTokenizer;

import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import android.content.Context;
import android.database.Cursor;

/**
 * Classe que realiza tarefas de persistência integradas a gui
 * @author maycon
 *
 */
public class UtilsDAO {

	Context context = null;
	AbstractDAO dao = null;

	public UtilsDAO(Context context){
		this.context = context;
		dao = new AbstractDAO(context);
	}

	/**
	 * Busca um objeto no bd pelo id
	 */
	public Object getObject(Class<?> classe, Integer id, Integer cod){


		if(classe.equals(Municipio.class)){
			Cursor cursor = dao.consultar("Municipio", null, this.verificaSelecao(id, cod, "idMunicipio"), this.verificaSelecaoArgs(id, cod));
			Municipio mun = new Municipio(cursor);
			cursor.close();

			return mun;
		}

		if(classe.equals(Localidade.class)){
			Cursor cursor = dao.consultar("Localidade", null, this.verificaSelecao(id, cod, "idLocalidade"), this.verificaSelecaoArgs(id, cod));
			Localidade loc = new Localidade(cursor);
			cursor.close();

			return loc;

		}

		if(classe.equals(Agente.class)){
			Cursor cursor = dao.consultar("Agente", null, this.verificaSelecao(id, cod, "idAgente"), this.verificaSelecaoArgs(id, cod));
			Agente age = new Agente(cursor);
			cursor.close();

			return age;

		}
		
		if(classe.equals(Quadra.class)){
			Cursor cursor = dao.consultar("Quadra", null, this.verificaSelecao(id, cod, "idQuadra"), this.verificaSelecaoArgs(id, cod));
			Quadra log = new Quadra(cursor);
			cursor.close();

			return log;
		}

		if(classe.equals(Logradouro.class)){
			Cursor cursor = dao.consultar("Logradouro", null, this.verificaSelecao(id, cod, "idLogradouro"), this.verificaSelecaoArgs(id, cod));
			Logradouro log = new Logradouro(cursor);
			cursor.close();

			return log;
		}

		if(classe.equals(Imovel.class)){
			Cursor cursor = dao.consultar("Imovel", null, this.verificaSelecao(id, cod, "idImovel"), this.verificaSelecaoArgs(id, cod));
			Imovel imo = new Imovel(cursor);
			cursor.close();

			return imo;
		}

		if(classe.equals(Boletimtratamento.class)){
			Cursor cursor = dao.consultar("Boletimtratamento", null, this.verificaSelecao(id, cod, "idBoletimtratamento"), this.verificaSelecaoArgs(id, cod));
			Boletimtratamento bol = new Boletimtratamento(cursor);
			cursor.close();

			return bol;
		}
		
		if(classe.equals(Boletimpesquisa.class)){
			Cursor cursor = dao.consultar("Boletimpesquisa", null, this.verificaSelecao(id, cod, "idBoletimpesquisa"), this.verificaSelecaoArgs(id, cod));
			Boletimpesquisa bol = new Boletimpesquisa(cursor);
			cursor.close();

			return bol;
		}

		return null;
	}

	/**
	 * Busca no bd uma quadra
	 * @return
	 */
	public Quadra getQuadra(Integer idLocalidade, Integer cod){

		Cursor cursor = dao.consultar("Quadra", null, "idLocalidade=? AND codigo=?", new String[]{idLocalidade.toString(), cod.toString()});
		Quadra qua = new Quadra(cursor);
		cursor.close();

		return qua;
	}

	/**
	 * Busca no bd um Logradouro
	 */
	public Logradouro getLogradouro(Integer idMunicipio, String nome){

		Cursor cursor = dao.consultar("Logradouro", null, "idMunicipio=? AND nome=?", new String[]{idMunicipio.toString(), nome});
		Logradouro log = new Logradouro(cursor);
		cursor.close();

		return log;
	}

	/**
	 * Busca no bd um Imovel
	 */
	public Imovel getImovel(Integer idQuadra, Integer idLogradouro, String numeroComplemento){

		StringTokenizer tok = new StringTokenizer(numeroComplemento, " - ");
		String numero = tok.nextToken();
		String comp = tok.nextToken("").replaceAll(" - ", "");

		//Verifica se o complemento está vazio
		Cursor cursor = null;
		if(comp.equals("")){
			cursor = dao.consultar("Imovel", null, "idQuadra=? AND idLogradouro=? AND numero=?", 
					new String[]{idQuadra.toString(), idLogradouro.toString(), numero});
			
		} else {
			cursor = dao.consultar("Imovel", null, "idQuadra=? AND idLogradouro=? AND numero=? AND complemento=?", 
					new String[]{idQuadra.toString(), idLogradouro.toString(), numero, comp});
			
		}
		
		
		Imovel imo = new Imovel(cursor);
		cursor.close();

		return imo;

	}

	/**
	 *	Verifica entre o id e o codigo qual não é null 
	 */
	private String verificaSelecao(Integer id, Integer cod, String nomeId){

		String str = "";

		if(id != null && cod != null){

			str = nomeId +"=? AND codigo=?";

		} else if(cod == null && cod == null){

			str = nomeId +"=?";

		} else if(id == null && cod != null){

			str = "codigo=?";

		}

		return str;
	}
	private String[] verificaSelecaoArgs(Integer id, Integer cod){

		String[] str = null;

		if(id != null && cod != null){

			str = new String[2];
			str[0] = id.toString();
			str[1] = cod.toString();

		} else if(id != null && cod == null){

			str = new String[1];
			str[0] = id.toString();

		} else if(id == null && cod != null){

			str = new String[1];
			str[0] = cod.toString();

		}


		return str;
	}
}
