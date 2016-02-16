package ufop.smd.gui.activity;

import java.io.Serializable;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.sincronizacao.AsyncTaskSincronizacaoUsuarios;
import ufop.smd.controle.sincronizacao.TesteAsync;
import ufop.smd.controle.utils.Criptografia;
import ufop.smd.modelo.Agente;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Nesta activity o usuario realiza o login, alem disso, ele tambem tem através
 * do menu a opção de sincronizar os usuários do sistema com o servidor.
 * 
 * @author maycon
 * 
 *         Dúvidas: 1 - Não voltar a outras Activitys que não seja a de login
 *         caso o usuário faça logoff 2 - Dois componentes podem ter nomes
 *         iguais caso estejam em Layouts diferentes?
 * 
 */
public class ActivityLogin extends ActivityPai implements Serializable{

	
	private static final long serialVersionUID = 1L;
	// Elementos da gui
	private transient Button btOk;
	private transient Button btLimpar;
	private transient EditText etLogin;
	private transient EditText etSenha;
	private transient AsyncTaskSincronizacaoUsuarios asyncTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Executa os métodos básicos
		inicializaComponentes();

		if (savedInstanceState != null) {
			if(!asyncTask.isExecutando()){
				asyncTask = (AsyncTaskSincronizacaoUsuarios) savedInstanceState.getSerializable("1");
				asyncTask.setActivity(this);
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("1", asyncTask);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Cria o menu da activity
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Cria e adiciona novos menu itens
		menu.add(0, Constantes.MENU_SINCRONIZAR_USUARIOS, 0,
				"Sincronizar Usuários");
		menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configuração");

		return true;
	}

	/**
	 * Trata os eventos dos menuItens da Activity
	 * 
	 * @item - representa uma instância do MenuItem selecionado pelo usuário
	 */
	public boolean onMenuItemSelected(int featureItem, MenuItem item) {

		switch (item.getItemId()) {

		case Constantes.MENU_SINCRONIZAR_USUARIOS:
			executaAsyncTask();
			break;

		case Constantes.MENU_CONFIGURACAO:
			configuracao();
			break;

		}

		return true;
	}

	/**
	 * Implementa novamente este método para esta activity pois a mesma não
	 * realiza logoff
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public boolean inicializaComponentes() {

		btOk = (Button) findViewById(R.id.btOk);
		btLimpar = (Button) findViewById(R.id.btLimpar);
		etLogin = (EditText) findViewById(R.id.etLogin);
		etSenha = (EditText) findViewById(R.id.etSenha);
		asyncTask = new AsyncTaskSincronizacaoUsuarios(this, this, cursor);

		addListeners();

		return true;
	}

	@Override
	public boolean addListeners() {

		// Evento do botão limpar
		btLimpar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				limparGui();
			}
		});

		btOk.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				login();
			}
		});

		return false;
	}

	/**
	 * Realiza o login
	 */
	public boolean login() {

		// Caso os campos estejam preenchidos
		if (etLogin.getText().length() > 0 && etSenha.getText().length() > 0) {

			// Recupera os dados deste usuário no bd
			stopCursor(cursor);
			cursor = dao.consultar("agente", null, "login=?",
					new String[] { etLogin.getText().toString() }, null, null,
					null);

			// Verifica se existe algum usuário com este abreIFLogin no bd
			if (cursor != null && !cursor.isAfterLast()) {

				// Captura o usuário
				Agente agente = new Agente(cursor);
				cursor.close();

				// Verifica se a senha está correta
				if (agente.getSenha().equals(
						Criptografia.md5(new String(etSenha.getText()
								.toString())))) {

					// Conecta o usuário ao sistema
					if (Login.conectaUsuario(agente)) {

						// Realiza o login
						etLogin.setText("");
						etSenha.setText("");
						toast(getString(R.string.bemVindo));
						Intent it = new Intent(this, Opcoes.class);
						startActivityForResult(it, Constantes.TELA_OPCOES);

						return true;

					} else {
						toast(getString(R.string.toastErroLogin));
					}

				} else {
					toast(getString(R.string.toastSenhaInvalida));
					etSenha.setText("");

				}

			} else {
				toast(getString(R.string.toastUsuInexis));

			}

		} else {
			toast(getString(R.string.toastPreencheLogin));
		}

		return false;
	}

	public void executaAsyncTask() {
		if(!asyncTask.isExecutando()){
			try{
				asyncTask.execute();
			} catch(java.lang.IllegalStateException ex){
				asyncTask = new AsyncTaskSincronizacaoUsuarios(this, this, cursor);
				asyncTask.execute();
			}
		}
	}

	/**
	 * Limpa todos os elementos da gui
	 * 
	 * @return
	 */
	public boolean limparGui() {

		etLogin.setText("");
		etSenha.setText("");

		return true;
	}

}