package ufop.smd.gui.activity;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Croquis;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity em que o usuário define a localidade do croqui a ser visualizado  
 * 
 * @author maycon
 *
 */
public class LocalidadeCroqui extends ActivityPai{

	//Variaveis dos componentes da Activity
	private Button btVis;
	private Button btLimpar;
	private Button btVerLoc;
	private EditText etCodLoc;
	private EditText etNomeLoc;
	private EditText etCodMun;
	private EditText etNomeMun;

	//Variaveis em memória da Activity
	private Municipio municipio = new Municipio();
	private Localidade localidade = new Localidade();

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localidade_croqui);

		inicializaComponentes();
	}

	@Override
	public boolean inicializaComponentes() {

		btVis = (Button) findViewById(R.id.btVisualizar);
		btLimpar = (Button) findViewById(R.id.btLimpar);
		btVerLoc = (Button) findViewById(R.id.btVerLoc);
		etCodLoc = (EditText) findViewById(R.id.etCodLoc);
		etNomeLoc = (EditText) findViewById(R.id.etNomeLoc);
		etCodMun = (EditText) findViewById(R.id.etCodMun);
		etNomeMun = (EditText) findViewById(R.id.etNomeMun);

		//Busca o município pelo código no bd (Salvo em Configurações)
		String codMun = null;
		stopCursor(cursor);
		cursor = dao.consultar("Configuracao", null, "id=1", null);
		if(cursor != null && !cursor.isAfterLast()){
			codMun = cursor.getString(3);
			
		} else {
			toast("Código de município não encontrado em Configuração.");
			finish();
		}
		
		if(codMun != null){
			stopCursor(cursor);
			cursor = dao.consultar("Municipio", null, "codigo=?", new String[]{codMun});
			if(cursor != null && !cursor.isAfterLast()){
				municipio = new Municipio(cursor);
				etCodMun.setText(municipio.getCodigo());
				etNomeMun.setText(municipio.getNome());
			
			} else {
				toast("Não foi encontrado nenhum município com este código no banco de dados.");
				finish();
			}
		}

		addListeners();

		return false;
	}

	@Override
	public boolean addListeners() {

		btLimpar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				limparGui();
			}
		});

		btVis.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				visualizarCroqui();
			}
		});

		btVerLoc.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				verificarLocalidade();

			}
		});

		return true;
	}

	/**
	 * Verifica se existe o croqui e abre-o
	 */
	private boolean visualizarCroqui(){

		//Cria o diretório caso ele não exista
		new Croquis(this).criarDiretorio();

		//Chama a Activity com a imagem do croqui
		Intent it = new Intent(this, Croqui.class);
		it.putExtra("idLocalidade", localidade.getIdLocalidade());
		startActivityForResult(it, Constantes.TELA_CROQUI);

		return false;
	}

	/**
	 * Busca no bd a localidade com o codigo e o município correspondente e insere o seu nome na EditText
	 *  
	 */
	private boolean verificarLocalidade(){

		//Captura o codigo e busca no bd pela localidade correspondente
		stopCursor(cursor);
		cursor = dao.consultar("localidade", null, "codigo=? AND idMunicipio=?", 
				new String[]{etCodLoc.getText().toString(), municipio.getIdMunicipio().toString()}, null, null, null);


		//Verifica se foi encontrado alguma localidade
		if(cursor != null && !cursor.isAfterLast()){

			//Instancia o municipio na memoria
			localidade = new Localidade(cursor);
			cursor.close();

			//Insere o nome da localidade na EditText
			etNomeLoc.setText(localidade.getNome());

			//Configura o comportamento da gui
			guiAtiva(true);

			//Veririca se a localidade não tem um croqui cadastrado

			if(!localidade.isCroqui()){
				btVis.setEnabled(false);
				toast("Esta localidade ainda não tem um croqui cadastrado.");
			}

			return true;

		} else {

			etCodLoc.setText("");
			etNomeLoc.setText("");
			localidade = new Localidade();
			Componentes.disabledJComponent(etNomeLoc, btVis);
			toast("Código de localidade inexistente para este município.");

			return false;
		}


	}

	/**
	 * Limpa os componentes da Gui
	 */
	private void limparGui(){

		etCodLoc.setText("");
		etNomeLoc.setText("");

		municipio = new Municipio();
		localidade = new Localidade();

		guiAtiva(false);

	}

	/**
	 * Habilita ou desabilita os componentes da gui de acordo com o estado de @ativa
	 * @param ativa
	 */
	private void guiAtiva(boolean ativa){

		if(ativa){
			Componentes.enabledJComponent(etCodLoc, etNomeLoc, btVis);
		} else {
			Componentes.disabledJComponent(etCodLoc, etNomeLoc, btVis);
		}

	}

}
