package ufop.smd.gui.activity;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Superclasse para activitys de visualização/cadastro/alteração de boletins
 * @author maycon
 *
 */
public abstract class ActivityPaiBoletim extends ActivityPai {

	//Elementos na memória da Activity
	Municipio municipio = null;
	Localidade localidade = null;

	//Componentes da Activity
	protected EditText etCoReg;
	protected EditText etGrsNome;
	protected EditText etMunCod;
	protected EditText etMunNome;
	protected EditText etLocCod;
	protected EditText etLocNome;
	protected EditText etAgeId;
	protected EditText etAgeNome;
	protected EditText etSemEpidNum;
	protected EditText etSemEpidAno;
	protected EditText etNAtivNum;
	protected EditText etNAtivAno;
	protected EditText etData;
	protected EditText etId;
	protected EditText etTurma;
	protected Button btVerMun;
	protected Button btVerLoc;
	protected Button btCadAlt;
	protected Button btLimpar;
	protected Spinner spCat;
	protected Spinner spTipAtv;

	/**
	 * Sempre implementar o método onCreate
	 */


	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);

		menu.removeItem(Constantes.MENU_LOGOFF);
		menu.removeItem(Constantes.MENU_CONFIGURACAO);
		menu.add(1, Constantes.MENU_VISITAS, 0, "Visitas");
		menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configuração");
		menu.add(1, Constantes.MENU_LOGOFF, 2, "Logoff");
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureItem, MenuItem item){

		switch(item.getItemId()){

		//Evento do menuItem de Logoff
		case Constantes.MENU_LOGOFF:
			logoff();
			break;

		case Constantes.MENU_VISITAS:
			visitas();
			break;
			
		case Constantes.MENU_CONFIGURACAO:
			configuracao();
			break;

		}

		return true;
	}

	@Override
	public boolean inicializaComponentes() {

		//Inicializa todos os componentes da activity
		etCoReg = (EditText) findViewById(R.id.etCoordReg);
		etGrsNome = (EditText) findViewById(R.id.etGrsNome);
		etMunCod = (EditText) findViewById(R.id.etMunCod);
		etMunNome = (EditText) findViewById(R.id.etMunNome);
		etLocCod = (EditText) findViewById(R.id.etLocCod);
		etLocNome = (EditText) findViewById(R.id.etLocNome);
		etAgeId = (EditText) findViewById(R.id.etAgeId);
		etAgeNome = (EditText) findViewById(R.id.etAgeNome);
		etSemEpidNum = (EditText) findViewById(R.id.etSemEpidNum);
		etSemEpidAno = (EditText) findViewById(R.id.etSemEpidAno);
		etNAtivNum = (EditText) findViewById(R.id.etNAtivNum);
		etNAtivAno = (EditText) findViewById(R.id.etNAtivAno);
		etData = (EditText) findViewById(R.id.etData);
		etId = (EditText) findViewById(R.id.etIdBol);
		etTurma = (EditText) findViewById(R.id.etTurma);
		btVerMun = (Button)findViewById(R.id.btVerMun);
		btVerLoc = (Button)findViewById(R.id.btVerLoc);
		btCadAlt = (Button)findViewById(R.id.btCadAlt);
		btLimpar = (Button)findViewById(R.id.btLimpar);
		spCat = (Spinner)findViewById(R.id.spCat);
		spTipAtv = (Spinner)findViewById(R.id.spTpAtiv);

		etNAtivAno.setText(Datas.getYear().toString());
		etSemEpidAno.setText(Datas.getYear().toString());

		addListeners();

		getParcel();

		return false;
	}

	@Override
	public boolean addListeners() {

		btVerMun.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				municipio = verificarMunicipio(etMunCod, etMunNome, etMunNome, etLocCod, btVerLoc);
				etLocCod.setText("");
				etLocNome.setText("");
				setTurmaGrs();
			}
		});

		btVerLoc.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				localidade = verificarLocalidade(municipio.getIdMunicipio(), etLocCod, etLocNome, etLocNome);
			}
		});

		btCadAlt.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				cadastrarAlterar();
			}
		});

		btLimpar.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				limparGui();
			}
		});

		return true;
	}

	/**
	 * Captura o objeto enviado pela activity anterior através de parcelable 
	 * @return
	 */
	public abstract boolean getParcel();

	/**
	 * Preenche os componentes da Activity de acordo com o objeto enviado por parâmetro
	 * @return
	 */
	public abstract boolean preencheGui(Object obj);

	/**
	 * Seta os componentes da Gui de acordo como o estado de @ativa
	 * @param ativa
	 * @return
	 */
	public boolean guiAtiva(boolean ativa){

		if(ativa){

			Componentes.enabledJComponent(etGrsNome, etMunCod, btVerMun, etMunNome, etLocCod, btVerLoc, etLocNome,
					btCadAlt);

		} else {

			Componentes.disabledJComponent(etGrsNome, etMunCod, btVerMun, etMunNome, etLocCod, btVerLoc, etLocNome,
					btCadAlt);

		}

		return true;
	}

	/**
	 * Limpa os componentes da activity
	 * @return
	 */
	public boolean limparGui(){

		Componentes.apagaEditText(etCoReg, etGrsNome, etMunCod, etMunNome, etLocCod, etLocNome,
				etSemEpidNum, etSemEpidAno, etNAtivNum, etNAtivAno, etTurma);
		Componentes.setSelectedItemOnSpinner(spCat, "");
		Componentes.setSelectedItemOnSpinner(spTipAtv, "");

		municipio = null;
		localidade = null;

		guiAtiva(false);

		return true;
	}

	/**
	 * Abre a activity de visitas do boletim atual
	 * @return
	 */
	public abstract boolean visitas();
	
	
	/**
	 * Insere o valor da turma na activity
	 * @return
	 */
	public boolean setTurmaGrs(){

		if(etMunCod.getText().length() > 6){
			String turma = etMunCod.getText().toString().substring(2, (etMunCod.getText().toString().length() - 1));
			etTurma.setText(turma);
			
			if(municipio != null){
				if(municipio.getEstado() != null){
					etCoReg.setText(municipio.getEstado());
					
				} else {
					etGrsNome.setText("");
				}
			} else {
				etCoReg.setText("");
			}

		} else {
			etTurma.setText("");
			etCoReg.setText("");

		}

		return true;
	}

	/**
	 * Cadastra ou altera o boletim visualizado na activity
	 */
	public abstract boolean cadastrarAlterar();
	

	/**
	 * Captura as informações inseridas nos componentes da Activity e instancia um novo boletim
	 * @return
	 */
	public abstract Object getObjeto();

	/**
	 * Verifica se foram preenchidas todas as informações mínimas para cadastro/alteração do boletim 
	 * @return
	 */
	public boolean verificaInformacoesMinimas(){

		if (!verificaPreenchido("Coordenador Regional não preenchido.", etCoReg)){
			return false;
		}

		if (!verificaPreenchido("GRS não definido.", etGrsNome)){
			return false;
		}

		if (!verificaPreenchido("Município não preenchido.", etMunCod, etMunNome)){
			return false;
		}

		if (!verificaPreenchido("Localidade não preenchida.", etLocCod, etLocNome)){
			return false;
		}

		if (!verificaPreenchido("Semana Epid. não preenchida.", etSemEpidNum, etSemEpidAno)){
			return false;
		}

		if (!verificaPreenchido("Nº da Atividade não preenchida.", etNAtivNum, etNAtivAno)){
			return false;
		}

		if (!verificaPreenchido("Categoria não preenchida.", spCat)){
			return false;
		}

		if (!verificaPreenchido("Tipo da Atividade não preenchida.", spTipAtv)){
			return false;
		}

		if (!verificaPreenchido("Data não preenchida.", etData)){
			return false;
		}

		if (!verificaPreenchido("Agente não preenchido.", etAgeId, etAgeNome)){
			return false;
		}
		
		if(Integer.parseInt(etNAtivNum.getText().toString()) > 53){
			toast("O numero da semana da atividade deve ser no máximo 53");
			return false;
		}
		
		if(Integer.parseInt(etSemEpidNum.getText().toString()) > 53){
			toast("O numero da semana da epid. deve ser no máximo 53");
			return false;
		}

		return true;
	}




}
