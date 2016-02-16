package ufop.smd.persistencia;

import ufop.smd.controle.utils.Datas;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Agente;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe Responsável por criar o Banco de Dados Cria ou atualiza o Bd no
 * momento em que a aplicação é instalada
 * 
 * @author MAYCON
 */
public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, InterfaceScriptSQLite.NOME, null,
				InterfaceScriptSQLite.VERSAO);

	}

	/**
	 * Cria o banco de dados no momento em que a apk é instalada
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			
			Log.d("smd", "Criando banco de dados...");
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_AGENTE);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_BOLETIMPESQUISA);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_BOLETIMTRATAMENTO);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_MUNICIPIO);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_LOCALIDADE);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_LOGRADOURO);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_QUADRA);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_QUADRALOGRADOURO);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_IMOVEL);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_VISITAPESQUISA);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_VISITATRATAMENTO);
			db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_CONFIGURACAO);
			
			for(int index = 0; index < InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_INDEX.length; index++){
				db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_CREATE_INDEX[index]);
			}
			Log.d("smd", "Banco de dados criado com sucesso!");
			
			//Cria a tupla com propriedades padrões da tabela CONFIGURACAO
			ContentValues values = new ContentValues();
			values.put("id", 1);
			values.put("porta", 40500);
			values.put("ipServidor", "192.168.1.100");
			values.put("codigoMunicipio", "0000000");
			db.insert("configuracao", null, values);
			
			//Cria a tupla com o usuário administrador
			//Agente administrador = new Agente(0, "Administrador", true, "administrador", "admin", Datas.getDataHoje(), Horas.getHoraAgora());
			//db.insert("agente", null, administrador.getContentValues());
			
			
		} catch (Exception ex){
			Log.d("smd", "Erro ao criar o banco de dados!", ex.fillInStackTrace());
			System.exit(1);
		}
		
	}

	/**
	 * Atualiza o banco de dados para novas versões
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("smd", "Atualizando banco de dados de " + oldVersion + " para " + newVersion + "...");
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_AGENTE);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_BOLETIMPESQUISA);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_BOLETIMTRATAMENTO);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_MUNICIPIO);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_LOCALIDADE);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_LOGRADOURO);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_QUADRALOGRADOURO);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_QUADRA);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_IMOVEL);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_VISITAPESQUISA);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_VISITATRATAMENTO);
		db.execSQL(InterfaceScriptSQLite.SCRIPT_SQLITE_DROP_CONFIGURACAO);
		onCreate(db);
		Log.d("smd", "Banco de dados atualizado com sucesso!");

	}

}
