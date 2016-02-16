package ufop.smd.gui.activity;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import ufop.smd.persistencia.dao.AbstractDAO;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activity SuperClasse
 * @author maycon
 *
 */
public abstract class ActivityPai extends Activity{

	AbstractDAO dao = new AbstractDAO(this);
	Cursor cursor = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * Inicializa os componentes da Gui
	 */
	public abstract boolean inicializaComponentes();

	/**
	 * Adiciona os listeners da activity
	 */
	public abstract boolean addListeners();

	/**
	 * Cria o menu padrão da maioria das activitys
	 */
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);

		menu.add(0, Constantes.MENU_LOGOFF, 0, "Logoff");
		menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configuração");

		return true;
	}

	/**
	 * Implementa as opções do menu
	 */
	public boolean onMenuItemSelected(int featureItem, MenuItem item){

		switch(item.getItemId()){

		//Evento do menuItem de Logoff
		case Constantes.MENU_LOGOFF:
			logoff();
			break;
			
		case Constantes.MENU_CONFIGURACAO:
			configuracao();
			break;

		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){

		switch (resultCode){
		case Constantes.LOGOFF:
			logoff();
			break;
			
		case Constantes.ERRO_IMAGEM:
			toast("Erro ao carregar a imagem!");
			break;
		}
	}

	/**
	 * Realiza o logoff do usuário
	 */
	protected void logoff(){

		Log.d("smd", "Logoff");

		Login.desconectaUsuario(this);
		setResult(Constantes.LOGOFF);
		finish();
	}
	
	/**
	 * Abre a activity de configuração da apk
	 */
	protected void configuracao(){
		
		Intent it = new Intent(this, Configuracao.class);
		startActivityForResult(it, Constantes.TELA_CONFIGURACAO);
	}

	/**
	 * Exibe o toast com a mensagem enviada por parâmetro
	 */
	public void toast(String str){
		Toast.makeText(this, str, Constantes.TOAST_DURATION).show();
	}

	/**
	 * Fecha o cursor da classe e o banco de dados
	 * @param cursor
	 */
	protected void stopCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			stopManagingCursor(cursor);
			cursor.close();
		}
	}

	/**
	 * Implementa OnPause
	 */
	@Override
	protected void onPause() {
		super.onPause();

		stopCursor(cursor);

		dao.close();

	}

	/**
	 * Busca no bd o município com código correspondente e insere o seu nome em @etNome além de
	 * liberar os componentes @views
	 * @return
	 */
	protected Municipio verificarMunicipio(EditText etCod, EditText etNome, View... views){

		//Captura o codigo do municipio na editText e busca no bd
		stopCursor(cursor);
		cursor = dao.consultar("municipio", null, "codigo=?", new String[]{etCod.getText().toString()}, null, null, null);

		//Verifica se foi encontrado algum municipio
		if(cursor != null && !cursor.isAfterLast()){

			//Instancia o municipio na memoria
			Municipio municipio = new Municipio(cursor);
			cursor.close();

			//Insere o nome do municipio na EditText
			etNome.setText(municipio.getNome());

			//Configura o comportamento da gui
			Componentes.enabledJComponent(views);

			return municipio;

		} else {
			toast("Código de município inexistente.");
			Componentes.disabledJComponent(views);
			return null;
		}
	}

	/**
	 * Busca no bd a localidade com código correspondente e insere o seu nome em @etNome além de
	 * liberar os componentes @views
	 * @return
	 */
	protected Localidade verificarLocalidade(Integer idMunicipio, EditText etCod, EditText etNome, View... views){

		//Captura o codigo do municipio na editText e busca no bd
		stopCursor(cursor);
		cursor = dao.consultar("localidade", null, "codigo=? AND idMunicipio=?", new String[]{etCod.getText().toString(), idMunicipio.toString()},
				null, null, null);

		//Verifica se foi encontrado algum objeto
		if(cursor != null && !cursor.isAfterLast()){

			//Instancia o objeto na memoria
			Localidade localidade = new Localidade(cursor);
			cursor.close();

			//Insere o nome na EditText
			etNome.setText(localidade.getNome());

			//Configura o comportamento da gui
			Componentes.enabledJComponent(views);

			return localidade;

		} else {
			toast("Código de localidade inexistente para este município.");
			Componentes.disabledJComponent(views);
			return null;
		}
	}

	/**
	 * Verifica se o componente @view foi preenchido, caso não tenha sido, retorna um toast com o @aviso
	 * @return
	 */
	protected boolean verificaPreenchido(String aviso, View... view){

		for(int index = 0; index < view.length; index++){

			if(view[index] instanceof EditText){
				EditText obj = (EditText) view[index];
				if(obj.getText().length() == 0){
					toast(aviso);
					return false;
				}
			
			} else if(view[index] instanceof Spinner){
				Spinner obj = (Spinner) view[index];
				if(obj.getSelectedItem().toString().length() == 0){
					toast(aviso);
					return false;
				}
			}
		}
		
		
		return true;
	}
}
